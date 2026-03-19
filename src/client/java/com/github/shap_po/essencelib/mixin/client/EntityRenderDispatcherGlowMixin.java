package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.util.EssenceColorHelper;
import com.github.shap_po.essencelib.util.CollectorIntuitionOutlineHelper;
import com.github.shap_po.essencelib.util.PowerTooltipSlotRegistry;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sets custom outline color for ItemEntities glowing via Collector's Intuition.
 * Uses the more-outlines approach: inject before render, set color on OutlineVertexConsumerProvider.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherGlowMixin {

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void essencelib$setCollectorOutlineColor(Entity entity, double x, double y, double z,
            float yaw, float tickDelta, net.minecraft.client.util.math.MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(vertexConsumers instanceof OutlineVertexConsumerProvider outlineProvider)) return;
        if (entity instanceof ItemEntity itemEntity && !itemEntity.getStack().isEmpty()) {
            Identifier essenceId = itemEntity.getStack().get(ModDataComponentTypes.ESSENCE_ID);
            if (essenceId != null) {
                int rgb = EssenceColorHelper.getRgb(essenceId);
                int[] typed = resolvePowerTypeColor(itemEntity.getStack(), rgb);
                int r = typed[0];
                int g = typed[1];
                int b = typed[2];
                outlineProvider.setColor(r, g, b, 230);
                return;
            }
        }

        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!CollectorIntuitionOutlineHelper.hasCollectorsIntuition(player)) return;
        if (!CollectorIntuitionOutlineHelper.isInRange(player, entity)) return;
        if (entity instanceof ItemEntity itemEntity && itemEntity.getStack().isEmpty()) return;

        int argb = CollectorIntuitionOutlineHelper.getGlowColor(player, entity);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int a = (argb >> 24) & 0xFF;
        outlineProvider.setColor(r, g, b, a);
    }

    private static int[] resolvePowerTypeColor(ItemStack stack, int fallbackRgb) {
        int fallbackR = (fallbackRgb >> 16) & 0xFF;
        int fallbackG = (fallbackRgb >> 8) & 0xFF;
        int fallbackB = fallbackRgb & 0xFF;
        TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
        if (powers == null) {
            return new int[] {fallbackR, fallbackG, fallbackB};
        }

        var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
        for (var entry : entries) {
            if (entry.hidden()) continue;
            String slot = PowerTooltipSlotRegistry.getSlot(entry.powerId());
            if (PowerTooltipSlotRegistry.SLOT_ACTIVE.equals(slot)) {
                return blendWithAccent(fallbackR, fallbackG, fallbackB, 255, 85, 85, 0.34f);
            }
            if (PowerTooltipSlotRegistry.SLOT_PASSIVE.equals(slot)) {
                return blendWithAccent(fallbackR, fallbackG, fallbackB, 90, 255, 165, 0.34f);
            }
            if (PowerTooltipSlotRegistry.SLOT_LIFESTYLE.equals(slot)) {
                return blendWithAccent(fallbackR, fallbackG, fallbackB, 255, 215, 95, 0.34f);
            }
        }

        return new int[] {fallbackR, fallbackG, fallbackB};
    }

    private static int[] blendWithAccent(int br, int bg, int bb, int ar, int ag, int ab, float accentWeight) {
        float baseWeight = 1.0f - accentWeight;
        int r = Math.max(0, Math.min(255, (int) (br * baseWeight + ar * accentWeight)));
        int g = Math.max(0, Math.min(255, (int) (bg * baseWeight + ag * accentWeight)));
        int b = Math.max(0, Math.min(255, (int) (bb * baseWeight + ab * accentWeight)));
        return new int[] {r, g, b};
    }
}
