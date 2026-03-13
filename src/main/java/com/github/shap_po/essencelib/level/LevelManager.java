package com.github.shap_po.essencelib.level;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.collector.CollectorRushCapacity;
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
        totalEntityCount = null;
        CollectorRushCapacity.invalidateCache();
        setUpRequiredKills();
        EssenceLib.LOGGER.info("Reloaded leveling data.");
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    private void setUpRequiredKills() {
        REQUIRED_KILLS.clear();
        int total = getTotalEntityCount();
        
        // We set the maximum level requirement to 80% of the total available entities.
        // This ensures players don't need to kill literally every single entity (some might be unobtainable or boss-only).
        double maxRequirement = total * 0.80;
        
        // First level requires 5 or more kills, scaling with total mob count (e.g. ~5% of mobs, min 5)
        int firstLevelKills = Math.max(5, (int) Math.ceil(total * 0.05));

        for (int i = 1; i <= MAX_LEVEL; i++) {
            if (i == 1) {
                REQUIRED_KILLS.add(firstLevelKills);
            } else {
                double fraction = (double) (i - 1) / (MAX_LEVEL - 1);
                // Squaring the fraction gives a curve that requires less kills early on and scales up steeply later.
                int requiredKills = (int) Math.round(maxRequirement * Math.pow(fraction, 2.0));
                
                // Ensure strictly increasing required kills so you don't get stuck at levels
                int previous = REQUIRED_KILLS.get(i - 2);
                if (requiredKills <= previous) {
                    requiredKills = previous + 1;
                }
                
                REQUIRED_KILLS.add(requiredKills);
            }
        }
        
        EssenceLib.LOGGER.info("Calculated required kills for leveling: {}", REQUIRED_KILLS);
    }
}
