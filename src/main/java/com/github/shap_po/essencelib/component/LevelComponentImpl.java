package com.github.shap_po.essencelib.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.level.LevelManager;
import com.github.shap_po.shappoli.integration.trinkets.util.TrinketsSlotModifierUtil;
import com.google.common.collect.ImmutableSet;

import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import com.github.shap_po.essencelib.networking.s2c.LevelUpToastS2CPacket;
import com.github.shap_po.essencelib.networking.s2c.UniqueKillToastS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class LevelComponentImpl implements LevelComponent {
    private final PlayerEntity provider;
    private final Set<Identifier> uniqueKills = new HashSet<>();
    private final Map<Identifier, Integer> killCounts = new HashMap<>();
    private int level = 0;

    public LevelComponentImpl(PlayerEntity provider) {
        this.provider = provider;
    }

    private static void setSlotCount(TrinketInventory trinketInventory, int count) {
        TrinketsSlotModifierUtil.setSlotCountModifierValue(trinketInventory, MODIFIER_ID, count);
    }

    @Override
    public ImmutableSet<Identifier> getUniqueKills() {
        return ImmutableSet.copyOf(uniqueKills);
    }

    @Override
    public int getKillCount(Identifier id) {
        return killCounts.getOrDefault(id, 0);
    }

    @Override
    public void addUniqueKill(Identifier id) {
        boolean isNew = uniqueKills.add(id);
        killCounts.merge(id, 1, Integer::sum);

        if (isNew && provider instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new UniqueKillToastS2CPacket(id));
        }

        updateLevel(false);
        sync();
    }

    @Override
    public void addUniqueKill(Entity entity) {
        addUniqueKill(getEntityId(entity));
    }

    @Override
    public void removeUniqueKill(Identifier id) {
        if (!uniqueKills.contains(id)) {
            return;
        }
        uniqueKills.remove(id);
        killCounts.remove(id);

        updateLevel(false);
        sync();
    }

    @Override
    public void removeUniqueKill(Entity entity) {
        removeUniqueKill(getEntityId(entity));
    }

    @Override
    public boolean hasUniqueKill(Identifier id) {
        return uniqueKills.contains(id);
    }

    @Override
    public boolean hasUniqueKill(Entity entity) {
        return hasUniqueKill(getEntityId(entity));
    }

    @Override
    public void clearUniqueKills() {
        if (uniqueKills.isEmpty()) {
            return;
        }
        uniqueKills.clear();
        killCounts.clear();

        updateLevel(false);
        sync();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void updateLevel(boolean shouldSync) {
        int newLevel = LevelManager.getLevel(provider);
        applySlotCount(newLevel);  // Always apply so level 0 = 0 slots
        if (newLevel == level) {
            return;
        }

        boolean isLevelUp = newLevel > level;
        level = newLevel;
        handleLevelChange(isLevelUp);

        if (shouldSync) {
            sync();
        }
    }

    private void applySlotCount(int level) {
        // Apply on both client and server so slot counts stay in sync (prevents InventoryS2CPacket
        // IndexOutOfBounds when server has more slots than client)
        if (provider.getWorld().isClient) return; // Client: do nothing, let server sync
        if (provider instanceof ServerPlayerEntity player && player.networkHandler == null) return; // Server: defer until joined
        TrinketInventory trinketInventory = getTrinketInventory();
        if (trinketInventory == null) return;
        // Slot has base amount 1; modifier adjusts: level 0 -> -1 (0 slots), level 1 -> 0 (1 slot), level 2 -> 1 (2 slots), etc.
        setSlotCount(trinketInventory, level - 1);

        // Force Trinkets screen-slot layout refresh so client and server agree on slot count immediately.
        if (provider instanceof ServerPlayerEntity serverPlayer
            && serverPlayer.playerScreenHandler instanceof TrinketPlayerScreenHandler trinketScreen) {
            trinketScreen.trinkets$updateTrinketSlots(false);
            serverPlayer.playerScreenHandler.sendContentUpdates();
        }
    }

    private void handleLevelChange(boolean isLevelUp) {
        if (!(provider instanceof ServerPlayerEntity player)) return;

        if (!isLevelUp) {
            return;
        }

        int uniqueKills = LevelManager.getCurrentUniqueKillsCount(player);

        // Send toast packet
        ServerPlayNetworking.send(player, new LevelUpToastS2CPacket(level));

        player.networkHandler.sendPacket(new TitleS2CPacket(
            Text.literal("Level Up!").formatted(Formatting.GOLD, Formatting.BOLD, Formatting.UNDERLINE)
        ));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(
            Text.literal("You are now level " + level).formatted(Formatting.YELLOW, Formatting.ITALIC)
        ));
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);


        // Show the next number required and their current kills
        if (level >= LevelManager.MAX_LEVEL) {
            player.sendMessage(Text.literal("You have reached the maximum level!"), false);
        } else {
            int nextRequiredKills = LevelManager.getRequiredKills(level + 1);
            player.sendMessage(Text.literal("You need " + nextRequiredKills + " unique kills to reach the next level. You currently have " + uniqueKills + " unique kills."), false);
        }
    }

    private @Nullable TrinketInventory getTrinketInventory() {
        return TrinketsApi.getTrinketComponent(provider)
            .map(comp -> {
                var inv = comp.getInventory();
                var soul = inv != null ? inv.get("soul") : null;
                return soul != null ? soul.get("essence") : null;
            })
            .orElse(null);
    }

    private Identifier getEntityId(Entity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType());
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider; // no need to sync with other players
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound compoundTag, RegistryWrapper.WrapperLookup lookup) {
        uniqueKills.clear();
        killCounts.clear();
        NbtList killsTag = compoundTag.getList("unique_kills", NbtElement.STRING_TYPE);

        for (int i = 0; i < killsTag.size(); i++) {
            String id = killsTag.getString(i);
            if (id == null) {
                continue;
            }

            Identifier identifier = Identifier.tryParse(id);
            if (identifier == null) {
                continue;
            }

            uniqueKills.add(identifier);
        }

        // Load kill counts (new format); fallback: 1 per unique kill for backward compat
        NbtCompound countsTag = compoundTag.getCompound("kill_counts");
        if (!countsTag.isEmpty()) {
            for (String key : countsTag.getKeys()) {
                Identifier identifier = Identifier.tryParse(key);
                if (identifier != null) {
                    killCounts.put(identifier, countsTag.getInt(key));
                }
            }
        } else {
            for (Identifier id : uniqueKills) {
                killCounts.put(id, 1);
            }
        }

        level = compoundTag.getInt("level");
        // Recalculate level from kills on load (e.g. new players with 0 kills → level 0)
        updateLevel(false);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound compoundTag, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = new NbtList();

        for (Identifier id : uniqueKills) {
            list.add(NbtString.of(id.toString()));
        }

        compoundTag.put("unique_kills", list);

        NbtCompound countsTag = new NbtCompound();
        for (Map.Entry<Identifier, Integer> e : killCounts.entrySet()) {
            countsTag.putInt(e.getKey().toString(), e.getValue());
        }
        compoundTag.put("kill_counts", countsTag);

        compoundTag.putInt("level", level);
    }

    @Override
    public void sync() {
        KEY.sync(this.provider);
    }
}
