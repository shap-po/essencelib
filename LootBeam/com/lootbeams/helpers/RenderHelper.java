/*     */ package com.lootbeams.helpers;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import com.mojang.blaze3d.platform.GlStateManager;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import net.minecraft.class_1044;
/*     */ import net.minecraft.class_1058;
/*     */ import net.minecraft.class_286;
/*     */ import net.minecraft.class_287;
/*     */ import net.minecraft.class_289;
/*     */ import net.minecraft.class_293;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_768;
/*     */ import org.joml.Matrix4f;
/*     */ 
/*     */ public class RenderHelper {
/*     */   public static void bindTexture(class_2960 texture) {
/*  20 */     class_310 minecraft = class_310.method_1551();
/*  21 */     class_1044 abstractTexture = minecraft.method_1531().method_4619(texture);
/*  22 */     abstractTexture.method_23207();
/*     */   }
/*     */ 
/*     */   
/*     */   public static class_768 getCurrentTextureSize() {
/*  27 */     int textureWidth = GlStateManager._getTexLevelParameter(3553, 0, 4096);
/*  28 */     int textureHeight = GlStateManager._getTexLevelParameter(3553, 0, 4097);
/*     */     
/*  30 */     return new class_768(0, 0, textureWidth, textureHeight);
/*     */   }
/*     */   
/*     */   public static class_768 getSpritePositionAndSize(class_1058 sprite) {
/*  34 */     return new class_768(sprite.method_35806(), sprite.method_35807(), sprite.method_45851().method_45807(), sprite.method_45851().method_45815());
/*     */   }
/*     */   
/*     */   public static void blit(class_332 context, class_2960 texture, int x, int y, int width, int height, int texX, int texY, int regionWidth, int regionHeight) {
/*  38 */     bindTexture(texture);
/*  39 */     class_768 textureSize = getCurrentTextureSize();
/*     */     
/*  41 */     blit(context, texture, x, y, width, height, texX, texY, regionWidth, regionHeight, textureSize.method_3319(), textureSize.method_3320());
/*     */   }
/*     */   
/*     */   public static void blit(class_332 context, class_2960 texture, int x, int y, int width, int height, int texX, int texY) {
/*  45 */     blit(context, texture, x, y, width, height, texX, texY, width, height);
/*     */   }
/*     */   
/*     */   public static void blit(class_332 context, class_2960 texture, int x, int y) {
/*  49 */     bindTexture(texture);
/*  50 */     class_768 textureSize = getCurrentTextureSize();
/*     */     
/*  52 */     blit(context, texture, x, y, textureSize.method_3319(), textureSize.method_3320(), 0, 0);
/*     */   }
/*     */   
/*     */   public static void blit(class_332 context, class_2960 texture, int x, int y, int width, int height, float texX, float texY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
/*  56 */     context.method_25293(texture, x, y, width, height, texX, texY, regionWidth, regionHeight, textureWidth, textureHeight);
/*     */   }
/*     */   
/*     */   public static void drawColored(class_332 context, class_2960 texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, String color) {
/*  60 */     drawColored(context, texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight, color);
/*     */   }
/*     */   
/*     */   public static void drawColored(class_332 context, class_2960 texture, int x, int y, int width, int height, int texX, int texY, int regionWidth, int regionHeight, String color) {
/*  64 */     bindTexture(texture);
/*  65 */     class_768 textureSize = getCurrentTextureSize();
/*     */     
/*  67 */     drawColored(context, texture, x, y, width, height, texX, texY, regionWidth, regionHeight, textureSize.method_3319(), textureSize.method_3320(), color);
/*     */   }
/*     */   
/*     */   public static void drawColored(class_332 context, class_2960 texture, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight, String color) {
/*  71 */     drawColoredBlit(context, texture, x0, x1, y0, y1, z, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight, color);
/*     */   }
/*     */   
/*     */   public static void drawColoredBlit(class_332 context, class_2960 texture, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, String color) {
/*  75 */     RenderSystem.setShaderTexture(0, texture);
/*  76 */     RenderSystem.setShader(() -> LootBeamShaders.getShader(LootBeamShaders.Shader.PARTICLE_OVERLAY));
/*  77 */     RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
/*     */     
/*  79 */     Matrix4f matrix4f = context.method_51448().method_23760().method_23761();
/*  80 */     class_287 bufferBuilder = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1584);
/*     */     
/*  82 */     if (color.isEmpty()) {
/*  83 */       bufferBuilder.method_22918(matrix4f, x0, y0, z).method_22913(u0, v0).method_22915(0.5F, 0.5F, 0.5F, 1.0F).method_60803(15728880);
/*  84 */       bufferBuilder.method_22918(matrix4f, x0, y1, z).method_22913(u0, v1).method_22915(0.5F, 0.5F, 0.5F, 1.0F).method_60803(15728880);
/*  85 */       bufferBuilder.method_22918(matrix4f, x1, y1, z).method_22913(u1, v1).method_22915(0.5F, 0.5F, 0.5F, 1.0F).method_60803(15728880);
/*  86 */       bufferBuilder.method_22918(matrix4f, x1, y0, z).method_22913(u1, v0).method_22915(0.5F, 0.5F, 0.5F, 1.0F).method_60803(15728880);
/*     */     } else {
/*  88 */       int colorValue = ColorHelper.parseColor(color);
/*     */       
/*  90 */       float R = ColorHelper.getRed(colorValue) / 255.0F;
/*  91 */       float G = ColorHelper.getGreen(colorValue) / 255.0F;
/*  92 */       float B = ColorHelper.getBlue(colorValue) / 255.0F;
/*  93 */       float A = ColorHelper.getAlpha(colorValue) / 255.0F;
/*     */       
/*  95 */       bufferBuilder.method_22918(matrix4f, x0, y0, z).method_22913(u0, v0).method_22915(R, G, B, A).method_60803(15728880);
/*  96 */       bufferBuilder.method_22918(matrix4f, x0, y1, z).method_22913(u0, v1).method_22915(R, G, B, A).method_60803(15728880);
/*  97 */       bufferBuilder.method_22918(matrix4f, x1, y1, z).method_22913(u1, v1).method_22915(R, G, B, A).method_60803(15728880);
/*  98 */       bufferBuilder.method_22918(matrix4f, x1, y0, z).method_22913(u1, v0).method_22915(R, G, B, A).method_60803(15728880);
/*     */     } 
/*     */     
/* 101 */     class_286.method_43433(bufferBuilder.method_60800());
/*     */   }
/*     */   
/*     */   public static void blit(class_4587 poseStack, int x, int y, int width, int height, float texX, float texY, int texWidth, int texHeight, int fullWidth, int fullHeight) {
/* 105 */     blit(poseStack, x, x + width, y, y + height, 0, texWidth, texHeight, texX, texY, fullWidth, fullHeight);
/*     */   }
/*     */   
/*     */   public static void blit(class_4587 poseStack, int x0, int x1, int y0, int y1, int z, int texWidth, int texHeight, float texX, float texY, int fullWidth, int fullHeight) {
/* 109 */     innerBlit(poseStack, x0, x1, y0, y1, z, (texX + 0.0F) / fullWidth, (texX + texWidth) / fullWidth, (texY + 0.0F) / fullHeight, (texY + texHeight) / fullHeight);
/*     */   }
/*     */   
/*     */   private static void innerBlit(class_4587 poseStack, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
/* 113 */     RenderSystem.setShader(class_757::method_34542);
/* 114 */     Matrix4f matrix4f = poseStack.method_23760().method_23761();
/* 115 */     class_287 bufferBuilder = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1585);
/* 116 */     bufferBuilder.method_22918(matrix4f, x0, y0, z).method_22913(u0, v0);
/* 117 */     bufferBuilder.method_22918(matrix4f, x0, y1, z).method_22913(u0, v1);
/* 118 */     bufferBuilder.method_22918(matrix4f, x1, y1, z).method_22913(u1, v1);
/* 119 */     bufferBuilder.method_22918(matrix4f, x1, y0, z).method_22913(u1, v0);
/* 120 */     class_286.method_43433(bufferBuilder.method_60800());
/*     */   }
/*     */   
/*     */   public static void drawTextureAt(class_332 context, class_2960 texture, int x, int y) {
/* 124 */     blit(context, texture, x, y);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\RenderHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */