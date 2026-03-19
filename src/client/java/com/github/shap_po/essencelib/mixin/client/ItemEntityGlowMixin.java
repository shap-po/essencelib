package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.util.CollectorIntuitionOutlineHelper;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes ItemEntities glow (vanilla outline, visible through walls) when the player has
 * Collector's Intuition. Uses Minecraft's built-in isGlowing() system like more-outlines.
 */
@Mixin(Entity.class)
public abstract class ItemEntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void essencelib$collectorIntuitionGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ItemEntity itemEntity
            && !itemEntity.getStack().isEmpty()
            && itemEntity.getStack().getItem() instanceof MobEssenceTrinketItem) {
            // Always glow dropped essence orbs via vanilla outline pipeline.
            cir.setReturnValue(true);
            return;
        }

        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!CollectorIntuitionOutlineHelper.hasCollectorsIntuition(player)) return;
        if (!CollectorIntuitionOutlineHelper.isInRange(player, self)) return;
        if (self instanceof ItemEntity itemEntity && itemEntity.getStack().isEmpty()) return;

        cir.setReturnValue(true);
    }
}
