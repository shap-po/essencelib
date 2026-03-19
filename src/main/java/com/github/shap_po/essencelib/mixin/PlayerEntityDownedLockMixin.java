package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.util.DownedStateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDownedLockMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockAttackWhenDowned(Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (!DownedComponent.isDowned(self)) {
            return;
        }
        DownedStateHelper.sendDownedActionbar(self);
        ci.cancel();
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockDropWhenDowned(ItemStack stack, boolean throwRandomly, boolean retainOwnership,
                                                CallbackInfoReturnable<ItemEntity> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (!DownedComponent.isDowned(self)) {
            return;
        }
        DownedStateHelper.sendDownedActionbar(self);
        cir.setReturnValue(null);
        cir.cancel();
    }

    @Inject(method = "isImmobile", at = @At("HEAD"), cancellable = true)
    private void essencelib$lockMovementWhenDowned(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (!DownedComponent.isDowned(self)) {
            return;
        }
        cir.setReturnValue(true);
        cir.cancel();
    }
}
