package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.util.DownedStateHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Prevents IndexOutOfBoundsException when server sends inventory updates for more slots
 * than the client's screen handler has (e.g. Trinkets slot count sync race).
 */
@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerInventorySyncMixin {

    @Shadow
    private DefaultedList<Slot> slots;

    @ModifyVariable(
        method = "updateSlotStacks",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private List<ItemStack> essencelib$clampStacksToSlotCount(List<ItemStack> stacks) {
        int maxSlots = slots.size();
        if (stacks.size() <= maxSlots) return stacks;
        return stacks.subList(0, maxSlots);
    }

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockSlotInteractionWhenDowned(int slotIndex, int button, SlotActionType actionType,
                                                           PlayerEntity player, CallbackInfo ci) {
        if (!DownedComponent.isDowned(player)) {
            return;
        }
        DownedStateHelper.sendDownedActionbar(player);
        ci.cancel();
    }
}
