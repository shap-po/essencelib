package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.component.DownedComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenDownedLockMixin {

    @Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockHandledClickWhenDowned(int button, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (DownedComponent.isDowned(player)) {
            ci.cancel();
        }
    }
}
