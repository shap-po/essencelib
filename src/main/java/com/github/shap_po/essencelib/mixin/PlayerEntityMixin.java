package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "equipStack", at = @At("HEAD"), cancellable = true)
    private void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            if (TrinketsApi.getTrinketComponent((PlayerEntity) (Object) this).get().isEquipped(stack.getItem())) {
                ci.cancel();
            } else {
                // Equip the item in the custom trinket slot
                TrinketsApi.getTrinketComponent((PlayerEntity) (Object) this).get().isEquipped(stack.getItem());
                ci.cancel();
            }
        }
    }
}