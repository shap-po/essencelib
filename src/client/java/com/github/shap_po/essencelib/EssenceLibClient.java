package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.component.RestrainedComponent;
import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.networking.ModPacketsS2C;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.registry.ModItems;
import com.github.shap_po.essencelib.render.MobEssenceItemRenderer;
import com.github.shap_po.essencelib.util.EssenceOutlineColors;
import com.github.shap_po.essencelib.util.ActivePowerDebugHelper;
import com.github.shap_po.essencelib.registry.TrinketKeyBindingGenerator;
import com.github.shap_po.essencelib.registry.ModScreenHandlers;
import com.github.shap_po.essencelib.screen.DownedPlayerLootScreen;
import com.github.shap_po.essencelib.screen.DownedOverlayRenderer;
import com.github.shap_po.essencelib.screen.EssenceFloatingTooltipRenderer;
import com.github.shap_po.essencelib.screen.EssenceKeybindScreen;
import com.github.shap_po.essencelib.screen.EssenceResourceHudRenderer;
import com.github.shap_po.essencelib.screen.ManaHudRenderer;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipComponent;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipData;
import com.github.shap_po.essencelib.registry.EssenceLibParticles;
import com.github.shap_po.essencelib.particle.EssenceWispParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.SimpleParticleType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.AllayEntityRenderer;
import net.minecraft.entity.EntityType;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import com.github.shap_po.essencelib.block.GuillotineBlockRenderer;
import com.github.shap_po.essencelib.block.DownedCorpseBlockRenderer;
import com.github.shap_po.essencelib.registry.ModBlockEntities;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;


public class EssenceLibClient implements ClientModInitializer {
    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<>();
    public static KeyBinding DEBUG_TOGGLE_KEY;
    public static KeyBinding DEBUG_CHECK_KEY;
    public static KeyBinding OPEN_KEYBINDS_KEY;
    private static boolean forcedPerspectiveActive = false;
    private static Perspective previousPerspective = Perspective.FIRST_PERSON;

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ManaAttributeRegistry.initialize();
        ModPacketsS2C.register();
        EssenceOutlineColors.load();
        HandledScreens.register(ModScreenHandlers.DOWNED_LOOT, DownedPlayerLootScreen::new);
        net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
            ModBlockEntities.GUILLOTINE,
            (BlockEntityRendererFactory.Context context) -> new GuillotineBlockRenderer(context)
        );
        net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
            ModBlockEntities.DOWNED_CORPSE,
            (BlockEntityRendererFactory.Context context) -> new DownedCorpseBlockRenderer(context)
        );

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof EssenceTooltipData essenceData) {
                return new EssenceTooltipComponent(essenceData);
            }
            return null;
        });

        // Collector's Intuition glow behavior is mode-driven via ability_hooks.json:
        // player_through_walls_custom | player_not_through_walls_custom
        // everyone_not_through_walls_custom | default_minecraft_glow.
        // Uncollected item particles: subtle hint for new items to discover (Collector's Rush / lifestyle)
        com.github.shap_po.essencelib.render.UncollectedItemParticleRenderer.register();
        // Essence wisp: vanilla-style particle (end-rod-like glow)
        ParticleFactoryRegistry.getInstance().<SimpleParticleType>register(EssenceLibParticles.ESSENCE_WISP,
            spriteProvider -> new EssenceWispParticle.Factory(spriteProvider));

        // Essence item: glowing tilted orb (replaces flat texture in hand/GUI/frame)
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.MOB_ESSENCE_ITEM, new MobEssenceItemRenderer());

        EntityRendererRegistry.register(EntityType.ALLAY, AllayEntityRenderer::new);

        // Register HUD renderers
        HudRenderCallback.EVENT.register(new ManaHudRenderer());
        HudRenderCallback.EVENT.register(new EssenceResourceHudRenderer());
        HudRenderCallback.EVENT.register(new EssenceFloatingTooltipRenderer());
        HudRenderCallback.EVENT.register(new DownedOverlayRenderer());

        for (int i = 0; i < EssenceLib.MAX_SLOT_COUNT; i++) {
            int defaultKey = GLFW.GLFW_KEY_UNKNOWN;
            KeyBinding keyBinding = new KeyBinding(TrinketKeyBindingGenerator.slotTranslationKey(i), defaultKey, EssenceLib.KEYBINDINGS_CATEGORY);
            KEY_BINDINGS.add(keyBinding);
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }

        DEBUG_TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.essencelib.debug_toggle", GLFW.GLFW_KEY_F8, EssenceLib.KEYBINDINGS_CATEGORY));
        DEBUG_CHECK_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.essencelib.debug_check", GLFW.GLFW_KEY_F7, EssenceLib.KEYBINDINGS_CATEGORY));

        OPEN_KEYBINDS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.essencelib.open_keybinds", GLFW.GLFW_KEY_UNKNOWN, EssenceLib.KEYBINDINGS_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            DownedOverlayRenderer.tick(client);
            if (OPEN_KEYBINDS_KEY.wasPressed() && client.currentScreen == null) {
                client.setScreen(new EssenceKeybindScreen(null));
            }
            ActivePowerDebugHelper.tick(client, DEBUG_TOGGLE_KEY, DEBUG_CHECK_KEY, KEY_BINDINGS);
            if (client.player != null) {
                boolean restrained = RestrainedComponent.isRestrained(client.player);
                boolean downed = DownedComponent.isDowned(client.player);
                boolean shouldForcePerspective = restrained || downed;
                if (shouldForcePerspective) {
                    Perspective targetPerspective = restrained
                        ? Perspective.THIRD_PERSON_FRONT
                        : (DownedOverlayRenderer.isKillerFocusActive()
                            ? Perspective.FIRST_PERSON
                            : Perspective.THIRD_PERSON_BACK);
                    if (!forcedPerspectiveActive) {
                        previousPerspective = client.options.getPerspective();
                        forcedPerspectiveActive = true;
                    }
                    if (client.options.getPerspective() != targetPerspective) {
                        client.options.setPerspective(targetPerspective);
                    }
                } else if (forcedPerspectiveActive) {
                    client.options.setPerspective(previousPerspective);
                    forcedPerspectiveActive = false;
                }
            } else if (forcedPerspectiveActive) {
                forcedPerspectiveActive = false;
                previousPerspective = Perspective.FIRST_PERSON;
            }
        });

        // Essence in-world orb + particles are handled by SoulOrbEntityRenderer.
    }
}
