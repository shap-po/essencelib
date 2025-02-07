package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.networking.ModPacketsS2C;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.registry.TrinketKeyBindingGenerator;
import com.github.shap_po.essencelib.screen.ManaHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;


public class EssenceLibClient implements ClientModInitializer {
    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<>();

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ManaAttributeRegistry.initialize();
        ModPacketsS2C.register();

        // Register the mana HUD renderer
        HudRenderCallback.EVENT.register(new ManaHudRenderer());

        for (int i = 0; i < EssenceLib.MAX_SLOT_COUNT; i++) {
            KeyBinding keyBinding = new KeyBinding(TrinketKeyBindingGenerator.slotTranslationKey(i), GLFW.GLFW_KEY_UNKNOWN, EssenceLib.KEYBINDINGS_CATEGORY);
            KEY_BINDINGS.add(keyBinding);
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }
}
