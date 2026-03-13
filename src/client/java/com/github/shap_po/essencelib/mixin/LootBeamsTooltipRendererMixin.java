package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables Loot Beams tooltip for essence items - we use our own floating tooltip instead.
 */
@Mixin(targets = "com.lootbeams.renderers.TooltipRenderer", remap = false)
public abstract class LootBeamsTooltipRendererMixin {

    @Inject(method = "renderWorldPositionTooltip", at = @At("HEAD"), cancellable = true, remap = false)
    private static void essencelib$skipTooltipForEssence(net.minecraft.client.gui.DrawContext drawContext, Entity entity, ItemStack itemStack, float tickDelta, CallbackInfo ci) {
        if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof MobEssenceTrinketItem) {
            ci.cancel();
        }
    }
}
