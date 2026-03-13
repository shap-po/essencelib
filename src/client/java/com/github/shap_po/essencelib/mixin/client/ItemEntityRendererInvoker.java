package com.github.shap_po.essencelib.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface ItemEntityRendererInvoker {

    @Invoker("renderLabelIfPresent")
    void essencelib$invokeRenderLabelIfPresent(Entity entity, Text text, MatrixStack matrices,
        VertexConsumerProvider vertexConsumers, int light, float tickDelta);
}
