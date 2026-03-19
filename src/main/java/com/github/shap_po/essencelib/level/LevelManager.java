package com.github.shap_po.essencelib.level;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.collector.CollectorRushCapacity;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.registry.ModTags;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LevelManager implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = EssenceLib.identifier("level");
    public static final int MAX_LEVEL = 10;

    private static final List<Integer> REQUIRED_KILLS = new ArrayList<>();
    private static @Nullable Integer totalEntityCount = null;

    public static int getRequiredKills(int level) {
        return REQUIRED_KILLS.get(level - 1);
    }

    /**
     * Converts total unique kills to level. Kills are cumulative: the kills that got you to level 1
     * count toward level 2, and so on. REQUIRED_KILLS[i] is the total kills needed to reach level i+1.
     */
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
     * Gets the total number of unique entities that can be killed (LivingEntity with health),
     * excluding the ignore list (e.g. players, armor stands).
     */
    public static int getTotalEntityCount() {
        if (totalEntityCount == null) {
            totalEntityCount = Registries.ENTITY_TYPE.stream()
                .filter(LevelManager::isKillableForLeveling)
                .mapToInt(entityType -> 1)
                .sum();
        }
        return totalEntityCount;
    }

    /**
     * Gets all entity types that count toward leveling. Uses the entity registry (not world entities)
     * so the list shows ALL killable mob types, not just those currently nearby.
     * Includes MobEntity types (spawnable mobs with health) excluding the ignore list.
     */
    public static Stream<EntityType<?>> getKillableEntityTypes() {
        return Registries.ENTITY_TYPE.stream()
            .filter(LevelManager::isKillableForLeveling)
            .sorted(Comparator.comparing(
                e -> Registries.ENTITY_TYPE.getId(e).toString(),
                String.CASE_INSENSITIVE_ORDER
            ));
    }

    /** True if this entity type is a LivingEntity (has health, can be killed) and not in the ignore list. */
    private static boolean isKillableForLeveling(EntityType<?> entityType) {
        if (entityType == EntityType.PLAYER || entityType == EntityType.ARMOR_STAND) return false;
        try {
            if (entityType.isIn(ModTags.ENTITY_IGNORELIST)) return false;
        } catch (Exception ignored) {
            // Tag may not be loaded yet; explicit exclusions above cover vanilla
        }
        Class<?> base = entityType.getBaseClass();
        if (base != null && LivingEntity.class.isAssignableFrom(base)) return true;
        // Fallback: spawn groups for mobs (MONSTER, CREATURE, etc.); MISC excludes armor stand, items
        if (entityType.getSpawnGroup() != SpawnGroup.MISC) return true;
        // Fallback: has spawn egg (spawnable mob)
        return SpawnEggItem.forEntity(entityType) != null;
    }

    /**
     * Gets the current number of unique entities the player has killed
     *
     * @param player the player
     * @return the current number of unique kills
     */
    /**
     * Gets the current total unique kills that count toward leveling. Kills carry over:
     * the same kills that unlocked level 1 count toward level 2, etc.
     */
    public static int getCurrentUniqueKillsCount(PlayerEntity player) {
        return LevelComponent.getOptional(player)
            .map(LevelComponent::getUniqueKills)
            .map(k -> k.stream()
                .map(Registries.ENTITY_TYPE::getOrEmpty)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(LevelManager::isKillableForLeveling)
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
        
        // 85% Goal: max level = 85% of total mobs. Each level = cumulative % of that 85% goal.
        // Level 1: 3.5%, L2: 8%, L3: 14%, L4: 21.5%, L5: 30.5%, L6: 41.5%, L7: 54.5%, L8: 68.5%, L9: 83.5%, L10: 100%
        // Linear growth weight ensures kills always go up (previous + 1 or more).
        double goalFraction = 0.85;
        double[] cumulativeGoalPercent = {
            0.035,   // Level 1:  3.5% of the 85% goal
            0.080,   // Level 2:  8% (3.5+4.5)
            0.140,   // Level 3: 14% (+6)
            0.215,   // Level 4: 21.5% (+7.5)
            0.305,   // Level 5: 30.5% (+9)
            0.415,   // Level 6: 41.5% (+11)
            0.545,   // Level 7: 54.5% (+13)
            0.685,   // Level 8: 68.5% (+14)
            0.835,   // Level 9: 83.5% (+15)
            1.000    // Level 10: 100% of goal (+16.5) - the hardest push
        };

        for (int i = 1; i <= MAX_LEVEL; i++) {
            double fraction = cumulativeGoalPercent[i - 1] * goalFraction;
            int requiredKills = Math.max(1, (int) Math.ceil(total * fraction));
            
            // Ensure strictly increasing: previous + 1 or more (linear growth)
            if (i > 1 && requiredKills <= REQUIRED_KILLS.get(i - 2)) {
                requiredKills = REQUIRED_KILLS.get(i - 2) + 1;
            }
            REQUIRED_KILLS.add(requiredKills);
        }
        
        EssenceLib.LOGGER.info("Calculated required kills for leveling (total={}, 85% goal): {}", total, REQUIRED_KILLS);
    }
}
