package com.github.shap_po.essencelib.render;

import org.joml.Matrix4f;

import com.github.shap_po.essencelib.mixin.client.ItemEntityAccessor;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.util.CollectorIntuitionGlowMode;
import com.github.shap_po.essencelib.util.CollectorIntuitionOutlineHelper;
import com.github.shap_po.essencelib.util.EssenceOutlineColors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public final class CollectorIntuitionItemOutlineRenderer {
    private static final Identifier WHITE_TEXTURE = Identifier.of("minecraft", "textures/misc/white.png");
    private static final RenderLayer OUTLINE_LAYER = RenderLayer.getEntityTranslucentEmissive(WHITE_TEXTURE);
    private static final int FULL_BRIGHT = LightmapTextureManager.pack(15, 15);

    public static void render(ItemEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices) {
        if (entity == null || entity.getStack().isEmpty()) return;

        CollectorIntuitionGlowMode mode = CollectorIntuitionGlowMode.current();
        if (mode != CollectorIntuitionGlowMode.PLAYER_NOT_THROUGH_WALLS_CUSTOM
            && mode != CollectorIntuitionGlowMode.EVERYONE_NOT_THROUGH_WALLS_CUSTOM) {
            return;
        }

        float age = entity.getItemAge() + tickDelta;
        Identifier essenceId = entity.getStack().get(ModDataComponentTypes.ESSENCE_ID);
        if (essenceId != null) {
            // Essence orbs have their own renderer-driven visuals; skip the extra frame-outline pass.
            return;
        }

        int r;
        int g;
        int b;
        int a;
        if (mode == CollectorIntuitionGlowMode.PLAYER_NOT_THROUGH_WALLS_CUSTOM) {
            var player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (!CollectorIntuitionOutlineHelper.hasCollectorsIntuition(player)) return;
            if (!CollectorIntuitionOutlineHelper.isInRange(player, entity)) return;
            int argb = CollectorIntuitionOutlineHelper.getGlowColor(player, entity);
            r = (argb >> 16) & 0xFF;
            g = (argb >> 8) & 0xFF;
            b = argb & 0xFF;
            a = Math.max(80, (argb >> 24) & 0xFF);
        } else {
            int argb = EssenceOutlineColors.getDefaultArgb();
            r = (argb >> 16) & 0xFF;
            g = (argb >> 8) & 0xFF;
            b = argb & 0xFF;
            a = Math.max(80, (argb >> 24) & 0xFF);
        }

        float uniqueOffset = ((ItemEntityAccessor) (Object) entity).essencelib$getUniqueOffset();
        float bob = MathHelper.sin(age / 10.0f + uniqueOffset) * 0.1f + 0.1f;

        matrices.push();
        try {
            matrices.translate(0.0, 0.22 + bob, 0.0);
            VertexConsumer vc = vertices.getBuffer(OUTLINE_LAYER);
            float s = 0.18f;
            float t = 0.016f;
            for (int i = 0; i < 7; i++) {
                matrices.push();
                try {
                    // Multi-angle frame pass approximates vanilla glowing silhouette without through-wall rendering.
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * (1.8f + i * 0.25f) + i * 34.0f));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(12.0f + i * 9.0f));
                    emitFrameQuad(matrices, vc, s, t, r, g, b, Math.max(64, a - i * 10), FULL_BRIGHT);
                } finally {
                    matrices.pop();
                }
            }

            // Bright inner stroke to sharpen the contour.
            emitFrameQuad(matrices, vc, s * 0.92f, t * 0.72f,
                Math.min(255, r + 20), Math.min(255, g + 20), Math.min(255, b + 24),
                Math.max(100, a - 36), FULL_BRIGHT);
        } finally {
            matrices.pop();
        }
    }

    private static void emitQuad(MatrixStack matrices, VertexConsumer vc, float x0, float y0, float x1, float y1,
                                 int r, int g, int b, int a, int light) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        vc.vertex(m, x0, y0, 0).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x1, y0, 0).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x1, y1, 0).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x0, y1, 0).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, 0, 1);
    }

    private static void emitFrameQuad(MatrixStack matrices, VertexConsumer vc, float half, float thickness,
                                      int r, int g, int b, int a, int light) {
        emitQuad(matrices, vc, -half, half - thickness, half, half, r, g, b, a, light); // top
        emitQuad(matrices, vc, -half, -half, half, -half + thickness, r, g, b, a, light); // bottom
        emitQuad(matrices, vc, -half, -half, -half + thickness, half, r, g, b, a, light); // left
        emitQuad(matrices, vc, half - thickness, -half, half, half, r, g, b, a, light); // right
    }

    private CollectorIntuitionItemOutlineRenderer() {}
}
