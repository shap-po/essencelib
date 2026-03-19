package com.github.shap_po.essencelib.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

/**
 * A VertexConsumerProvider that wraps all returned buffers with FixedColorVertexConsumer,
 * forcing a fixed color while preserving the original render layer.
 */
@Environment(EnvType.CLIENT)
public class FixedColorVertexConsumerProvider implements VertexConsumerProvider {

    private final VertexConsumerProvider delegate;
    private final int r, g, b, a;

    public FixedColorVertexConsumerProvider(VertexConsumerProvider delegate, int r, int g, int b, int a) {
        this.delegate = delegate;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        // Preserve original layer so transparent model textures keep working.
        return new FixedColorVertexConsumer(delegate.getBuffer(layer), r, g, b, a);
    }
}
