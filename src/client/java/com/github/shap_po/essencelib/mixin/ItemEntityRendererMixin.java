package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.render.CollectorIntuitionItemOutlineRenderer;
import com.github.shap_po.essencelib.render.SoulOrbEntityRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Renders essence orb visuals on top of vanilla `ItemEntity` rendering.
 */
@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"))
    private void essencelib$setCurrentItemEntity(ItemEntity entity, float yaw, float tickDelta,
        MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Outline pass uses a different vertex format; skip custom textured orb geometry there.
        if (vertexConsumers instanceof OutlineVertexConsumerProvider) return;
        try {
            CollectorIntuitionItemOutlineRenderer.render(entity, tickDelta, matrices, vertexConsumers);
            // Render before vanilla item renderer flushes internal buffers.
            SoulOrbEntityRenderer.render(entity, tickDelta, matrices, vertexConsumers, light);
        } catch (Throwable ignored) {
            // Never let optional orb visuals crash item/entity rendering.
        }
    }
}
