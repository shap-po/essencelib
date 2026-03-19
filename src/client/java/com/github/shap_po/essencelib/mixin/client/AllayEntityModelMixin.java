package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.entity.NuisanceAllayGloatable;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.entity.passive.AllayEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When a Nuisance Allay is gloating (showing stolen item), applies a gloat/tease pose
 * (bob, head tilt, arm and wing motion) matching the Down Player "Gloat/ Tease" animation style.
 */
@Mixin(AllayEntityModel.class)
public class AllayEntityModelMixin {

    @Shadow
    @Final
    private ModelPart root;
    @Shadow
    @Final
    private ModelPart head;
    @Shadow
    @Final
    private ModelPart rightArm;
    @Shadow
    @Final
    private ModelPart leftArm;
    @Shadow
    @Final
    private ModelPart rightWing;
    @Shadow
    @Final
    private ModelPart leftWing;

    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0);
    private static final float GLOAT_BOB_AMPLITUDE = 7.0f * DEG_TO_RAD;
    private static final float GLOAT_HEAD_TILT = -20.0f * DEG_TO_RAD;
    private static final float GLOAT_ARM_EXTRA = -25.0f * DEG_TO_RAD;      // arms more forward
    private static final float GLOAT_WING_FLAP = 22.0f * DEG_TO_RAD;

    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/AllayEntity;FFFFF)V", at = @At("TAIL"))
    private void essencelib$applyGloatPose(AllayEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof NuisanceAllayGloatable gloatable) || !gloatable.isNuisanceGloating()) {
            return;
        }
        float t = (entity.age + animationProgress) * 0.26f;
        float bob = (float) (Math.sin(t * Math.PI * 2.0) * GLOAT_BOB_AMPLITUDE);
        float wingPhase = (float) Math.sin(t * Math.PI * 4.8) * GLOAT_WING_FLAP;
        float laughNod = (float) Math.sin(t * Math.PI * 6.2) * (3.5f * DEG_TO_RAD);

        root.pitch += bob;
        head.pitch += GLOAT_HEAD_TILT + laughNod;
        rightArm.pitch += GLOAT_ARM_EXTRA;
        leftArm.pitch += GLOAT_ARM_EXTRA;
        rightWing.pitch += wingPhase;
        leftWing.pitch += wingPhase;
    }
}
