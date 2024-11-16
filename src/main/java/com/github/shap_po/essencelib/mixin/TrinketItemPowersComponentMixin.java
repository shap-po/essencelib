package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTooltips;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import dev.emi.trinkets.TrinketModifiers;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TrinketItemPowersComponent.class)
public abstract class TrinketItemPowersComponentMixin {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void onAppendTooltip(PlayerEntity player, ItemStack stack, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        Text essenceName = Text.literal(stack.getName().getString()).formatted(Formatting.BOLD, Formatting.LIGHT_PURPLE);
        tooltip.add(essenceName);
        MobEssenceTooltips.appendTooltip(stack, null, tooltip);

        ci.cancel();
    }
}
