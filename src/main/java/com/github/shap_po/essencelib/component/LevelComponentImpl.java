package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.level.LevelManager;
import com.github.shap_po.shappoli.integration.trinkets.util.TrinketsSlotModifierUtil;
import com.google.common.collect.ImmutableSet;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class LevelComponentImpl implements LevelComponent {
    private final PlayerEntity provider;
    private final Set<Identifier> uniqueKills = new HashSet<>();
    private int level = 1;

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
    public void addUniqueKill(Identifier id) {
        if (uniqueKills.contains(id)) {
            return;
        }
        uniqueKills.add(id);

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

    private void handleLevelChange(boolean isLevelUp) {
        if (!(provider instanceof ServerPlayerEntity player)) return;

        TrinketInventory trinketInventory = getTrinketInventory();
        if (trinketInventory == null) {
            EssenceLib.LOGGER.warn("Could not level-up player {}. No trinket inventory found.", player.getName().getString());
            return;
        }
        // Add an essence/soul slot
        setSlotCount(trinketInventory, level - 1);

        if (!isLevelUp) {
            return;
        }

        int uniqueKills = LevelManager.getCurrentUniqueKillsCount(player);

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
        return TrinketsApi.getTrinketComponent(provider).map(
            comp -> comp.getInventory()
                .getOrDefault("soul", null)
                .getOrDefault("essence", null)
        ).orElse(null);
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

        level = compoundTag.getInt("level");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound compoundTag, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = new NbtList();

        for (Identifier id : uniqueKills) {
            list.add(NbtString.of(id.toString()));
        }

        compoundTag.put("unique_kills", list);
        compoundTag.putInt("level", level);
    }

    @Override
    public void sync() {
        KEY.sync(this.provider);
    }
}
