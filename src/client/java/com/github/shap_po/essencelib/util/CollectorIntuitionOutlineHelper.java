package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.collector.CollectorRushHelper;
import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** Checks if the player has Collector's Intuition (item_whisperer) from Allay essence. */
@Environment(EnvType.CLIENT)
public final class CollectorIntuitionOutlineHelper {

    private static final Identifier ITEM_WHISPERER = Identifier.of("esspack", "allay_essence/item_whisperer");
    private static final double RANGE_SQ = 30.0 * 30.0;

    /** Returns true if the player has an equipped essence granting item_whisperer (Collector's Intuition). */
    public static boolean hasCollectorsIntuition(PlayerEntity player) {
        if (player == null) return false;
        Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(player);
        if (opt.isEmpty()) return false;

        for (var group : opt.get().getInventory().values()) {
            for (var inv : group.values()) {
                for (int i = 0; i < inv.size(); i++) {
                    ItemStack s = inv.getStack(i);
                    if (s.isEmpty() || !(s.getItem() instanceof com.github.shap_po.essencelib.item.MobEssenceTrinketItem))
                        continue;
                    var powers = s.get(com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
                    if (powers == null) continue;
                    var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
                    for (var e : entries) {
                        if (e.hidden()) continue;
                        if (ITEM_WHISPERER.equals(e.powerId()))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isInRange(PlayerEntity player, net.minecraft.entity.Entity entity) {
        if (player == null || entity == null) return false;
        return player.squaredDistanceTo(entity) <= RANGE_SQ;
    }

    /** Returns glow color (ARGB) for item - collected vs uncollected, from CollectorRushComponent list. */
    public static int getGlowColor(PlayerEntity player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) return EssenceOutlineColors.getUncollectedArgb();
        return CollectorRushHelper.isCollected(player, stack)
            ? EssenceOutlineColors.getCollectedArgb()
            : EssenceOutlineColors.getUncollectedArgb();
    }

    private CollectorIntuitionOutlineHelper() {}
}
