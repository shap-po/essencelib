package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.power.PowerManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Client helper for essence keybind logic. Checks if essences have Active powers
 * and which equipped slots have Active essence.
 */
@Environment(EnvType.CLIENT)
public final class ActiveEssenceHelper {

    /**
     * Returns true if this essence stack has at least one Active power.
     */
    public static boolean hasActivePower(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MobEssenceTrinketItem)) return false;
        TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
        if (powers == null) return false;
        var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
        for (var e : entries) {
            if (!e.hidden() && PowerManager.getNullable(e.powerId()) != null
                && PowerTooltipSlotRegistry.SLOT_ACTIVE.equals(PowerTooltipSlotRegistry.getSlot(e.powerId()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the slot indices (0-based) of equipped essence slots that have an Active power.
     * Only considers soul.essence slots.
     */
    public static List<Integer> getEquippedSlotsWithActivePower(PlayerEntity player) {
        List<Integer> out = new ArrayList<>();
        TrinketsApi.getTrinketComponent(player).ifPresent(comp -> {
            var essenceGroup = comp.getInventory().getOrDefault("soul", null);
            if (essenceGroup == null) return;
            var essenceInv = essenceGroup.getOrDefault("essence", null);
            if (essenceInv == null) return;
            for (int i = 0; i < essenceInv.size(); i++) {
                ItemStack s = essenceInv.getStack(i);
                if (!s.isEmpty() && hasActivePower(s)) {
                    out.add(i);
                }
            }
        });
        return out;
    }

    /**
     * Returns true if the player has at least one equipped essence with an Active power.
     */
    public static boolean hasAnyActiveEssenceEquipped(PlayerEntity player) {
        return !getEquippedSlotsWithActivePower(player).isEmpty();
    }
}
