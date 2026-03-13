package com.github.shap_po.essencelib.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

/**
 * Redirects all layer requests to a single glow layer and forces vertex colors (white/yellow).
 * Used for the Collector's Intuition halo.
 */
public record HaloVertexConsumerProvider(
    VertexConsumerProvider parent,
    RenderLayer layer,
    int r, int g, int b, int a
) implements VertexConsumerProvider {

    @Override
    public VertexConsumer getBuffer(RenderLayer type) {
        return new ColorForcingVertexConsumer(parent.getBuffer(layer), r, g, b, a);
    }
}
