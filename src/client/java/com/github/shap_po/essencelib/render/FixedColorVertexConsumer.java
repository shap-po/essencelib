package com.github.shap_po.essencelib.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A VertexConsumer that overrides all vertex colors to a fixed value.
 * Used for rendering entity silhouettes (solid color, no texture tint).
 */
@Environment(EnvType.CLIENT)
public class FixedColorVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final int fixedR, fixedG, fixedB, fixedA;
    private final float fixedRf, fixedGf, fixedBf, fixedAf;

    public FixedColorVertexConsumer(VertexConsumer delegate, int r, int g, int b, int a) {
        this.delegate = delegate;
        this.fixedR = Math.max(0, Math.min(255, r));
        this.fixedG = Math.max(0, Math.min(255, g));
        this.fixedB = Math.max(0, Math.min(255, b));
        this.fixedA = Math.max(0, Math.min(255, a));
        this.fixedRf = this.fixedR / 255f;
        this.fixedGf = this.fixedG / 255f;
        this.fixedBf = this.fixedB / 255f;
        this.fixedAf = this.fixedA / 255f;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        delegate.color(fixedR, fixedG, fixedB, fixedA);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        delegate.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        delegate.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        delegate.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light,
                       float normalX, float normalY, float normalZ) {
        int packed = (fixedA << 24) | (fixedR << 16) | (fixedG << 8) | fixedB;
        delegate.vertex(x, y, z, packed, u, v, overlay, light, normalX, normalY, normalZ);
    }

    @Override
    public VertexConsumer color(float red, float green, float blue, float alpha) {
        delegate.color(fixedRf, fixedGf, fixedBf, fixedAf);
        return this;
    }

    @Override
    public VertexConsumer color(int argb) {
        delegate.color((fixedA << 24) | (fixedR << 16) | (fixedG << 8) | fixedB);
        return this;
    }

    @Override
    public VertexConsumer colorRgb(int rgb) {
        delegate.color((fixedA << 24) | (fixedR << 16) | (fixedG << 8) | fixedB);
        return this;
    }

    @Override
    public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] light, int overlay, boolean useQuadColorData) {
        delegate.quad(matrixEntry, quad, brightnesses, fixedRf, fixedGf, fixedBf, fixedAf, light, overlay, false);
    }

    @Override
    public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay) {
        delegate.quad(matrixEntry, quad, fixedRf, fixedGf, fixedBf, fixedAf, light, overlay);
    }

    @Override
    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        delegate.vertex(matrix, x, y, z);
        return this;
    }

    @Override
    public VertexConsumer vertex(Vector3f vec) {
        delegate.vertex(vec);
        return this;
    }

    @Override
    public VertexConsumer vertex(MatrixStack.Entry matrix, Vector3f vec) {
        delegate.vertex(matrix, vec);
        return this;
    }

    @Override
    public VertexConsumer vertex(MatrixStack.Entry matrix, float x, float y, float z) {
        delegate.vertex(matrix, x, y, z);
        return this;
    }

    @Override
    public VertexConsumer normal(MatrixStack.Entry matrix, float x, float y, float z) {
        delegate.normal(matrix, x, y, z);
        return this;
    }
}
