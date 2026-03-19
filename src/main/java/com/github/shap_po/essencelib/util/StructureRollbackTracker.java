package com.github.shap_po.essencelib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tracks per-player structure edits (place/break) and supports rollback.
 * "Structure-only" means we only track air <-> non-air transitions.
 */
public final class StructureRollbackTracker {

    private static final int MAX_HISTORY_PER_PLAYER = 20000;
    private static final Map<UUID, Deque<BlockChange>> HISTORY = new HashMap<>();
    private static final Set<UUID> APPLYING_ROLLBACK = new HashSet<>();
    private static final Set<UUID> LOADED = new HashSet<>();

    private StructureRollbackTracker() {}

    public static boolean isApplyingRollback(ServerPlayerEntity player) {
        return APPLYING_ROLLBACK.contains(player.getUuid());
    }

    public static void record(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        if (player == null || world == null || pos == null || oldState == null || newState == null) return;
        if (oldState == newState) return;
        // Structure edits only: place or break style transitions.
        if (oldState.isAir() == newState.isAir()) return;

        Deque<BlockChange> history = getHistory(world.getServer(), player.getUuid());
        NbtCompound oldStateTag = serializeBlockState(oldState);
        NbtCompound newStateTag = serializeBlockState(newState);
        NbtCompound oldBeTag = snapshotBlockEntityNbt(world, pos, oldState);
        NbtCompound newBeTag = snapshotBlockEntityNbt(world, pos, newState);
        history.addLast(new BlockChange(world.getRegistryKey().getValue(), pos.toImmutable(), oldStateTag, newStateTag, oldBeTag, newBeTag));
        while (history.size() > MAX_HISTORY_PER_PLAYER) {
            history.removeFirst();
        }
        saveHistory(world.getServer(), player.getUuid(), history);
    }

    public static int rollback(ServerPlayerEntity target, int count) {
        if (target == null || count <= 0) return 0;
        MinecraftServer server = target.getServer();
        if (server == null) return 0;
        Deque<BlockChange> history = getHistory(server, target.getUuid());
        if (history.isEmpty()) return 0;

        APPLYING_ROLLBACK.add(target.getUuid());
        int applied = 0;
        try {
            while (applied < count && !history.isEmpty()) {
                BlockChange change = history.removeLast();
                RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, change.worldId());
                ServerWorld world = server.getWorld(worldKey);
                if (world == null) {
                    continue;
                }
                BlockState restoreState = deserializeBlockState(change.oldStateTag());
                world.setBlockState(change.pos(), restoreState, Block.NOTIFY_ALL);
                restoreBlockEntityNbt(world, change.pos(), restoreState, change.oldBlockEntityNbt());
                applied++;
            }
        } finally {
            APPLYING_ROLLBACK.remove(target.getUuid());
        }
        saveHistory(server, target.getUuid(), history);
        return applied;
    }

    public static int rollbackAll(ServerPlayerEntity target) {
        if (target == null) return 0;
        MinecraftServer server = target.getServer();
        if (server == null) return 0;
        Deque<BlockChange> history = getHistory(server, target.getUuid());
        if (history.isEmpty()) return 0;
        return rollback(target, history.size());
    }

    private static Deque<BlockChange> getHistory(MinecraftServer server, UUID playerId) {
        Deque<BlockChange> history = HISTORY.computeIfAbsent(playerId, k -> new ArrayDeque<>());
        if (!LOADED.contains(playerId)) {
            loadHistory(server, playerId, history);
            LOADED.add(playerId);
        }
        return history;
    }

    private static Path getPlayerHistoryPath(MinecraftServer server, UUID playerId) {
        return server.getSavePath(WorldSavePath.ROOT)
            .resolve("essencelib")
            .resolve("rollback")
            .resolve(playerId + ".dat");
    }

    private static void saveHistory(MinecraftServer server, UUID playerId, Deque<BlockChange> history) {
        try {
            Path path = getPlayerHistoryPath(server, playerId);
            Files.createDirectories(path.getParent());
            NbtCompound root = new NbtCompound();
            NbtList list = new NbtList();
            for (BlockChange change : history) {
                NbtCompound entry = new NbtCompound();
                entry.putString("world", change.worldId().toString());
                entry.putInt("x", change.pos().getX());
                entry.putInt("y", change.pos().getY());
                entry.putInt("z", change.pos().getZ());
                entry.put("old_state", change.oldStateTag().copy());
                entry.put("new_state", change.newStateTag().copy());
                if (change.oldBlockEntityNbt() != null) entry.put("old_be", change.oldBlockEntityNbt().copy());
                if (change.newBlockEntityNbt() != null) entry.put("new_be", change.newBlockEntityNbt().copy());
                list.add(entry);
            }
            root.put("changes", list);
            try (OutputStream out = Files.newOutputStream(path)) {
                NbtIo.writeCompressed(root, out);
            }
        } catch (Exception ignored) {
            // Keep rollback runtime-safe even if disk persistence fails.
        }
    }

    private static void loadHistory(MinecraftServer server, UUID playerId, Deque<BlockChange> out) {
        out.clear();
        try {
            Path path = getPlayerHistoryPath(server, playerId);
            if (!Files.exists(path)) return;
            NbtCompound root;
            try (InputStream in = Files.newInputStream(path)) {
                root = NbtIo.readCompressed(in, NbtSizeTracker.ofUnlimitedBytes());
            }
            if (root == null || !root.contains("changes")) return;
            NbtList list = root.getList("changes", NbtCompound.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound entry = list.getCompound(i);
                Identifier worldId = Identifier.tryParse(entry.getString("world"));
                if (worldId == null) continue;
                BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
                NbtCompound oldState = entry.getCompound("old_state");
                NbtCompound newState = entry.getCompound("new_state");
                NbtCompound oldBe = entry.contains("old_be") ? entry.getCompound("old_be") : null;
                NbtCompound newBe = entry.contains("new_be") ? entry.getCompound("new_be") : null;
                out.addLast(new BlockChange(worldId, pos, oldState, newState, oldBe, newBe));
            }
            while (out.size() > MAX_HISTORY_PER_PLAYER) out.removeFirst();
        } catch (Exception ignored) {
            out.clear();
        }
    }

    private static NbtCompound serializeBlockState(BlockState state) {
        NbtCompound out = new NbtCompound();
        out.putString("block", Registries.BLOCK.getId(state.getBlock()).toString());
        NbtCompound props = new NbtCompound();
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
            Property<?> prop = entry.getKey();
            Comparable<?> value = entry.getValue();
            props.putString(prop.getName(), valueToString(prop, value));
        }
        out.put("props", props);
        return out;
    }

    private static BlockState deserializeBlockState(NbtCompound tag) {
        Identifier blockId = Identifier.tryParse(tag.getString("block"));
        Optional<Block> blockOpt = blockId == null ? Optional.empty() : Registries.BLOCK.getOrEmpty(blockId);
        Block block = blockOpt.orElse(Blocks.AIR);
        BlockState state = block.getDefaultState();
        if (tag.contains("props")) {
            NbtCompound props = tag.getCompound("props");
            for (String key : props.getKeys()) {
                Property<?> property = block.getStateManager().getProperty(key);
                if (property == null) continue;
                state = applyProperty(state, property, props.getString(key));
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        Optional<T> parsed = property.parse(value);
        if (parsed.isPresent()) {
            return state.with(property, parsed.get());
        }
        return state;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static String valueToString(Property property, Comparable value) {
        return property.name(value);
    }

    @Nullable
    private static NbtCompound snapshotBlockEntityNbt(ServerWorld world, BlockPos pos, BlockState state) {
        if (!state.hasBlockEntity()) return null;
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return null;
        return be.createNbtWithIdentifyingData(world.getRegistryManager());
    }

    private static void restoreBlockEntityNbt(ServerWorld world, BlockPos pos, BlockState state, @Nullable NbtCompound nbt) {
        if (!state.hasBlockEntity() || nbt == null) return;
        BlockEntity restored = BlockEntity.createFromNbt(pos, state, nbt, world.getRegistryManager());
        if (restored == null) return;
        world.removeBlockEntity(pos);
        world.addBlockEntity(restored);
        restored.markDirty();
    }

    private record BlockChange(
        Identifier worldId,
        BlockPos pos,
        NbtCompound oldStateTag,
        NbtCompound newStateTag,
        @Nullable NbtCompound oldBlockEntityNbt,
        @Nullable NbtCompound newBlockEntityNbt
    ) {}
}
