package com.github.shap_po.essencelib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/** Client helper for Collector's Rush (hoarder_lifestyle) - the lifestyle ability. */
@Environment(EnvType.CLIENT)
public final class CollectorsRushHelper {

    /** Returns true if the player has Collector's Rush (hoarder_lifestyle) equipped. */
    public static boolean hasCollectorsRush(PlayerEntity player) {
        Identifier powerId = ClientAbilityHookConfig.get().collectorsRushPower();
        return EquippedEssencePowerHelper.hasVisibleEquippedPower(player, powerId);
    }

    private CollectorsRushHelper() {}
}
