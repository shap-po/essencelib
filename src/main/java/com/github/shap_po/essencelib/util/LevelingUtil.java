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
}
