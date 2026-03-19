package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.collector.CollectorRushHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/** Checks if the player has Collector's Intuition (item_whisperer) from Allay essence. */
@Environment(EnvType.CLIENT)
public final class CollectorIntuitionOutlineHelper {

    /** Returns true if the player has an equipped essence granting item_whisperer (Collector's Intuition). */
    public static boolean hasCollectorsIntuition(PlayerEntity player) {
        Identifier profileId = ClientAbilityHookConfig.get().collectorIntuitionDetectionProfile();
        return EntityDetectionComponentRegistry.hasDetector(player, profileId);
    }

    public static boolean isInRange(PlayerEntity player, Entity entity) {
        Identifier profileId = ClientAbilityHookConfig.get().collectorIntuitionDetectionProfile();
        return EntityDetectionComponentRegistry.canDetect(player, entity, profileId);
    }

    /**
     * Collector's Intuition follows stat-based scaling:
     * final_range = clamp(base_range + (collection_range * coefficient), min, max)
     */
    public static double getCollectorsIntuitionRange(PlayerEntity player) {
        Identifier profileId = ClientAbilityHookConfig.get().collectorIntuitionDetectionProfile();
        return EntityDetectionComponentRegistry.getRange(player, profileId);
    }

    /**
     * Returns glow color (ARGB) for item. Only differentiates collected vs uncollected when player
     * has BOTH Collector's Intuition AND Collector's Rush (rare). Otherwise uses single default color.
     */
    public static int getGlowColor(PlayerEntity player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) return EssenceOutlineColors.getDefaultArgb();
        if (CollectorsRushHelper.hasCollectorsRush(player)) {
            return CollectorRushHelper.isCollected(player, stack)
                ? EssenceOutlineColors.getCollectedArgb()
                : EssenceOutlineColors.getUncollectedArgb();
        }
        return EssenceOutlineColors.getDefaultArgb();
    }

    public static int getGlowColor(PlayerEntity player, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return getGlowColor(player, itemEntity.getStack());
        }
        return EssenceOutlineColors.getDefaultArgb();
    }

    private CollectorIntuitionOutlineHelper() {}
}
