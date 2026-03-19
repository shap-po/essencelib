package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.render.ArmadilloShellFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void essencelib$addArmadilloShellFeature(CallbackInfo ci) {
        PlayerEntityRenderer self = (PlayerEntityRenderer) (Object) this;
        ((LivingEntityRendererFeatureAdder) self).essencelib$addFeature(new ArmadilloShellFeatureRenderer(self));
    }

    @Inject(
        method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void essencelib$renderBallOnlyWhenCurled(
        AbstractClientPlayerEntity player,
        float entityYaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (DownedComponent.isDowned(player)) {
            ci.cancel();
            return;
        }
        if (!ArmadilloShellFeatureRenderer.isBallUpVisualActive(player)) {
            return;
        }
        ArmadilloShellFeatureRenderer.renderBallModel(matrices, vertexConsumers, light, player, tickDelta);
        ci.cancel();
    }
}
