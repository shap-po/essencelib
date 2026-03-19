package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Guards against occasional slot-update packets that reference a slot index that doesn't
 * exist on the current client screen handler yet (slot sync race).
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void essencelib$ignoreOutOfRangeSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        ScreenHandler handler = client.player.currentScreenHandler;
        if (handler == null) return;

        int slot = packet.getSlot();
        if (slot >= 0 && slot < handler.slots.size()) return;

        EssenceLib.LOGGER.warn(
            "Ignored out-of-range slot update packet: slot={} handlerSlots={} syncId={}",
            slot, handler.slots.size(), handler.syncId
        );
        ci.cancel();
    }
}
