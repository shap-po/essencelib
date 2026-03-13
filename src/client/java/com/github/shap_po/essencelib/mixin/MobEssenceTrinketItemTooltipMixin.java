package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipData;
import com.github.shap_po.essencelib.util.EssenceTooltipHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Provides custom EssenceTooltipData for essence items so the tooltip uses our two-column component.
 */
@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public class MobEssenceTrinketItemTooltipMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void essencelib$injectEssenceTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            Optional<TooltipData> data = EssenceTooltipHelper.buildTooltipData(stack).map(d -> (TooltipData) d);
            if (data.isPresent()) {
                cir.setReturnValue(data);
            }
        }
    }
}
