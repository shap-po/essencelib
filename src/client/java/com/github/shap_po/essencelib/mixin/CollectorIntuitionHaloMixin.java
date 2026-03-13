package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.mixin.client.ItemEntityAccessor;
import com.github.shap_po.essencelib.mixin.client.ItemEntityRendererAccessor;
import com.github.shap_po.essencelib.render.CollectorIntuitionRenderLayers;
import com.github.shap_po.essencelib.render.HaloVertexConsumerProvider;
import com.github.shap_po.essencelib.util.CollectorIntuitionOutlineHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**
 * Draws a halo (scaled-up item model with glow color) for Collector's Intuition.
 * Uses HEAD injection (INVOKE fails with Loot Beams). Replicates vanilla translate/rotate/scale
 * so the halo moves and spins with the item.
 */
@Mixin(ItemEntityRenderer.class)
public abstract class CollectorIntuitionHaloMixin {

    @Inject(
        method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD")
    )
    private void essencelib$drawHaloBeforeItem(ItemEntity entity, float yaw, float tickDelta,
        MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        var player = MinecraftClient.getInstance().player;
        if (player == null || !CollectorIntuitionOutlineHelper.hasCollectorsIntuition(player) || !CollectorIntuitionOutlineHelper.isInRange(player, entity)) return;

        var stack = entity.getStack();
        if (stack.isEmpty()) return;

        var accessor = (ItemEntityRendererAccessor) this;

        int argb = CollectorIntuitionOutlineHelper.getGlowColor(player, stack);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int a = 255; // Fully opaque, no transparency

        float u = ((ItemEntityAccessor) entity).essencelib$getUniqueOffset();
        float age = entity.getItemAge() + tickDelta;

        matrices.push();
        // Match vanilla ItemEntityRenderer: translate + bob + rotate + scale
        matrices.translate(0.0, 0.25, 0.0);
        matrices.translate(0.0, Math.sin(age / 20.0 + u * Math.PI * 2) * 0.1, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
            age / 20.0f * 360.0f + u * 360.0f));
        matrices.scale(1.0f, 1.0f, 1.0f);   // Base 1
        matrices.scale(1.25f, 1.25f, 1.25f); // Halo slightly larger

        VertexConsumerProvider haloConsumers = new HaloVertexConsumerProvider(
            vertexConsumers, CollectorIntuitionRenderLayers.GLOW_HALO, r, g, b, a);

        ItemEntityRenderer.renderStack(
            accessor.essencelib$getItemRenderer(),
            matrices,
            haloConsumers,
            15728880, // Full bright so the glow doesn't darken in shadows
            stack,
            accessor.essencelib$getRandom(),
            entity.getWorld()
        );

        matrices.pop();
    }
}
