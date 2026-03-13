/*     */ package com.lootbeams.render;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.features.BeamOpacityOnApproach;
/*     */ import com.lootbeams.features.BeamSizeOnApproach;
/*     */ import com.lootbeams.features.CustomLootBeamsConfig;
/*     */ import com.lootbeams.helpers.NumberHelper;
/*     */ import com.lootbeams.helpers.TextColorHelper;
/*     */ import com.lootbeams.renderers.IBeamRenderer;
/*     */ import com.lootbeams.renderers.LootBeamRenderer;
/*     */ import com.lootbeams.renderers.NameTagRenderer;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import java.util.HashMap;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_5251;
/*     */ import net.minecraft.class_746;
/*     */ 
/*     */ public class BeamRender {
/*  20 */   private static final Map<class_1542, Integer> ITEM_GROUND_START_TIMES = new HashMap<>();
/*     */   
/*     */   private static float fadeDistanceAlpha(float distance, Configuration itemConfig) {
/*  23 */     float defaultMultiplier = 1.0F;
/*     */     
/*  25 */     if (itemConfig.beamOpacityOnApproach == BeamOpacityOnApproach.DISABLED) {
/*  26 */       return defaultMultiplier;
/*     */     }
/*  28 */     if (itemConfig.beamOpacityOnApproach == BeamOpacityOnApproach.FADE_IN) {
/*  29 */       return (float)NumberHelper.clampedMapRange(
/*  30 */           Float.valueOf(distance), 
/*  31 */           Float.valueOf(itemConfig.changeOffset), 
/*  32 */           Float.valueOf(itemConfig.changeDistance), 
/*  33 */           Float.valueOf(1.0F), 
/*  34 */           Float.valueOf(0.0F));
/*     */     }
/*     */     
/*  37 */     if (itemConfig.beamOpacityOnApproach == BeamOpacityOnApproach.FADE_OUT) {
/*  38 */       return (float)NumberHelper.clampedMapRange(
/*  39 */           Float.valueOf(distance), 
/*  40 */           Float.valueOf(itemConfig.changeOffset), 
/*  41 */           Float.valueOf(itemConfig.changeDistance), 
/*  42 */           Float.valueOf(0.0F), 
/*  43 */           Float.valueOf(1.0F));
/*     */     }
/*     */ 
/*     */     
/*  47 */     return defaultMultiplier;
/*     */   }
/*     */   
/*     */   private static float getBeamSizeMultiplier(float distance, Configuration itemConfig) {
/*  51 */     float defaultMultiplier = 1.0F;
/*     */     
/*  53 */     if (itemConfig.beamSizeOnApproach == BeamSizeOnApproach.DISABLED) {
/*  54 */       return defaultMultiplier;
/*     */     }
/*  56 */     if (itemConfig.beamSizeOnApproach == BeamSizeOnApproach.SHRINK) {
/*  57 */       return (float)NumberHelper.clampedMapRange(
/*  58 */           Float.valueOf(distance), 
/*  59 */           Float.valueOf(itemConfig.changeOffset), 
/*  60 */           Float.valueOf(itemConfig.changeDistance), 
/*  61 */           Float.valueOf(0.0F), 
/*  62 */           Float.valueOf(1.0F));
/*     */     }
/*     */     
/*  65 */     if (itemConfig.beamSizeOnApproach == BeamSizeOnApproach.GROW) {
/*  66 */       return (float)NumberHelper.clampedMapRange(
/*  67 */           Float.valueOf(distance), 
/*  68 */           Float.valueOf(itemConfig.changeOffset), 
/*  69 */           Float.valueOf(itemConfig.changeDistance), 
/*  70 */           Float.valueOf(1.0F), 
/*  71 */           Float.valueOf(0.0F));
/*     */     }
/*     */ 
/*     */     
/*  75 */     return defaultMultiplier;
/*     */   }
/*     */   
/*     */   public static boolean canRenderBeam(Configuration itemConfig, float fadeAlpha) {
/*  79 */     if (itemConfig.beamAlpha * fadeAlpha < 0.01F) {
/*  80 */       return false;
/*     */     }
/*  82 */     return true;
/*     */   }
/*     */   
/*     */   public static void render(class_4587 stack, class_4597.class_4598 buffer, class_1542 item, long worldtime, float pticks) {
/*  86 */     if (!ITEM_GROUND_START_TIMES.containsKey(item)) {
/*  87 */       ITEM_GROUND_START_TIMES.put(item, Integer.valueOf(item.method_6985()));
/*     */     }
/*     */     
/*  90 */     float itemGroundStartTime = ((Integer)ITEM_GROUND_START_TIMES.getOrDefault(item, Integer.valueOf(0))).intValue();
/*  91 */     float currentGroundTime = item.method_6985() - itemGroundStartTime + pticks;
/*  92 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(item.method_6983());
/*     */ 
/*     */     
/*  95 */     class_746 player = (class_310.method_1551()).field_1724;
/*  96 */     float distance = (player != null) ? player.method_5739((class_1297)item) : 0.0F;
/*  97 */     float sizeMultiplier = getBeamSizeMultiplier(distance, itemConfig);
/*  98 */     float fadeAlpha = fadeDistanceAlpha(distance, itemConfig);
/*     */     
/* 100 */     if (!canRenderBeam(itemConfig, fadeAlpha)) {
/*     */       return;
/*     */     }
/*     */     
/* 104 */     RenderSystem.enableDepthTest();
/*     */     
/* 106 */     class_5251 color = TextColorHelper.getItemColor(item.method_6983());
/*     */     
/* 108 */     if (item.method_24828()) {
/* 109 */       stack.method_22903();
/*     */       
/* 111 */       GroundEffectRenderer.renderGroundEffect(buffer, stack, item, itemConfig, color, sizeMultiplier, fadeAlpha, currentGroundTime, worldtime, pticks);
/*     */ 
/*     */ 
/*     */       
/* 115 */       IBeamRenderer BeamRenderer = itemConfig.renderDroplightBeam ? DroplightRenderer::renderBeam : LootBeamRenderer::renderBeam;
/*     */       
/* 117 */       BeamRenderer.renderBeam(buffer, stack, item, itemConfig, color, sizeMultiplier, fadeAlpha, currentGroundTime, worldtime, pticks);
/*     */       
/* 119 */       stack.method_22909();
/*     */       
/* 121 */       NameTagRenderer.renderNameTags(buffer, stack, item, itemConfig, color, fadeAlpha, currentGroundTime, worldtime, pticks);
/* 122 */       ParticleEmitter.createParticlesForItem(item, itemConfig, item.method_6985(), color, fadeAlpha, pticks);
/*     */     } 
/*     */     
/* 125 */     RenderSystem.disableDepthTest();
/* 126 */     ITEM_GROUND_START_TIMES.keySet().removeIf(itemEntity -> !itemEntity.method_24828());
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\render\BeamRender.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */