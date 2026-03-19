package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.lootbeams.config.Configuration;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables Loot Beams nametag for essence items - we use our own floating tooltip instead.
 */
@Mixin(value = com.lootbeams.renderers.NameTagRenderer.class, remap = false)
public abstract class LootBeamsNameTagRendererMixin {

    @Inject(method = "renderNameTags", at = @At("HEAD"), cancellable = true, remap = false)
    private static void essencelib$skipNametagForEssence(VertexConsumerProvider.Immediate buffer, MatrixStack matrixStack, ItemEntity itemEntity, Configuration itemConfig, TextColor color, float fadeAlpha, float currentGroundTime, long worldtime, float pticks, CallbackInfo ci) {
        ItemStack stack = itemEntity.getStack();
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof MobEssenceTrinketItem) {
            ci.cancel();
        }
    }
}
