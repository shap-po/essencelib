package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private long sneakStartTime = -1;

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        ItemStack stack = itemEntity.getStack();

        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            if (player.isSneaking()) {
                if (sneakStartTime == -1) {
                    sneakStartTime = System.currentTimeMillis();
                } else {
                    long elapsed = System.currentTimeMillis() - sneakStartTime;
                    if (elapsed >= 5000) {
                        // Try to equip the item
                        if (TrinketsApi.getTrinketComponent(player).get().isEquipped(stack.getItem())) {
                            itemEntity.remove(Entity.RemovalReason.DISCARDED);
                        } else {
                            // Drop the item if it cannot be equipped
                            ci.cancel();
                        }
                        ci.cancel();
                        return;
                    } else {
                        int secondsLeft = 5 - (int) (elapsed / 1000);
                        player.sendMessage(Text.literal("Shift to equip in " + secondsLeft + " seconds").formatted(Formatting.YELLOW), true);
                    }
                }
            } else {
                sneakStartTime = -1;
            }
            ci.cancel();
        }
    }

    @Inject(method = "canMerge", at = @At("HEAD"), cancellable = true)
    private void canMerge(CallbackInfoReturnable<Boolean> cir) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        ItemStack stack = itemEntity.getStack();

        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            cir.setReturnValue(false);
        }
    }
}


