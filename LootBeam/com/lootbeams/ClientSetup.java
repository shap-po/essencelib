/*     */ package com.lootbeams;
/*     */ import com.lootbeams.contexts.WorldRendererContext;
/*     */ import com.lootbeams.dconfig.DynamicConfig;
/*     */ import com.lootbeams.events.RenderEvents;
/*     */ import com.lootbeams.features.BeamOpacityOnApproach;
/*     */ import com.lootbeams.features.BeamSizeOnApproach;
/*     */ import com.lootbeams.features.CustomLootBeamsConfig;
/*     */ import com.lootbeams.features.CustomRarity;
/*     */ import com.lootbeams.managers.GlowEffectManager;
/*     */ import com.lootbeams.managers.PresetManager;
/*     */ import com.lootbeams.managers.RenderManager;
/*     */ import com.lootbeams.managers.TooltipManager;
/*     */ import com.lootbeams.renderers.HudRenderer;
/*     */ import com.lootbeams.renderers.LootBeamRenderer;
/*     */ import com.lootbeams.screens.LootBeamsPresetManagerScreen;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import com.mojang.brigadier.CommandDispatcher;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.arguments.StringArgumentType;
/*     */ import com.mojang.brigadier.builder.LiteralArgumentBuilder;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.exceptions.CommandSyntaxException;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.Executor;
/*     */ import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
/*     */ import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
/*     */ import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_304;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_3300;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_437;
/*     */ import net.minecraft.class_5250;
/*     */ import net.minecraft.class_638;
/*     */ import net.minecraft.class_746;
/*     */ import net.minecraft.class_9779;
/*     */ 
/*     */ public class ClientSetup {
/*     */   public static void registerKeyBindings() {
/*  46 */     keyBinding = KeyBindingHelper.registerKeyBinding(new class_304("lootbeams.keybindings.savePreset", class_3675.class_307.field_1668, 325, "lootbeams.title"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class_304 keyBinding;
/*     */ 
/*     */   
/*     */   public static void registerCoreShaderRegistrationEvents() {
/*  55 */     CoreShaderRegistrationCallback.EVENT.register(shaderRegistry -> {
/*     */           LootBeamShaders.registerCoreShaders(shaderRegistry);
/*     */           LootBeamShaders.registerDroplightCoreShaders(shaderRegistry);
/*     */           LootBeamShaders.registerCustomCoreShaders(shaderRegistry);
/*     */         });
/*     */   }
/*     */   
/*     */   public static void registerHudRenderEvents() {
/*  63 */     RenderEvents.HUD.register((context, tickCounter) -> HudRenderer.onHudRender(context, tickCounter));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerWorldRenderEvents() {
/*  69 */     RenderEvents.BEFORE_PARTICLES.register(worldRendererContext -> RenderManager.onWorldRenderBeforeParticles(worldRendererContext));
/*     */ 
/*     */     
/*  72 */     RenderEvents.AFTER_TRANSLUCENT.register(worldRendererContext -> RenderManager.onWorldRenderAfterTranslucent(worldRendererContext));
/*     */ 
/*     */     
/*  75 */     RenderEvents.AFTER_WEATHER.register(worldRendererContext -> RenderManager.onWorldRenderAfterWeather(worldRendererContext));
/*     */ 
/*     */     
/*  78 */     RenderEvents.BEFORE_END.register(worldRendererContext -> RenderManager.onWorldRenderBeforeEnd(worldRendererContext));
/*     */ 
/*     */     
/*  81 */     RenderEvents.END.register(worldRendererContext -> RenderManager.onWorldRenderEnd(worldRendererContext));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerEntityEvents() {
/*  87 */     ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
/*     */           if (entity instanceof class_1542) {
/*     */             class_1542 itemEntity = (class_1542)entity; TooltipManager.onEntityLoad(itemEntity, world);
/*     */             RenderManager.onEntityLoad(itemEntity, world);
/*     */           } 
/*     */         });
/*  93 */     ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
/*     */           if (entity instanceof class_1542) {
/*     */             class_1542 itemEntity = (class_1542)entity;
/*     */             TooltipManager.onEntityUnload(itemEntity, world);
/*     */             RenderManager.onEntityUnload(itemEntity, world);
/*     */           } 
/*     */         });
/*     */   }
/*     */   public static void registerClientEvents() {
/* 102 */     ResourceManagerHelper.get(class_3264.field_14188).registerReloadListener((IdentifiableResourceReloadListener)new ResourceReloadListener());
/* 103 */     ClientTickEvents.END_CLIENT_TICK.register(client -> {
/*     */           while (keyBinding.method_1436()) {
/*     */             class_310.method_1551().method_1507((class_437)new LootBeamsPresetManagerScreen((class_310.method_1551()).field_1755));
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerCommands() {
/* 113 */     ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal("lootbeams").then(ClientCommandManager.literal("save-preset").then(ClientCommandManager.argument("presetName", (ArgumentType)StringArgumentType.word()).executes(())))).then(ClientCommandManager.literal("item-custom-config").then(ClientCommandManager.literal("available").executes(())))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerCustomEvents() {
/* 185 */     ConfigEvents.SAVE.register(() -> {
/*     */           CustomLootBeamsConfig.onConfigurationChange();
/*     */           LootBeamRenderer.onConfigurationChange();
/*     */         });
/* 189 */     PresetEvents.APPLY_PRESET.register(presetName -> {
/*     */           CustomLootBeamsConfig.onConfigurationChange();
/*     */           LootBeamRenderer.onConfigurationChange();
/*     */         });
/* 193 */     ResourceEvents.RESOURCE_RELOAD.register((synchronizer, resourceManager, prepareExecutor, applyExecutor) -> {
/*     */           TooltipManager.onResourcesReload();
/*     */           CustomLootBeamsConfig.onResourcesReload();
/*     */           CustomRarity.onResourcesReload(resourceManager);
/*     */           PresetManager.onResourcesReload(resourceManager);
/*     */           GlowEffectManager.onResourceManagerReload(resourceManager, prepareExecutor);
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\ClientSetup.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */