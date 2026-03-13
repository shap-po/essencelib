package com.github.shap_po.essencelib.render;

import net.minecraft.client.render.VertexConsumer;

/**
 * Forces every vertex to use a fixed color, rejecting the original texture colors.
 * Used for the Collector's Intuition halo so the glow appears solid white/yellow.
 */
public final class ColorForcingVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final int r, g, b, a;

    public ColorForcingVertexConsumer(VertexConsumer delegate, int r, int g, int b, int a) {
        this.delegate = delegate;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        return delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return delegate.color(r, g, b, a);
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return delegate.texture(u, v);
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return delegate.overlay(u, v);
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return delegate.light(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return delegate.normal(x, y, z);
    }
}
