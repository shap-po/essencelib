package com.github.shap_po.essencelib.mixin.client;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererFeatureAdder {

    @Invoker("addFeature")
    boolean essencelib$addFeature(FeatureRenderer<?, ?> feature);
}
