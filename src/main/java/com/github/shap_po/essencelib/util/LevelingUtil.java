package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.component.UniqueKillsCounterComponent;
import com.github.shap_po.essencelib.registry.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;

import java.util.Optional;

public class LevelingUtil {
    public static int getTotalEntityCount() {
        return Registries.ENTITY_TYPE.stream()
            .filter(e -> !e.isIn(ModTags.ENTITY_IGNORELIST))
            .mapToInt(entityType -> 1)
            .sum();
    }

    public static int getCurrentEntityCount(PlayerEntity player) {
        return UniqueKillsCounterComponent.getOptional(player)
            .map(UniqueKillsCounterComponent::getUniqueKills)
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

    public static int getLevel(int uniqueKills, int totalEntities) {
        if (totalEntities <= 0) return 0;

        int level = 1;
        int baseRequirement = totalEntities / 20;

        while (uniqueKills >= baseRequirement) {
            uniqueKills -= baseRequirement;
            level++;
            baseRequirement = (int) Math.pow(level, 2) * (totalEntities / 100);
        }

        return level;
    }

    public static int getKillsToNextLevel(int currentLevel, int totalEntities) {
        if (totalEntities <= 0) return 0;

        return (int) Math.pow(currentLevel + 1, 2) * (totalEntities / 100);
    }
}
