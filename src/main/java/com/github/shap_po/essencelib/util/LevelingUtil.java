package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.component.UniqueKillsCounterComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;

public class LevelingUtil {
    public static int getTotalEntityCount() {
        return Registries.ENTITY_TYPE.size();
    }

    public static int getCurrentEntityCount(PlayerEntity player) {
        return UniqueKillsCounterComponent.getOptional(player).map(UniqueKillsCounterComponent::getUniqueKillsCount).orElse(0);
    }
}
