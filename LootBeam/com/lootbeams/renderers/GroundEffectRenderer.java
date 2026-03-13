/*     */ package com.lootbeams.renderers;
/*     */ 
/*     */ import com.lootbeams.compat.iris.IrisCompat;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.extensions.LootbeamsBufferBuilder;
/*     */ import com.lootbeams.helpers.ColorHelper;
/*     */ import com.lootbeams.helpers.NumberHelper;
/*     */ import com.lootbeams.managers.GlowEffectManager;
/*     */ import com.lootbeams.render.LootBeamRenderLayers;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import java.util.List;
/*     */ import net.minecraft.class_1058;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_1921;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4588;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_4608;
/*     */ import net.minecraft.class_5251;
/*     */ import net.minecraft.class_7833;
/*     */ import org.joml.Matrix4f;
/*     */ 
/*     */ public class GroundEffectRenderer {
/*     */   public static void renderGroundEffect(class_4597.class_4598 buffer, class_4587 matrixStack, class_1542 itemEntity, Configuration itemConfig, class_5251 color, float sizeMultiplier, float fadeAlpha, float currentGroundTime, long worldtime, float pticks) {
/*  25 */     ColorHelper.Color effectColor = ColorHelper.Color.of(color);
/*     */     
/*  27 */     if (itemConfig.glowEffect) {
/*  28 */       matrixStack.method_22903();
/*  29 */       matrixStack.method_46416(0.0F, 0.001F, 0.0F);
/*  30 */       if (itemConfig.rotateGlow) {
/*  31 */         float rotationSpeed = itemConfig.glowRotationSpeed;
/*  32 */         float rotation = currentGroundTime * rotationSpeed % 360.0F;
/*  33 */         float rotationDirection = itemConfig.glowRotateClockwise ? -1.0F : 1.0F;
/*  34 */         matrixStack.method_22907(class_7833.field_40716.rotationDegrees(rotation * rotationDirection));
/*     */       } 
/*  36 */       class_1058 glowEffectSprite = itemConfig.glowEffectTexture.getSprite();
/*  37 */       if (IrisCompat.isShaderPackInUse() && itemConfig.glowEffectTexture == GlowEffectManager.GLOW_TEXTURE) {
/*  38 */         glowEffectSprite = GlowEffectManager.GLOW_SHADER_TEXTURE.getSprite();
/*     */       }
/*  40 */       if (!IrisCompat.isShaderPackInUse() && itemConfig.glowEffectTexture == GlowEffectManager.GLOW_SHADER_TEXTURE) {
/*  41 */         glowEffectSprite = GlowEffectManager.GLOW_TEXTURE.getSprite();
/*     */       }
/*  43 */       class_1921 glowLayer = LootBeamRenderLayers.groundGlowEffect(glowEffectSprite.method_45852(), itemConfig.glowEffectTexture.isColored(), itemConfig.useGlowGradient, itemConfig.glowCustomShader);
/*  44 */       float radius = itemConfig.glowEffectRadius;
/*  45 */       float glowEffectAlpha = fadeAlpha * itemConfig.glowEffectAlpha;
/*     */       
/*  47 */       if (itemConfig.pulseGlow) {
/*  48 */         float pulseSpeed = itemConfig.pulseGlowSpeed / 10.0F;
/*  49 */         float pulseMinAlpha = itemConfig.pulseGlowMinAlpha;
/*  50 */         float pulseMaxAlpha = itemConfig.pulseGlowMaxAlpha;
/*  51 */         float pulseMinRadius = itemConfig.pulseGlowMinRadius;
/*  52 */         float pulseMaxRadius = itemConfig.pulseGlowMaxRadius;
/*     */         
/*  54 */         float cosineFactor = (float)Math.cos((currentGroundTime * pulseSpeed));
/*  55 */         float normalizedCosine = cosineFactor * 0.5F + 0.5F;
/*     */         
/*  57 */         float pulseAlpha = (float)NumberHelper.clampedMapRange(
/*  58 */             Float.valueOf(normalizedCosine), 
/*  59 */             Integer.valueOf(0), 
/*  60 */             Integer.valueOf(1), 
/*  61 */             Float.valueOf(pulseMinAlpha), 
/*  62 */             Float.valueOf(pulseMaxAlpha));
/*     */ 
/*     */         
/*  65 */         float pulseRadius = (float)NumberHelper.clampedMapRange(
/*  66 */             Float.valueOf(normalizedCosine), 
/*  67 */             Integer.valueOf(0), 
/*  68 */             Integer.valueOf(1), 
/*  69 */             Float.valueOf(pulseMinRadius), 
/*  70 */             Float.valueOf(pulseMaxRadius));
/*     */ 
/*     */         
/*  73 */         glowEffectAlpha = pulseAlpha * fadeAlpha;
/*  74 */         radius = pulseRadius;
/*     */       } 
/*     */       
/*  77 */       radius *= sizeMultiplier;
/*  78 */       if (radius < itemConfig.pulseGlowMinRadius) {
/*  79 */         radius = itemConfig.pulseGlowMinRadius;
/*     */       }
/*     */       
/*  82 */       float glowR = effectColor.fR;
/*  83 */       float glowG = effectColor.fG;
/*  84 */       float glowB = effectColor.fB;
/*     */       
/*  86 */       if (itemConfig.glowEffectTexture.isColored()) {
/*  87 */         float averageColor = LootBeamShaders.getAverageColor(LootBeamShaders.Shader.GLOW_OVERLAY);
/*  88 */         glowR = averageColor;
/*  89 */         glowG = averageColor;
/*  90 */         glowB = averageColor;
/*     */       } 
/*     */       
/*  93 */       if (itemConfig.smoothGlowEffectRadius) {
/*  94 */         radius = NumberHelper.smoothValue(radius, currentGroundTime, itemConfig.smoothDuration);
/*     */       }
/*  96 */       if (itemConfig.smoothGlowEffectAlpha) {
/*  97 */         glowEffectAlpha = NumberHelper.smoothValue(glowEffectAlpha, currentGroundTime, itemConfig.smoothDuration);
/*     */       }
/*     */       
/* 100 */       boolean useGlowGradient = ((!itemConfig.glowEffectTexture.isColored() && itemConfig.useGlowGradient) || itemConfig.glowCustomShader != LootBeamShaders.CustomShader.NONE);
/*     */ 
/*     */ 
/*     */       
/* 104 */       renderGlow(matrixStack, buffer.getBuffer(glowLayer), glowR, glowG, glowB, glowEffectAlpha, radius, glowEffectSprite, useGlowGradient, itemConfig.glowGradientModifiers, itemConfig.glowGradientStart / 100.0F, itemConfig.glowGradientEnd / 100.0F);
/* 105 */       buffer.method_22993();
/* 106 */       matrixStack.method_22909();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static LootbeamsBufferBuilder addGlowVertex(class_4588 builder, class_4587.class_4665 matrixEntry, float x, float y, float z, float red, float green, float blue, float alpha, float u, float v) {
/* 111 */     Matrix4f poseMatrix = matrixEntry.method_23761();
/*     */     
/* 113 */     return (LootbeamsBufferBuilder)builder.method_22918(poseMatrix, x, y, z).method_22915(red, green, blue, alpha).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(15728880).method_60831(matrixEntry, 0.0F, 1.0F, 0.0F);
/*     */   }
/*     */   
/*     */   private static void modifyGlowVertex(LootbeamsBufferBuilder builder, ColorHelper.Color color1, float centerU, float centerV, float uvWidth, float uvHeight, float gradientStart, float gradientEnd, boolean useGlowGradient) {
/* 117 */     if (useGlowGradient) {
/* 118 */       builder
/* 119 */         .uvCenter(centerU, centerV)
/* 120 */         .uvSize(uvWidth, uvHeight)
/* 121 */         .shortCustomData(gradientStart, gradientEnd)
/* 122 */         .color1(color1.R, color1.G, color1.B, color1.A);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void renderGlow(class_4587 stack, class_4588 builder, float red, float green, float blue, float alpha, float radius, class_1058 glowSprite, boolean useGlowGradient, List<String> gradientModifiers, float gradientStart, float gradientEnd) {
/* 127 */     class_4587.class_4665 matrixentry = stack.method_23760();
/*     */     
/* 129 */     boolean shadersLoaded = IrisCompat.isShaderPackInUse();
/*     */     
/* 131 */     float minX = glowSprite.method_4594();
/* 132 */     float maxX = glowSprite.method_4577();
/* 133 */     float minY = glowSprite.method_4593();
/* 134 */     float maxY = glowSprite.method_4575();
/*     */     
/* 136 */     float centerU = (minX + maxX) / 2.0F;
/* 137 */     float centerV = (minY + maxY) / 2.0F;
/* 138 */     float uvWidth = maxX - minX;
/* 139 */     float uvHeight = maxY - minY;
/*     */     
/* 141 */     ColorHelper.Color secondColor = ColorHelper.Color.of(DroplightRenderer.getSecondColor(red, green, blue, alpha, gradientModifiers));
/*     */ 
/*     */     
/* 144 */     modifyGlowVertex(
/* 145 */         addGlowVertex(builder, matrixentry, -radius, 0.0F, -radius, red, green, blue, alpha, minX, minY), secondColor, centerU, centerV, uvWidth, uvHeight, gradientStart, gradientEnd, (useGlowGradient && !shadersLoaded));
/*     */ 
/*     */     
/* 148 */     modifyGlowVertex(
/* 149 */         addGlowVertex(builder, matrixentry, -radius, 0.0F, radius, red, green, blue, alpha, minX, maxY), secondColor, centerU, centerV, uvWidth, uvHeight, gradientStart, gradientEnd, (useGlowGradient && !shadersLoaded));
/*     */ 
/*     */     
/* 152 */     modifyGlowVertex(
/* 153 */         addGlowVertex(builder, matrixentry, radius, 0.0F, radius, red, green, blue, alpha, maxX, maxY), secondColor, centerU, centerV, uvWidth, uvHeight, gradientStart, gradientEnd, (useGlowGradient && !shadersLoaded));
/*     */ 
/*     */     
/* 156 */     modifyGlowVertex(
/* 157 */         addGlowVertex(builder, matrixentry, radius, 0.0F, -radius, red, green, blue, alpha, maxX, minY), secondColor, centerU, centerV, uvWidth, uvHeight, gradientStart, gradientEnd, (useGlowGradient && !shadersLoaded));
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\renderers\GroundEffectRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */