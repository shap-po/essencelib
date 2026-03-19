package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.util.EssenceTooltipHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixes into Loot Beams' TooltipManager. For essence items:
 * - Never cache (onEntityLoad): ensures we always compute fresh tooltip with current Shift state.
 * - Always return fresh (getTooltipFromCache): so the look-at nametag updates every frame.
 */
@Mixin(value = com.lootbeams.managers.TooltipManager.class, remap = false)
public abstract class LootBeamsTooltipManagerMixin {

    @Inject(method = "onEntityLoad(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/client/world/ClientWorld;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void essencelib$skipCacheEssence(ItemEntity item, ClientWorld world, CallbackInfo ci) {
        ItemStack stack = item.getStack();
        if (!stack.isEmpty() && stack.getItem() instanceof MobEssenceTrinketItem) {
            ci.cancel(); // Don't cache; getTooltipFromCache will always compute fresh.
        }
    }

    @Inject(method = "getTooltipFromCache(Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void essencelib$injectEssenceTooltip(ItemStack itemStack, CallbackInfoReturnable<List<Text>> cir) {
        if (itemStack == null || itemStack.isEmpty()) return;
        if (!(itemStack.getItem() instanceof MobEssenceTrinketItem)) return;

        // Always show full tooltip with descriptions; no shift-to-expand.
        List<Text> lines = EssenceTooltipHelper.buildTooltipLines(itemStack, true, null);
        cir.setReturnValue(lines.isEmpty() ? List.of(itemStack.getName()) : lines);
        cir.cancel();
    }
}
