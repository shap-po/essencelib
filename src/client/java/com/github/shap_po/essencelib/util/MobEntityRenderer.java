package com.github.shap_po.essencelib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Renders mob 3D models in GUI. Uses vanilla drawEntity rectangle overload
 * so entities auto-fit and orient correctly.
 */
@Environment(EnvType.CLIENT)
public final class MobEntityRenderer {

    private MobEntityRenderer() {}

    /**
     * Renders a LivingEntity in the GUI within the given rectangle.
     * Uses vanilla's rectangle-based drawEntity for correct fit and orientation.
     *
     * @param context    the draw context
     * @param x          left edge of slot (pixels)
     * @param y          top edge of slot (pixels)
     * @param size       slot size (pixels) - entity is scaled to fit
     * @param entity     the living entity to render
     * @param silhouette if true, renders as solid black silhouette
     * @param mouseX     screen X for entity to look at (use slot center for forward-facing)
     * @param mouseY     screen Y for entity to look at
     */
    public static void renderMobInGui(DrawContext context, int x, int y, int size,
                                      LivingEntity entity, boolean silhouette, float mouseX, float mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null) return;

        // Smooth idle spin so library mobs are not static.
        float spinYaw = (System.currentTimeMillis() % 12000L) * 0.03f;
        entity.setPosition(0, 0, 0);
        entity.calculateDimensions();
        entity.setYaw(spinYaw);
        entity.setPitch(0);
        entity.setHeadYaw(spinYaw);
        entity.bodyYaw = spinYaw;
        // Freeze limb animations so entities don't bob/sway when scrolling
        entity.limbAnimator.setSpeed(0);

        try {
            if (silhouette) {
                SilhouetteRendering.setActive(true);
            }
            // Keep tiny mobs from over-scaling and clipping out of center.
            float height = Math.max(0.6f, entity.getHeight());
            float scaleFactor = 1.8f / height;
            int renderScale = (int) (size * 0.45f * scaleFactor);
            renderScale = MathHelper.clamp(renderScale, (int) (size * 0.24f), (int) (size * 0.62f));
            
            InventoryScreen.drawEntity(
                context,
                x, y,
                x + size, y + size,
                renderScale,
                0.04f,
                mouseX, mouseY,
                entity
            );
        } finally {
            if (silhouette) {
                SilhouetteRendering.setActive(false);
            }
        }
    }

    /**
     * Creates a temporary LivingEntity for rendering. Caller must ensure the entity
     * is only used for rendering and not added to the world permanently.
     */
    public static LivingEntity createEntityForRendering(EntityType<?> type, World world) {
        Entity entity = type.create(world);
        if (!(entity instanceof LivingEntity living)) {
            return null;
        }

        // GUI previews should show only the mob model; strip any held/equipped items.
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            living.equipStack(slot, ItemStack.EMPTY);
        }
        return living;
    }
}
