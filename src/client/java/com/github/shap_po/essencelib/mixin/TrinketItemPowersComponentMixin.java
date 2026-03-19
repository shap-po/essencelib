package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(TrinketItemPowersComponent.class)
public abstract class TrinketItemPowersComponentMixin {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void onAppendTooltip(PlayerEntity player, ItemStack stack, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (stack.getItem() instanceof com.github.shap_po.essencelib.item.MobEssenceTrinketItem) {
            // Essence items use custom EssenceTooltipComponent via getTooltipData.
            // Cancel this append pass, but keep any existing base lines intact.
            ci.cancel();
        }
    }
}
