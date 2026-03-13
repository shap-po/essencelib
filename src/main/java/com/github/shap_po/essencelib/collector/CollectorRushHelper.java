package com.github.shap_po.essencelib.collector;

import com.github.shap_po.essencelib.component.CollectorRushComponent;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.PowerUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * Central helper for Collector's Rush data.
 * Use this to pull collected items instead of accessing the component directly.
 */
public final class CollectorRushHelper {

    private static final Identifier HOARDER_COLLECTION_CHARGE =
        Identifier.of("esspack", "allay_essence/hoarder_lifestyle_collection_charge");

    /**
     * Triggers Collector's Rush (charge, mana, particle) only for NEW items.
     * Call when addCollectedItem returns true.
     */
    public static void triggerRushForNewItemPickup(@Nullable PlayerEntity player, ItemStack stack) {
        if (player == null || player.getWorld().isClient() || stack == null || stack.isEmpty()) return;

        Power power = PowerManager.getNullable(HOARDER_COLLECTION_CHARGE);
        if (power == null) return;

        PowerType powerType = PowerUtil.getNullablePowerType(power, player);
        if (powerType == null) return;

        int current = PowerUtil.getResourceValue(powerType);
        if (current >= 600) return;

        PowerUtil.setResourceValue(powerType, 600);

        if (player instanceof LivingEntity living) {
            EntityAttributeInstance mana = living.getAttributeInstance(ManaAttributeRegistry.getCurrentManaEntry());
            if (mana != null) {
                double max = living.getAttributeValue(ManaAttributeRegistry.getMaxManaEntry());
                mana.setBaseValue(Math.min(max, mana.getBaseValue() + 15));
            }
        }

        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        long worldTime = player.getWorld().getTime();
        if (player.getServer() != null) {
            player.getServer().getCommandManager().executeWithPrefix(
                player.getCommandSource().withSilent(),
                "data modify storage esspack:memory RecentItems append value {id:\"" + itemId + "\",Time:" + worldTime + "}"
            );
        }
    }

    /**
     * Returns true if the player has collected this item type (for Collector's Rush).
     */
    public static boolean isCollected(@Nullable PlayerEntity player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) return false;
        var comp = CollectorRushComponent.getNullable(player);
        return comp != null && comp.hasCollectedItem(stack);
    }

    /**
     * Returns true if the player has collected this item ID.
     */
    public static boolean isCollected(@Nullable PlayerEntity player, Identifier itemId) {
        if (player == null || itemId == null) return false;
        var comp = CollectorRushComponent.getNullable(player);
        return comp != null && comp.hasCollectedItem(itemId);
    }

    /**
     * Returns the set of collected item IDs for the player.
     * Returns empty set if player or component is null.
     */
    public static Set<String> getCollectedItemIds(@Nullable PlayerEntity player) {
        if (player == null) return Collections.emptySet();
        var comp = CollectorRushComponent.getNullable(player);
        return comp != null ? comp.getCollectedItemIds() : Collections.emptySet();
    }

    private CollectorRushHelper() {}
}
