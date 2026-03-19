package com.github.shap_po.essencelib.render;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.registry.ModItems;
import com.github.shap_po.essencelib.util.EssenceColorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * Renders the essence item as a glowing, tilted orb/cube (Orb of Dominance style)
 * with essence-based coloring. Used in inventory, hand, and item frames.
 */
@Environment(EnvType.CLIENT)
public class MobEssenceItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    private static final Identifier WHITE_TEXTURE = EssenceLib.identifier("textures/misc/silhouette_white.png");
    private static final RenderLayer ORB_LAYER = RenderLayer.getEntityTranslucentEmissive(WHITE_TEXTURE);
    private static final float GUI_OFFSET_X = 0.5f;
    private static final float GUI_OFFSET_Y = 0.55f;

    // Cache for dummy entities to avoid re-creation every frame
    private static final Map<EntityType<?>, Entity> DUMMY_ENTITY_CACHE = new HashMap<>();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!stack.isOf(ModItems.MOB_ESSENCE_ITEM)) return;
        boolean worldDrop = mode == ModelTransformationMode.GROUND;
        boolean inGui = mode == ModelTransformationMode.GUI;
        if (worldDrop) {
            // Dropped essence is rendered by SoulOrbEntityRenderer as a single centered orb.
            return;
        }

        Identifier essenceId = stack.get(ModDataComponentTypes.ESSENCE_ID);
        int rgb = EssenceColorHelper.getRgb(essenceId);
        int r = EssenceColorHelper.getR(rgb);
        int g = EssenceColorHelper.getG(rgb);
        int b = EssenceColorHelper.getB(rgb);

        // Continuous time phase + full 360 spin (no cut-back loop).
        float phase = (System.currentTimeMillis() % 10000L) / 10000.0f;
        float time = phase * (float) (2 * Math.PI);
        float spinDegrees = phase * 360.0f;
        int fullBright = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        matrices.push();

        if (!worldDrop) {
            // Keep GUI render centered and stable inside the slot.
            if (inGui) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(35.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.0f));
                // Apply offset after tilt so movement is predictable in GUI slot space.
                matrices.translate(GUI_OFFSET_X, GUI_OFFSET_Y, 0.0f);
            } else {
                // Hand/frame tilt.
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35.264f)); // acos(1/sqrt(3)) ~ 35.3°
            }
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinDegrees));
        }

        // Slow breathing pulse for a calmer, hypnotic feel.
        float pulse = inGui ? 1.0f : (float) Math.sin(time * 2.8f) * 0.03f + 1.0f;
        float scale = (inGui ? 0.265f : 0.28f) * pulse;

        // In GUI use exact essence color; hand/frame can keep a tiny lift.
        int rr = inGui ? r : MathHelper.clamp(r + 10, 0, 255);
        int gg = inGui ? g : MathHelper.clamp(g + 8, 0, 255);
        int bb = inGui ? b : MathHelper.clamp(b + 14, 0, 255);
        VertexConsumer vc = vertexConsumers.getBuffer(ORB_LAYER);

        // GUI-only black rim so icon stays readable in inventory/menus.
        if (inGui) {
            drawGuiBlackRim(matrices, vc, spinDegrees, scale, fullBright);
        }

        // Draw a smooth-looking orb shell (crossed emissive quads).
        drawRoundShell(matrices, vc, time * 16, scale, rr, gg, bb, fullBright);
        drawRoundShell(matrices, vc, -time * 12 + 1.2f, scale * 0.78f, rr, gg, bb, fullBright);
        int coreR = Math.min(255, rr + 25);
        int coreG = Math.min(255, gg + 25);
        int coreB = Math.min(255, bb + 25);
        drawPowerTypeCoreGlow(matrices, vc, time, scale, coreR, coreG, coreB, fullBright);
        if (!inGui) {
            drawHighlightCrescent(matrices, vc, time, scale * 0.92f, fullBright);
        }

        // Render the Mob Inside (Hologram/Glint Effect)
        renderMobInside(essenceId, matrices, vertexConsumers, time);

        matrices.pop();
    }

    private void renderMobInside(Identifier essenceId, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float time) {
        if (essenceId == null) return;
        Essence essence = EssenceManager.getNullable(essenceId);
        if (essence == null || essence.getDroppedBy() == null) return;

        EntityType<?> type = essence.getDroppedBy();
        Entity entity = DUMMY_ENTITY_CACHE.computeIfAbsent(type, t -> t.create(MinecraftClient.getInstance().world));

        if (entity == null) return;

        float w = Math.max(entity.getWidth(), 0.4f);
        float h = Math.max(entity.getHeight(), 0.4f);
        float maxDim = Math.max(w, h);
        float target = 0.20f;
        float scale = target / maxDim;

        matrices.push();
        // Center on the entity's visual mid-body so it stays inside the orb core.
        matrices.scale(scale, scale, scale);
        matrices.translate(0.0, -entity.getHeight() * 0.46, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 12.0f));

        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        entity.setPos(0, 0, 0);
        entity.setBodyYaw(0);
        entity.setHeadYaw(0);
        if (entity instanceof LivingEntity living) {
            living.bodyYaw = 0;
            living.headYaw = 0;
        }

        try {
            dispatcher.setRenderShadows(false);
            dispatcher.render(entity, 0, 0, 0, 0, 1.0f, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        } catch (Exception ignored) {
            // Ignore rendering errors for dummy entities.
        } finally {
            dispatcher.setRenderShadows(true);
            entity.setPos(x, y, z);
        }
        
        matrices.pop();
    }

    private static void drawRoundShell(MatrixStack matrices, VertexConsumer vc, float spinDeg, float scale, int r, int g, int b, int light) {
        for (int i = 0; i < 6; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinDeg + i * 30.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * 14.0f));
            emitQuad(matrices, vc, -scale, -scale, scale, scale, r, g, b, 62, light);
            matrices.pop();
        }
    }

    private static void drawGuiBlackRim(MatrixStack matrices, VertexConsumer vc, float spinDegrees, float scale, int light) {
        for (int i = 0; i < 8; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinDegrees + i * 22.5f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(8.0f + i * 10.0f));
            emitQuad(matrices, vc, -scale * 1.34f, -scale * 1.34f, scale * 1.34f, scale * 1.34f, 0, 0, 0, 210, light);
            matrices.pop();
        }
    }

    private static void drawPowerTypeCoreGlow(MatrixStack matrices, VertexConsumer vc, float time, float scale,
                                              int r, int g, int b, int light) {
        float core = scale * 0.42f;
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 18.0f));
        emitQuad(matrices, vc, -core, -core, core, core, r, g, b, 170, light);
        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-time * 16.0f + 40.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(24.0f));
        emitQuad(matrices, vc, -core * 0.78f, -core * 0.78f, core * 0.78f, core * 0.78f, 245, 250, 255, 110, light);
        matrices.pop();
    }

    private static void drawHighlightCrescent(MatrixStack matrices, VertexConsumer vc, float time, float scale, int light) {
        // Fake glass specular: soft, offset crescent that slowly drifts.
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(26.0f + time * 8.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-18.0f));
        matrices.translate(scale * 0.22f, scale * 0.16f, scale * 0.02f);
        emitQuad(matrices, vc, -scale * 0.45f, -scale * 0.30f, scale * 0.45f, scale * 0.30f, 235, 245, 255, 52, light);
        matrices.pop();
    }

    // private static void drawCore(MatrixStack matrices, VertexConsumer vc, float spinDeg, float scale, int r, int g, int b, int a, int light) { ... } // Removed
    // private static void drawSpiral(MatrixStack matrices, VertexConsumer vc, float time, int r, int g, int b, int light) { ... } // Removed

    private static void emitQuad(MatrixStack matrices, VertexConsumer vc, float x0, float y0, float x1, float y1,
                                int r, int g, int b, int a, int light) {
        Matrix4f m = matrices.peek().getPositionMatrix();
        vc.vertex(m, x0, y0, 0).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x1, y0, 0).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x1, y1, 0).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x0, y1, 0).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, 0, 1);
    }
}
