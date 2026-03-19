package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.NuisanceAllayGloatable;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AllayEntity.class)
public abstract class AllayEntityMixin implements NuisanceAllayGloatable {

    @Unique
    private static final TrackedData<Boolean> ESSENCELIB_IS_GLOATING = DataTracker.registerData(AllayEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void essencelib$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(ESSENCELIB_IS_GLOATING, false);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void essencelib$enforceNuisanceOnLoad(NbtCompound nbt, CallbackInfo ci) {
        AllayEntity allay = (AllayEntity) (Object) this;
        if (allay.getWorld().isClient) return;
        NuisanceAllayHelper.ensureNuisanceTag(allay);
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void essencelib$tickNuisanceAllay(CallbackInfo ci) {
        AllayEntity allay = (AllayEntity) (Object) this;
        if (allay.getWorld().isClient) return;
        if ((allay.age + allay.getId()) % 40 == 0) {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H2B",
                "AllayEntityMixin#mobTick",
                "Allay common tick reached",
                "{\"entityId\":" + allay.getId() + ",\"age\":" + allay.age + "}"
            );
            // #endregion
        }

        if (NuisanceAllayHelper.isNuisance(allay)) {
            allay.getDataTracker().set(ESSENCELIB_IS_GLOATING, NuisanceAllayHelper.isGloating(allay));
        }
        else {
            allay.getDataTracker().set(ESSENCELIB_IS_GLOATING, false);
        }
    }

    @Override
    public void setNuisanceGloating(boolean gloating) {
        AllayEntity allay = (AllayEntity) (Object) this;
        allay.getDataTracker().set(ESSENCELIB_IS_GLOATING, gloating);
    }

    @Override
    public boolean isNuisanceGloating() {
        AllayEntity allay = (AllayEntity) (Object) this;
        return allay.getDataTracker().get(ESSENCELIB_IS_GLOATING);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void essencelib$tameNuisanceAllay(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        AllayEntity allay = (AllayEntity) (Object) this;
        if (allay.getWorld().isClient) return;
        if (!NuisanceAllayHelper.isNuisance(allay)) return;
        if (!NuisanceAllayHelper.isValuableTamingItem(player.getStackInHand(hand))) return;

        NuisanceAllayHelper.tameNuisanceAllay(allay, player, hand);
        cir.setReturnValue(ActionResult.SUCCESS);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void essencelib$panicSprintWhenHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        AllayEntity allay = (AllayEntity) (Object) this;
        if (allay.getWorld().isClient) return;
        NuisanceAllayHelper.onNuisanceDamaged(allay);
    }
}
