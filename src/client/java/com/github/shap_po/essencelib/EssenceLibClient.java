package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.networking.ModPacketsS2C;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.util.EssenceOutlineColors;
import com.github.shap_po.essencelib.registry.TrinketKeyBindingGenerator;
import com.github.shap_po.essencelib.screen.EssenceFloatingTooltipRenderer;
import com.github.shap_po.essencelib.screen.ManaHudRenderer;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipComponent;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
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
        EssenceOutlineColors.load();

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof EssenceTooltipData essenceData) {
                return new EssenceTooltipComponent(essenceData);
            }
            return null;
        });

        // Collector's Intuition: halo glow drawn via CollectorIntuitionHaloMixin in ItemEntityRenderer

        // Register HUD renderers
        HudRenderCallback.EVENT.register(new ManaHudRenderer());
        HudRenderCallback.EVENT.register(new EssenceFloatingTooltipRenderer());

        for (int i = 0; i < EssenceLib.MAX_SLOT_COUNT; i++) {
            int defaultKey = (i == 0) ? GLFW.GLFW_KEY_V : GLFW.GLFW_KEY_UNKNOWN;
            KeyBinding keyBinding = new KeyBinding(TrinketKeyBindingGenerator.slotTranslationKey(i), defaultKey, EssenceLib.KEYBINDINGS_CATEGORY);
            KEY_BINDINGS.add(keyBinding);
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }
}
