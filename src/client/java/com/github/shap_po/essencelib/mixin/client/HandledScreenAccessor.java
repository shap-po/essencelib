// HandledScreenAccessor.java
package com.github.shap_po.essencelib.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();
}