/*     */ package com.lootbeams.renderers;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.compat.iris.IrisCompat;
/*     */ import com.lootbeams.compat.prism.PrismCompat;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.extensions.LootbeamsBufferBuilder;
/*     */ import com.lootbeams.helpers.ColorHelper;
/*     */ import com.lootbeams.helpers.NumberHelper;
/*     */ import com.lootbeams.render.LootBeamRenderLayers;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import java.util.List;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_1921;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_4184;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4588;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_4608;
/*     */ import net.minecraft.class_5251;
/*     */ import org.joml.Matrix4f;
/*     */ 
/*     */ public class DroplightRenderer {
/*  25 */   private static final class_2960 DROPLIGHT_TEXTURE = LootBeams.id("textures/droplight/droplight.png");
/*  26 */   private static final class_2960 DROPLIGHT_GLOW_TEXTURE = LootBeams.id("textures/droplight/droplight_glow.png");
/*  27 */   private static final class_2960 DROPLIGHT_ANIMATED_TEXTURE = LootBeams.id("textures/droplight/droplight_animated.png");
/*  28 */   private static final class_2960 DROPLIGHT_ANIMATED_BASE_TEXTURE = LootBeams.id("textures/droplight/droplight_animated_base.png");
/*     */   
/*  30 */   private static class_1921 DROPLIGHT_LAYER = LootBeamRenderLayers.droplight(DROPLIGHT_TEXTURE);
/*  31 */   private static class_1921 DROPLIGHT_GLOW_LAYER = LootBeamRenderLayers.droplight(DROPLIGHT_GLOW_TEXTURE);
/*  32 */   private static class_1921 DROPLIGHT_ANIMATED_LAYER = LootBeamRenderLayers.droplightAnimated(DROPLIGHT_ANIMATED_TEXTURE);
/*  33 */   private static class_1921 DROPLIGHT_BASE_LAYER = LootBeamRenderLayers.droplight(DROPLIGHT_ANIMATED_BASE_TEXTURE);
/*     */   
/*  35 */   private static boolean SHADERS_LOADED = IrisCompat.isShaderPackInUse();
/*     */   
/*     */   public static void renderBeam(class_4597.class_4598 buffer, class_4587 matrixStack, class_1542 itemEntity, Configuration itemConfig, class_5251 color, float beamSizeMultiplier, float fadeAlpha, float currentGroundTime, long worldtime, float pticks) {
/*  38 */     if (!itemConfig.renderBeam) {
/*     */       return;
/*     */     }
/*     */     
/*  42 */     class_310 minecraft = class_310.method_1551();
/*  43 */     class_4184 camera = minecraft.field_1773.method_19418();
/*     */     
/*  45 */     boolean updatedShadersLoaded = IrisCompat.isShaderPackInUse();
/*  46 */     if (updatedShadersLoaded != SHADERS_LOADED) {
/*  47 */       SHADERS_LOADED = updatedShadersLoaded;
/*     */       
/*  49 */       DROPLIGHT_LAYER = LootBeamRenderLayers.droplight(DROPLIGHT_TEXTURE);
/*  50 */       DROPLIGHT_GLOW_LAYER = LootBeamRenderLayers.droplight(DROPLIGHT_GLOW_TEXTURE);
/*  51 */       DROPLIGHT_ANIMATED_LAYER = LootBeamRenderLayers.droplightAnimated(DROPLIGHT_ANIMATED_TEXTURE);
/*  52 */       DROPLIGHT_BASE_LAYER = LootBeamRenderLayers.droplight(DROPLIGHT_ANIMATED_BASE_TEXTURE);
/*     */     } 
/*     */     
/*  55 */     float beamAlpha = itemConfig.beamAlpha * fadeAlpha;
/*  56 */     float beamHeight = itemConfig.beamHeight * beamSizeMultiplier;
/*  57 */     float beamGlowHeight = 2.5F * beamSizeMultiplier;
/*  58 */     if (beamHeight < itemConfig.minBeamHeight) {
/*  59 */       beamHeight = itemConfig.minBeamHeight;
/*  60 */       beamAlpha = beamHeight * 0.2F;
/*     */     } 
/*  62 */     float beamWidth = itemConfig.beamRadius * beamSizeMultiplier;
/*  63 */     if (beamWidth < itemConfig.minBeamRadius) {
/*  64 */       beamWidth = itemConfig.minBeamRadius;
/*     */     }
/*  66 */     float yOffset = itemConfig.beamYOffset;
/*  67 */     if (itemConfig.commonShorterBeam && 
/*  68 */       !RarityHelper.rarityCheck(itemEntity.method_6983(), false)) {
/*  69 */       beamHeight *= 0.65F;
/*     */     }
/*     */     
/*  72 */     if (itemConfig.smoothBeamSize) {
/*  73 */       beamHeight = NumberHelper.smoothValue(beamHeight, currentGroundTime, itemConfig.smoothDuration);
/*  74 */       beamWidth = NumberHelper.smoothValue(beamWidth, currentGroundTime, itemConfig.smoothDuration);
/*  75 */       yOffset = NumberHelper.smoothValue(yOffset, currentGroundTime, itemConfig.smoothDuration);
/*     */     } 
/*     */     
/*  78 */     class_5251 color2 = getSecondColor(color, itemConfig.beamGradientModifiers);
/*     */     
/*  80 */     RenderSystem.setShaderTexture(1, minecraft.method_1522().method_30278());
/*  81 */     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, beamAlpha);
/*     */     
/*  83 */     matrixStack.method_22903();
/*  84 */     matrixStack.method_22904(0.0D, 0.015D, 0.0D);
/*  85 */     matrixStack.method_22903();
/*  86 */     matrixStack.method_22907(class_7833.field_40716.rotationDegrees(-camera.method_19330()));
/*     */     
/*  88 */     class_4588 builder = buffer.getBuffer(DROPLIGHT_GLOW_LAYER);
/*  89 */     renderQuad(builder, matrixStack, color.method_27716(), color2.method_27716(), SHADERS_LOADED ? (beamAlpha / 2.0F) : beamAlpha, 0.0F, yOffset, -0.001F, beamWidth, beamGlowHeight, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F, true, SHADERS_LOADED);
/*  90 */     buffer.method_22993();
/*     */     
/*  92 */     if (beamHeight > 0.0F) {
/*  93 */       if (itemConfig.animateDroplightBeam && !SHADERS_LOADED) {
/*  94 */         float halfBeamWidth = beamWidth / 2.0F;
/*  95 */         float oneThreeBeamWidth = beamWidth / 3.0F;
/*  96 */         float animationSpeed = itemConfig.droplightBeamAnimationSpeed;
/*  97 */         float itemAgeInSeconds = (itemEntity.method_6985() + minecraft.method_60646().method_60637(true)) / 20.0F;
/*     */ 
/*     */         
/* 100 */         builder = buffer.getBuffer(DROPLIGHT_ANIMATED_LAYER);
/* 101 */         renderAnimatedQuad(builder, matrixStack, color.method_27716(), color2.method_27716(), beamAlpha, 0.0F, halfBeamWidth - oneThreeBeamWidth + yOffset, 0.0F, beamWidth, beamHeight, 0.0F, 0.0F, 1.0F, 1.0F, true, animationSpeed, itemAgeInSeconds);
/* 102 */         buffer.method_22993();
/*     */ 
/*     */         
/* 105 */         builder = buffer.getBuffer(DROPLIGHT_BASE_LAYER);
/* 106 */         renderQuad(builder, matrixStack, color.method_27716(), color2.method_27716(), beamAlpha, 0.0F, -oneThreeBeamWidth + yOffset, 0.0F, beamWidth, beamWidth, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, false, SHADERS_LOADED);
/* 107 */         buffer.method_22993();
/*     */       } else {
/* 109 */         builder = buffer.getBuffer(DROPLIGHT_LAYER);
/* 110 */         renderQuad(builder, matrixStack, color.method_27716(), color2.method_27716(), beamAlpha, 0.0F, yOffset, 0.0F, beamWidth, beamHeight, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, true, SHADERS_LOADED);
/* 111 */         buffer.method_22993();
/*     */       } 
/*     */     }
/* 114 */     matrixStack.method_22909();
/* 115 */     matrixStack.method_22909();
/* 116 */     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
/*     */   }
/*     */   
/*     */   public static class_5251 getSecondColor(float r, float g, float b, float a, List<String> modifiers) {
/* 120 */     int R = (int)(r * 255.0F);
/* 121 */     int G = (int)(g * 255.0F);
/* 122 */     int B = (int)(b * 255.0F);
/* 123 */     int A = (int)(a * 255.0F);
/*     */     
/* 125 */     return getSecondColor(class_5251.method_27717(ColorHelper.build(A, R, G, B)), modifiers);
/*     */   }
/*     */   
/*     */   public static class_5251 getSecondColor(class_5251 color, List<String> modifiers) {
/* 129 */     if (PrismCompat.isPrismLoaded()) {
/* 130 */       return PrismCompat.applyModifiers(modifiers, color);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 138 */     ColorHelper.Color newColor = (new ColorHelper.Color(color.method_27716())).applyModifiers(modifiers);
/*     */     
/* 140 */     return class_5251.method_27717(newColor
/* 141 */         .getRgb() & 0xFFFFFF);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void renderQuad(class_4588 builder, class_4587 matrixStack, int color, int color2, float alpha, float x, float y, float z, float w, float h, float u, float v, float u2, float v2, float alphaMultiplier, boolean fade, boolean shadersLoaded) {
/* 146 */     class_4587.class_4665 stack = matrixStack.method_23760();
/* 147 */     Matrix4f positionMatrix = stack.method_23761();
/* 148 */     float alpha2 = fade ? 0.0F : alpha;
/* 149 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 150 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 151 */     float blue = (color >> 0 & 0xFF) / 255.0F;
/* 152 */     int red2 = color2 >> 16 & 0xFF;
/* 153 */     int green2 = color2 >> 8 & 0xFF;
/* 154 */     int blue2 = color2 >> 0 & 0xFF;
/*     */     
/* 156 */     if (shadersLoaded) {
/* 157 */       builder.method_22918(positionMatrix, x - w / 2.0F, y, z).method_22913(u, v2).method_22915(red, green, blue, alpha).method_22922(class_4608.field_21444).method_60803(15728880).method_60831(stack, 0.0F, 1.0F, 0.0F);
/* 158 */       builder.method_22918(positionMatrix, x + w / 2.0F, y, z).method_22913(u2, v2).method_22915(red, green, blue, alpha).method_22922(class_4608.field_21444).method_60803(15728880).method_60831(stack, 0.0F, 1.0F, 0.0F);
/* 159 */       builder.method_22918(positionMatrix, x + w / 2.0F, y + h, z).method_22913(u2, v).method_22915(red, green, blue, alpha2).method_22922(class_4608.field_21444).method_60803(15728880).method_60831(stack, 0.0F, 1.0F, 0.0F);
/* 160 */       builder.method_22918(positionMatrix, x - w / 2.0F, y + h, z).method_22913(u, v).method_22915(red, green, blue, alpha2).method_22922(class_4608.field_21444).method_60803(15728880).method_60831(stack, 0.0F, 1.0F, 0.0F);
/*     */       
/*     */       return;
/*     */     } 
/* 164 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x - w / 2.0F, y, z).method_22913(u, v2).method_22915(red, green, blue, alpha)).color1(red2, green2, blue2, (int)(alpha * 255.0F)).shortCustomData(alphaMultiplier, 0.0F);
/* 165 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x + w / 2.0F, y, z).method_22913(u2, v2).method_22915(red, green, blue, alpha)).color1(red2, green2, blue2, (int)(alpha * 255.0F)).shortCustomData(alphaMultiplier, 0.0F);
/* 166 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x + w / 2.0F, y + h, z).method_22913(u2, v).method_22915(red, green, blue, alpha2)).color1(red2, green2, blue2, (int)(alpha2 * 255.0F)).shortCustomData(alphaMultiplier, 0.0F);
/* 167 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x - w / 2.0F, y + h, z).method_22913(u, v).method_22915(red, green, blue, alpha2)).color1(red2, green2, blue2, (int)(alpha2 * 255.0F)).shortCustomData(alphaMultiplier, 0.0F);
/*     */   }
/*     */   
/*     */   private static void renderAnimatedQuad(class_4588 builder, class_4587 matrixStack, int color, int color2, float alpha, float x, float y, float z, float w, float h, float u, float v, float u2, float v2, boolean fade, float animationSpeed, float itemAge) {
/* 171 */     Matrix4f positionMatrix = matrixStack.method_23760().method_23761();
/* 172 */     float alpha2 = fade ? 0.0F : alpha;
/* 173 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 174 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 175 */     float blue = (color >> 0 & 0xFF) / 255.0F;
/* 176 */     int red2 = color2 >> 16 & 0xFF;
/* 177 */     int green2 = color2 >> 8 & 0xFF;
/* 178 */     int blue2 = color2 >> 0 & 0xFF;
/*     */     
/* 180 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x - w / 2.0F, y, z).method_22913(u, v2).method_22915(red, green, blue, alpha)).color1(red2, green2, blue2, (int)(alpha * 255.0F)).longCustomData(w, h, animationSpeed, itemAge);
/* 181 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x + w / 2.0F, y, z).method_22913(u2, v2).method_22915(red, green, blue, alpha)).color1(red2, green2, blue2, (int)(alpha * 255.0F)).longCustomData(w, h, animationSpeed, itemAge);
/* 182 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x + w / 2.0F, y + h, z).method_22913(u2, v).method_22915(red, green, blue, alpha2)).color1(red2, green2, blue2, (int)(alpha2 * 255.0F)).longCustomData(w, h, animationSpeed, itemAge);
/* 183 */     ((LootbeamsBufferBuilder)builder.method_22918(positionMatrix, x - w / 2.0F, y + h, z).method_22913(u, v).method_22915(red, green, blue, alpha2)).color1(red2, green2, blue2, (int)(alpha2 * 255.0F)).longCustomData(w, h, animationSpeed, itemAge);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\renderers\DroplightRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */