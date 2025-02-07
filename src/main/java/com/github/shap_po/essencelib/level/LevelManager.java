package com.github.shap_po.essencelib.level;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.registry.ModTags;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LevelManager implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = EssenceLib.identifier("level");
    public static final int MAX_LEVEL = 10;

    private static final List<Integer> REQUIRED_KILLS = new ArrayList<>();
    private static @Nullable Integer totalEntityCount = null;

    public static int getRequiredKills(int level) {
        return REQUIRED_KILLS.get(level - 1);
    }

    public static int getLevelFromKills(int uniqueKills) {
        for (int i = 0; i < REQUIRED_KILLS.size(); i++) {
            if (uniqueKills < REQUIRED_KILLS.get(i)) {
                return i;
            }
        }
        return MAX_LEVEL;
    }

    public static int getLevel(PlayerEntity player) {
        return getLevelFromKills(getCurrentUniqueKillsCount(player));
    }

    /**
     * Gets the total number of unique entities in the game that aren't in the ignore list
     *
     * @return the total number of unique entities
     */
    public static int getTotalEntityCount() {
        if (totalEntityCount == null) {
            totalEntityCount = Registries.ENTITY_TYPE.stream()
                .filter(e -> !e.isIn(ModTags.ENTITY_IGNORELIST))
                .mapToInt(entityType -> 1)
                .sum();
        }
        return totalEntityCount;
    }

    /**
     * Gets the current number of unique entities the player has killed
     *
     * @param player the player
     * @return the current number of unique kills
     */
    public static int getCurrentUniqueKillsCount(PlayerEntity player) {
        return LevelComponent.getOptional(player)
            .map(LevelComponent::getUniqueKills)
            .map(k -> k.stream()
                .map(Registries.ENTITY_TYPE::getOrEmpty)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(e -> !e.isIn(ModTags.ENTITY_IGNORELIST))
                .mapToInt(entityType -> 1)
                .sum()
            )
            .orElse(0);
    }

    @Override
    public void reload(ResourceManager manager) {
        totalEntityCount = null; // reset count so it will be recalculated
        setUpRequiredKills();
        EssenceLib.LOGGER.info("Reloaded leveling data.");
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    private void setUpRequiredKills() {
        REQUIRED_KILLS.clear();
        REQUIRED_KILLS.addAll(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)); // TODO: calculate by formula
    }
}
