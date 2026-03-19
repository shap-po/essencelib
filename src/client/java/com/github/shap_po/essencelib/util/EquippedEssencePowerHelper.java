package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Shared client helper for checking visible powers from equipped essence trinkets.
 */
@Environment(EnvType.CLIENT)
public final class EquippedEssencePowerHelper {

    public static boolean hasVisibleEquippedPower(PlayerEntity player, Identifier powerId) {
        if (player == null || powerId == null) return false;
        Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(player);
        if (opt.isEmpty()) return false;

        for (var group : opt.get().getInventory().values()) {
            for (var inv : group.values()) {
                for (int i = 0; i < inv.size(); i++) {
                    ItemStack stack = inv.getStack(i);
                    if (stack.isEmpty() || !(stack.getItem() instanceof MobEssenceTrinketItem)) continue;
                    var powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
                    if (powers == null) continue;
                    var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
                    for (var entry : entries) {
                        if (entry.hidden()) continue;
                        if (powerId.equals(entry.powerId())) return true;
                    }
                }
            }
        }
        return false;
    }

    private EquippedEssencePowerHelper() {}
}
