package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.config.ServerAbilityHookConfig;
import com.github.shap_po.essencelib.util.EssenceCleanupHelper;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import dev.emi.trinkets.api.SlotReference;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

/**
 * Makes trinket item-power source IDs unique per slot index, so powers from
 * multiple essence slots (e.g. soul/essence[0] and soul/essence[1]) do not collide.
 */
@Mixin(value = TrinketItemPowersComponent.class, remap = false)
public abstract class ShappoliTrinketItemPowersComponentMixin {

    @Inject(method = "onEquip", at = @At("TAIL"))
    private static void essencelib$onEquip(ItemStack stack, SlotReference slot, LivingEntity entity, CallbackInfo ci) {
        Collection<Power> granted = getGrantedPowers(stack);

        Identifier legacySource = Identifier.of("shappoli", "item/" + slot.getId());
        Identifier indexedSource = Identifier.of("shappoli", "item/" + slot.getId() + "/" + slot.index());

        // Always clear legacy source by source-id, even if stack component data is missing/empty.
        PowerHolderComponent.revokeAllPowersFromSource(entity, legacySource, true);
        // Also clear indexed source first to prevent duplicate/stale grants across slot refreshes.
        PowerHolderComponent.revokeAllPowersFromSource(entity, indexedSource, true);
        if (!granted.isEmpty()) {
            // Remap granted powers from legacy source to unique slot-indexed source.
            PowerHolderComponent.grantPowers(entity, Map.of(indexedSource, granted), true);
        }

        // Reset transient resource state so active powers only begin from explicit key input.
        EssenceCleanupHelper.performEquipStateReset(stack, entity, granted);
    }

    @Inject(method = "onUnequip", at = @At("TAIL"))
    private static void essencelib$onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity, CallbackInfo ci) {
        Collection<Power> granted = getGrantedPowers(stack);

        // Perform centralized cleanup for essence trinkets (resets resources, clears effects)
        EssenceCleanupHelper.performCleanup(stack, entity, granted);

        Identifier legacySource = Identifier.of("shappoli", "item/" + slot.getId());
        Identifier indexedSource = Identifier.of("shappoli", "item/" + slot.getId() + "/" + slot.index());
        // Revoke both source formats to prevent ghost powers from legacy or migrated source IDs.
        PowerHolderComponent.revokeAllPowersFromSource(entity, legacySource, true);
        // Ensure indexed source is always removed, even when stack power component cannot be read.
        PowerHolderComponent.revokeAllPowersFromSource(entity, indexedSource, true);
    }

    private static Collection<Power> getGrantedPowers(ItemStack stack) {
        TrinketItemPowersComponent powers = stack.getOrDefault(
            ShappoliTrinketsDataComponentTypes.TRINKET_POWERS,
            TrinketItemPowersComponent.DEFAULT
        );
        Identifier collectorRushCore = ServerAbilityHookConfig.get().collectorRushChargePower();
        boolean hasCollectorRushCore = powers.stream().anyMatch(entry -> collectorRushCore.equals(entry.powerId()));
        return powers.stream()
            .filter(entry -> !essencelib$isCollectorRushSupplemental(entry.powerId()) || hasCollectorRushCore)
            .map(entry -> PowerManager.getNullable(entry.powerId()))
            .filter(p -> p != null)
            .toList();
    }

    private static boolean essencelib$isCollectorRushSupplemental(Identifier powerId) {
        if (powerId == null) return false;
        if (!"esspack".equals(powerId.getNamespace())) return false;
        String path = powerId.getPath();
        return path.startsWith("allay_essence/hoarder_lifestyle_")
            && !path.endsWith("collection_charge");
    }
}
