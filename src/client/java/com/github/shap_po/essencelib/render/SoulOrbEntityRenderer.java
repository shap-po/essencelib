package com.github.shap_po.essencelib.render;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.essencelib.mixin.client.ItemEntityAccessor;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.util.EssenceColorHelper;
import com.github.shap_po.essencelib.util.PowerTooltipSlotRegistry;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * Extra in-world renderer for essence drops: clean glass orb with centered mob.
 */
@Environment(EnvType.CLIENT)
public final class SoulOrbEntityRenderer {

    private static final Identifier WHITE_TEXTURE = Identifier.of("minecraft", "textures/misc/white.png");
    private static final RenderLayer ORB_LAYER = RenderLayer.getEntityTranslucentEmissive(WHITE_TEXTURE);

    private static final Map<EntityType<?>, Entity> DUMMY_ENTITY_CACHE = new HashMap<>();
    /** Packed light for full brightness (block 15, sky 15) for emissive orb. */
    private static final int FULL_BRIGHT = LightmapTextureManager.pack(15, 15);

    public static void render(ItemEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        if (entity.getStack().isEmpty() || !(entity.getStack().getItem() instanceof MobEssenceTrinketItem)) return;

        Identifier essenceId = entity.getStack().get(ModDataComponentTypes.ESSENCE_ID);
        int rgb = EssenceColorHelper.getRgb(essenceId);
        int baseR = EssenceColorHelper.getR(rgb);
        int baseG = EssenceColorHelper.getG(rgb);
        int baseB = EssenceColorHelper.getB(rgb);

        float age = entity.getItemAge() + tickDelta;
        float pulse = 1.0f; // keep world orb visually stable (no breathing pulse)
        // Neon-ish color push while preserving the essence hue.
        int r = MathHelper.clamp((int) (baseR * 1.05f + 10.0f), 0, 255);
        int g = MathHelper.clamp((int) (baseG * 1.05f + 10.0f), 0, 255);
        int b = MathHelper.clamp((int) (baseB * 1.08f + 14.0f), 0, 255);
        int[] orbitalTypeColor = resolvePowerTypeOrbitalColor(entity.getStack(), r, g, b);
        matrices.push();
        try {
            float uniqueOffset = ((ItemEntityAccessor) (Object) entity).essencelib$getUniqueOffset();
            float bob = MathHelper.sin(age / 10.0f + uniqueOffset) * 0.1f + 0.1f;

            // Float 1 block higher + bob
            matrices.translate(0.0, 1.0 + bob, 0.0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 5.0f));

            VertexConsumer orbVc = vertices.getBuffer(ORB_LAYER);
            drawBlockbenchSphereShell(matrices, orbVc, age, pulse, r, g, b, FULL_BRIGHT);
            // Keep orb shell halo tied to the essence/egg color.
            drawPulsingWhiteOutline(matrices, orbVc, age, pulse, r, g, b, FULL_BRIGHT);
            drawInnerCoreVfx(matrices, orbVc, age, pulse, r, g, b, FULL_BRIGHT);
            int glowBoost = resolvePowerTypeGlowBoost(entity.getStack());
            drawInwardSpiralVfx(matrices, orbVc, age, orbitalTypeColor[0], orbitalTypeColor[1], orbitalTypeColor[2], glowBoost, FULL_BRIGHT);
            renderMobInside(essenceId, matrices, vertices, age);
        } finally {
            matrices.pop();
        }
    }

    private static void drawBlockbenchSphereShell(MatrixStack matrices, VertexConsumer vc, float age, float pulse, int r, int g, int b, int light) {
        // Matches user's sphere.json display tilt (45,45,45) with gentle rotation.
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f + age * 3.5f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f));
        matrices.scale(pulse, pulse, pulse);

        // Cubes from sphere.json, converted from model units [0..16] around origin (8,8,8).
        drawModelCube(matrices, vc, 11, 6, 6, 12, 10, 10, r, g, b, 20, light);
        drawModelCube(matrices, vc, 5, 5, 5, 11, 11, 11, r, g, b, 14, light);
        drawModelCube(matrices, vc, 4, 6, 6, 5, 10, 10, r, g, b, 20, light);
        drawModelCube(matrices, vc, 6, 4, 6, 10, 5, 10, r, g, b, 20, light);
        drawModelCube(matrices, vc, 6, 6, 11, 10, 10, 12, r, g, b, 20, light);
        drawModelCube(matrices, vc, 6, 11, 6, 10, 12, 10, r, g, b, 20, light);
        drawModelCube(matrices, vc, 6, 6, 4, 10, 10, 5, r, g, b, 20, light);
        // Keep shell minimal so orbitals stay readable.

        // Core glow to keep it luminous and glassy.
        int cr = Math.min(255, r + 55);
        int cg = Math.min(255, g + 55);
        int cb = Math.min(255, b + 70);
        emitQuad(matrices, vc, -0.045f, -0.045f, 0.045f, 0.045f, cr, cg, cb, 70, light);
        matrices.pop();
    }

    private static void drawInnerCoreVfx(MatrixStack matrices, VertexConsumer vc, float age, float pulse, int r, int g, int b, int light) {
        int coreR = Math.min(255, r + 40);
        int coreG = Math.min(255, g + 40);
        int coreB = Math.min(255, b + 55);
        float corePulse = pulse * (1.0f + MathHelper.sin(age * 0.16f) * 0.08f);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 1.8f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(18.0f));
        emitQuad(matrices, vc, -0.05f * corePulse, -0.05f * corePulse, 0.05f * corePulse, 0.05f * corePulse, coreR, coreG, coreB, 112, light);
        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-age * 2.2f + 60.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(22.0f));
        emitQuad(matrices, vc, -0.04f, -0.04f, 0.04f, 0.04f, coreR, coreG, coreB, 98, light);
        matrices.pop();
    }


    private static void drawPulsingWhiteOutline(MatrixStack matrices, VertexConsumer vc, float age, float pulse,
                                                int r, int g, int b, int light) {
        float s = 0.145f;
        int alpha = 220;
        int neonR = Math.min(255, r + 48);
        int neonG = Math.min(255, g + 48);
        int neonB = Math.min(255, b + 64);

        // Depth-tested emissive outline: visible to everyone in sight, not through walls.
        for (int i = 0; i < 7; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * (2.6f + i * 0.5f) + i * 40.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(18.0f + i * 9.0f));
            emitQuad(matrices, vc, -s, -s, s, s, neonR, neonG, neonB, Math.max(70, alpha - i * 22), light);
            matrices.pop();
        }
    }

    private static void drawInwardSpiralVfx(MatrixStack matrices, VertexConsumer vc, float age, int r, int g, int b, int glowBoost, int light) {
        // Bright typed orbitals flowing into the center (active/passive/lifestyle).
        int orbitalMotes = 50;
        int orbR = r;
        int orbG = g;
        int orbB = b;
        float sizeScale = 1.0f + (glowBoost / 255.0f) * 0.35f;
        for (int i = 0; i < orbitalMotes; i++) {
            float phase = i / (float) orbitalMotes;
            float t = (age * 0.042f + phase) % 1.0f;
            float inv = 1.0f - t;
            float radius = 0.2f * inv;
            float theta = age * 0.42f + phase * (float) (Math.PI * 8.0);
            float px = MathHelper.cos(theta) * radius;
            float pz = MathHelper.sin(theta) * radius;
            float py = (phase - 0.5f) * 0.09f * inv;
            int alpha = MathHelper.clamp((int) (190 + glowBoost + inv * 70), 170, 255);
            float size = (0.0050f + inv * 0.0132f) * sizeScale;

            matrices.push();
            matrices.translate(px, py, pz);
            emitOrbitalOutlineMote(matrices, vc, age, i, size, orbR, orbG, orbB, alpha, light);
            matrices.pop();
        }
    }



    private static void drawModelCube(MatrixStack matrices, VertexConsumer vc,
                                      float fx, float fy, float fz, float tx, float ty, float tz,
                                      int r, int g, int b, int a, int light) {
        float x0 = (fx - 8.0f) / 16.0f;
        float y0 = (fy - 8.0f) / 16.0f;
        float z0 = (fz - 8.0f) / 16.0f;
        float x1 = (tx - 8.0f) / 16.0f;
        float y1 = (ty - 8.0f) / 16.0f;
        float z1 = (tz - 8.0f) / 16.0f;

        Matrix4f m = matrices.peek().getPositionMatrix();

        // front
        vc.vertex(m, x0, y0, z1).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x1, y0, z1).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x1, y1, z1).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, 0, 1);
        vc.vertex(m, x0, y1, z1).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, 0, 1);

        // back
        vc.vertex(m, x1, y0, z0).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, 0, -1);
        vc.vertex(m, x0, y0, z0).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, 0, -1);
        vc.vertex(m, x0, y1, z0).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, 0, -1);
        vc.vertex(m, x1, y1, z0).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, 0, -1);

        // left
        vc.vertex(m, x0, y0, z0).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(-1, 0, 0);
        vc.vertex(m, x0, y0, z1).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(-1, 0, 0);
        vc.vertex(m, x0, y1, z1).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(-1, 0, 0);
        vc.vertex(m, x0, y1, z0).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(-1, 0, 0);

        // right
        vc.vertex(m, x1, y0, z1).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(1, 0, 0);
        vc.vertex(m, x1, y0, z0).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(1, 0, 0);
        vc.vertex(m, x1, y1, z0).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(1, 0, 0);
        vc.vertex(m, x1, y1, z1).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(1, 0, 0);

        // top
        vc.vertex(m, x0, y1, z1).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, 1, 0);
        vc.vertex(m, x1, y1, z1).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, 1, 0);
        vc.vertex(m, x1, y1, z0).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, 1, 0);
        vc.vertex(m, x0, y1, z0).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, 1, 0);

        // bottom
        vc.vertex(m, x0, y0, z0).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, -1, 0);
        vc.vertex(m, x1, y0, z0).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, -1, 0);
        vc.vertex(m, x1, y0, z1).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, -1, 0);
        vc.vertex(m, x0, y0, z1).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, -1, 0);
    }


    private static void renderMobInside(Identifier essenceId, MatrixStack matrices, VertexConsumerProvider vertices, float age) {
        if (essenceId == null) return;
        Essence essence = EssenceManager.getNullable(essenceId);
        if (essence == null || essence.getDroppedBy() == null) return;

        EntityType<?> type = essence.getDroppedBy();
        Entity entity = DUMMY_ENTITY_CACHE.computeIfAbsent(type, t -> t.create(MinecraftClient.getInstance().world));
        if (entity == null) return;

        float w = Math.max(entity.getWidth(), 0.4f);
        float h = Math.max(entity.getHeight(), 0.4f);
        float maxDim = Math.max(w, h);
        float target = 0.17f; // Keep mob clearly visible in the orb center.
        float scale = target / maxDim;

        matrices.push();
        matrices.scale(scale, scale, scale);
        matrices.translate(0.0, -entity.getHeight() * 0.46, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 1.2f));

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
            dispatcher.render(entity, 0, 0, 0, 0, 1.0f, matrices, vertices, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        } catch (Exception ignored) {
            // Ignore dummy entity render edge cases.
        } finally {
            dispatcher.setRenderShadows(true);
            entity.setPos(x, y, z);
        }

        matrices.pop();
    }
    private static void emitQuad(MatrixStack matrices, VertexConsumer vc, float x0, float y0, float x1, float y1,
        int r, int g, int b, int a, int light) {
Matrix4f m = matrices.peek().getPositionMatrix();
vc.vertex(m, x0, y0, 0).color(r, g, b, a).texture(0, 1).overlay(0).light(light).normal(0, 0, 1);
vc.vertex(m, x1, y0, 0).color(r, g, b, a).texture(1, 1).overlay(0).light(light).normal(0, 0, 1);
vc.vertex(m, x1, y1, 0).color(r, g, b, a).texture(1, 0).overlay(0).light(light).normal(0, 0, 1);
vc.vertex(m, x0, y1, 0).color(r, g, b, a).texture(0, 0).overlay(0).light(light).normal(0, 0, 1);
}

    private static void emitOrbitalOutlineMote(MatrixStack matrices, VertexConsumer vc, float age, int index, float size,
                                               int r, int g, int b, int a, int light) {
        int glowR = r;
        int glowG = g;
        int glowB = b;
        int fillA = Math.max(96, (int) (a * 0.86f));

        // Add a soft filled center so the spiral reads as "filled", not just wireframe.
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 3.7f + index * 17.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(16.0f));
        emitQuad(matrices, vc, -size * 0.95f, -size * 0.95f, size * 0.95f, size * 0.95f, glowR, glowG, glowB, fillA, light);
        matrices.pop();


    }

    private static int[] resolvePowerTypeOrbitalColor(ItemStack stack, int fallbackR, int fallbackG, int fallbackB) {
        TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
        if (powers == null) return new int[] {fallbackR, fallbackG, fallbackB};

        boolean hasActive = false;
        boolean hasPassive = false;
        boolean hasLifestyle = false;
        var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
        for (var entry : entries) {
            if (entry.hidden()) continue;
            String slot = PowerTooltipSlotRegistry.getSlot(entry.powerId());
            if (PowerTooltipSlotRegistry.SLOT_ACTIVE.equals(slot)) hasActive = true;
            if (PowerTooltipSlotRegistry.SLOT_PASSIVE.equals(slot)) hasPassive = true;
            if (PowerTooltipSlotRegistry.SLOT_LIFESTYLE.equals(slot)) hasLifestyle = true;
        }

        // Prefer passive/lifestyle over helper active powers to avoid incorrect red orbitals.
        if (hasPassive) return new int[] {102, 255, 51};     // neon light green
        if (hasLifestyle) return new int[] {255, 250, 0};   // bright yellow
        if (hasActive) return new int[] {255, 0, 0};       // vibrant red
        return new int[] {fallbackR, fallbackG, fallbackB};
    }

    private static int resolvePowerTypeGlowBoost(ItemStack stack) {
        TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
        if (powers == null) return 10;

        boolean hasActive = false;
        boolean hasPassive = false;
        boolean hasLifestyle = false;
        var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
        for (var entry : entries) {
            if (entry.hidden()) continue;
            String slot = PowerTooltipSlotRegistry.getSlot(entry.powerId());
            if (PowerTooltipSlotRegistry.SLOT_ACTIVE.equals(slot)) hasActive = true;
            if (PowerTooltipSlotRegistry.SLOT_PASSIVE.equals(slot)) hasPassive = true;
            if (PowerTooltipSlotRegistry.SLOT_LIFESTYLE.equals(slot)) hasLifestyle = true;
        }

        if (hasPassive) return 90;
        if (hasLifestyle) return 36;
        if (hasActive) return 24;
        return 10;
    }

    private SoulOrbEntityRenderer() {}
}
