package com.github.shap_po.essencelib.render;

import com.github.shap_po.essencelib.util.CollectorIntuitionOutlineHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;

/**
 * Renders AABB line-box glows around item entities for players with Collector's Intuition.
 * Uses a custom RenderLayer (ALWAYS_DEPTH_TEST + COLOR_MASK) so the glow is visible through
 * blocks without blocking the view.
 */
@Environment(EnvType.CLIENT)
public final class CollectorIntuitionGlowRenderer {

    private static final float EXPAND = 0.05f;

    public static void render(WorldRenderContext context) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player == null) return;
        if (!CollectorIntuitionOutlineHelper.hasCollectorsIntuition(player)) return;

        var matrixStack = context.matrixStack();
        var consumers = context.consumers();
        if (matrixStack == null || consumers == null) return;

        var world = context.world();
        if (world == null) return;

        VertexConsumer buffer = consumers.getBuffer(CollectorIntuitionRenderLayers.GLOW_LINES);
        var cam = context.camera();
        double cx = cam.getPos().x;
        double cy = cam.getPos().y;
        double cz = cam.getPos().z;

        matrixStack.push();
        matrixStack.translate(-cx, -cy, -cz);

        for (var entity : world.getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(30), e -> true)) {
            if (!CollectorIntuitionOutlineHelper.isInRange(player, entity)) continue;

            Box aabb = entity.getBoundingBox().expand(EXPAND);
            int argb = CollectorIntuitionOutlineHelper.getGlowColor(player, entity.getStack());
            float r = ((argb >> 16) & 0xFF) / 255f;
            float g = ((argb >> 8) & 0xFF) / 255f;
            float b = (argb & 0xFF) / 255f;
            float a = ((argb >> 24) & 0xFF) / 255f;

            WorldRenderer.drawBox(matrixStack, buffer, aabb, r, g, b, a);
        }

        matrixStack.pop();
    }

    private CollectorIntuitionGlowRenderer() {}
}
