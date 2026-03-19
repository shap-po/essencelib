package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.config.ServerAbilityHookConfig;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.PowerUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

import java.util.Collection;

/**
 * Centralized cleanup utility for essence unequip events.
 * Automatically clears potion effects for ALL essences.
 * Note: Resources are automatically cleaned up by Apoli when powers are revoked.
 */
public class EssenceCleanupHelper {

    /**
     * Performs transient-state reset when an essence is equipped.
     * This prevents stale saved resources from auto-starting active powers.
     */
    public static void performEquipStateReset(ItemStack stack, LivingEntity entity, Collection<Power> powers) {
        if (!(stack.getItem() instanceof MobEssenceTrinketItem)) {
            return;
        }
        resetArmadilloBallStateIfPresent(entity);
    }
    
    /**
     * Performs cleanup when an essence trinket is unequipped.
     * Clears common potion effects that might persist after unequip.
     *
     * @param stack The ItemStack being unequipped
     * @param entity The entity unequipping the item
     * @param powers The powers that were granted by this item (before revocation)
     */
    public static void performCleanup(ItemStack stack, LivingEntity entity, Collection<Power> powers) {
        // Only perform cleanup for essence trinkets
        if (!(stack.getItem() instanceof MobEssenceTrinketItem)) {
            return;
        }
        
        resetArmadilloBallStateIfPresent(entity);

        // Clear common potion effects that might be applied by essence powers
        clearCommonEffects(entity);
    }

    /**
     * Explicitly resets Armadillo Ball Up state to prevent stale resource values from
     * resuming the active state when the essence is equipped again.
     */
    public static boolean resetArmadilloBallStateIfPresent(LivingEntity entity) {
        Power ballStatePower = PowerManager.getNullable(ServerAbilityHookConfig.get().armadilloBallStatePower());
        if (ballStatePower == null) {
            return false;
        }
        PowerType ballStateType = PowerUtil.getNullablePowerType(ballStatePower, entity);
        if (ballStateType == null) {
            return false;
        }
        if (PowerUtil.getResourceValue(ballStateType) != 0) {
            PowerUtil.setResourceValue(ballStateType, 0);
        }
        return true;
    }
    
    /**
     * Clears common potion effects that are typically applied by essence powers.
     */
    private static void clearCommonEffects(LivingEntity entity) {
        if (entity.hasStatusEffect(StatusEffects.SLOWNESS)) {
            entity.removeStatusEffect(StatusEffects.SLOWNESS);
        }
        if (entity.hasStatusEffect(StatusEffects.WEAKNESS)) {
            entity.removeStatusEffect(StatusEffects.WEAKNESS);
        }
        if (entity.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            entity.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        }
        if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
            entity.removeStatusEffect(StatusEffects.RESISTANCE);
        }
        if (entity.hasStatusEffect(StatusEffects.REGENERATION)) {
            entity.removeStatusEffect(StatusEffects.REGENERATION);
        }
        if (entity.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            entity.removeStatusEffect(StatusEffects.INVISIBILITY);
        }
        if (entity.hasStatusEffect(StatusEffects.SPEED)) {
            entity.removeStatusEffect(StatusEffects.SPEED);
        }
        if (entity.hasStatusEffect(StatusEffects.HASTE)) {
            entity.removeStatusEffect(StatusEffects.HASTE);
        }
    }
}
