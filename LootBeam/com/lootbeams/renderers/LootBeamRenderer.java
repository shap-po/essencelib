/*     */ package com.lootbeams.renderers;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.helpers.ColorHelper;
/*     */ import com.lootbeams.helpers.NumberHelper;
/*     */ import com.lootbeams.render.LootBeamRenderLayers;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_1921;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4588;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_5251;
/*     */ import net.minecraft.class_7833;
/*     */ import org.joml.Matrix3f;
/*     */ 
/*     */ public class LootBeamRenderer {
/*  18 */   private static final class_2960 LOOT_BEAM_TEXTURE = LootBeams.id("textures/lootbeam/lootbeam.png");
/*  19 */   private static final class_2960 LOOT_BEAM_WHITE_TEXTURE = LootBeams.id("textures/lootbeam/lootbeam_white.png");
/*     */   
/*  21 */   private static final class_1921 LOOT_BEAM_CENTER_LAYER = LootBeamRenderLayers.lootBeamCenter();
/*  22 */   private static class_1921 LOOT_BEAM_LAYER = updateLootBeamLayer();
/*     */   
/*     */   private static class_1921 updateLootBeamLayer() {
/*  25 */     class_2960 beamTexture = !LootBeams.config.solidBeam ? LOOT_BEAM_TEXTURE : LOOT_BEAM_WHITE_TEXTURE;
/*     */     
/*  27 */     return LootBeams.config.glowingBeam ? 
/*  28 */       LootBeamRenderLayers.lootBeamLightning() : 
/*  29 */       LootBeamRenderLayers.lootBeam(beamTexture);
/*     */   }
/*     */   
/*     */   public static void onConfigurationChange() {
/*  33 */     LOOT_BEAM_LAYER = updateLootBeamLayer();
/*     */   }
/*     */   
/*     */   public static void renderBeam(class_4597.class_4598 buffer, class_4587 matrixStack, class_1542 itemEntity, Configuration itemConfig, class_5251 color, float beamSizeMultiplier, float fadeAlpha, float currentGroundTime, long worldtime, float pticks) {
/*  37 */     if (!itemConfig.renderBeam) {
/*     */       return;
/*     */     }
/*     */     
/*  41 */     ColorHelper.Color beamColor = ColorHelper.Color.of(color);
/*  42 */     ColorHelper.Color centerColor = ColorHelper.Color.of(color).applyModifiers(List.of("+r64", "+g64", "+b64"));
/*     */     
/*  44 */     float beamAlpha = itemConfig.beamAlpha * fadeAlpha;
/*  45 */     float glowBeamAlpha = beamAlpha * 0.4F;
/*  46 */     float beamHeight = itemConfig.beamHeight * beamSizeMultiplier;
/*  47 */     if (beamHeight < itemConfig.minBeamHeight) {
/*  48 */       beamHeight = itemConfig.minBeamHeight;
/*     */     }
/*  50 */     float beamRadius = 0.05F * itemConfig.beamRadius * beamSizeMultiplier;
/*  51 */     float minBeamRadius = 0.05F * itemConfig.minBeamRadius;
/*  52 */     if (beamRadius < minBeamRadius) {
/*  53 */       beamRadius = minBeamRadius;
/*     */     }
/*  55 */     float beamGlowRadius = beamRadius + beamRadius * 0.2F;
/*  56 */     float yOffset = itemConfig.beamYOffset;
/*  57 */     if (itemConfig.commonShorterBeam && 
/*  58 */       !RarityHelper.rarityCheck(itemEntity.method_6983(), false)) {
/*  59 */       beamHeight *= 0.65F;
/*     */     }
/*     */     
/*  62 */     if (itemConfig.smoothBeamSize) {
/*  63 */       beamHeight = NumberHelper.smoothValue(beamHeight, currentGroundTime, itemConfig.smoothDuration);
/*  64 */       yOffset = NumberHelper.smoothValue(yOffset, currentGroundTime, itemConfig.smoothDuration);
/*  65 */       beamRadius = NumberHelper.smoothValue(beamRadius, currentGroundTime, itemConfig.smoothDuration);
/*  66 */       beamGlowRadius = NumberHelper.smoothValue(beamGlowRadius, currentGroundTime, itemConfig.smoothDuration);
/*     */     } 
/*     */ 
/*     */     
/*  70 */     matrixStack.method_22903();
/*  71 */     float rotation = (float)Math.floorMod(worldtime, 40L) + pticks;
/*  72 */     matrixStack.method_22907(class_7833.field_40716.rotationDegrees(rotation * 2.25F - 45.0F));
/*  73 */     renderBeamLayer(matrixStack, buffer.getBuffer(LOOT_BEAM_LAYER), beamColor, beamAlpha, 0.0F, beamHeight, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius, itemConfig.solidBeam, yOffset);
/*  74 */     matrixStack.method_22909();
/*     */ 
/*     */     
/*  77 */     renderBeamLayer(matrixStack, buffer.getBuffer(LOOT_BEAM_LAYER), beamColor, glowBeamAlpha, 0.0F, beamHeight, -beamGlowRadius, -beamGlowRadius, beamGlowRadius, -beamGlowRadius, -beamGlowRadius, beamGlowRadius, beamGlowRadius, beamGlowRadius, itemConfig.solidBeam, yOffset);
/*  78 */     buffer.method_22993();
/*     */ 
/*     */     
/*  81 */     if (itemConfig.whiteCenter) {
/*  82 */       renderBeamLayer(matrixStack, buffer.getBuffer(LOOT_BEAM_CENTER_LAYER), centerColor, beamAlpha, 0.0F, beamHeight, 0.0F, beamRadius * 0.4F, beamRadius * 0.4F, 0.0F, -beamRadius * 0.4F, 0.0F, 0.0F, -beamRadius * 0.4F, itemConfig.solidBeam, yOffset);
/*  83 */       buffer.method_22993();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void renderBeamLayer(class_4587 stack, class_4588 builder, ColorHelper.Color color, float alpha, float y, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8, boolean solidBeam, float yOffset) {
/*  88 */     float halfHeight = height / 2.0F;
/*  89 */     stack.method_22903();
/*  90 */     stack.method_46416(0.0F, yOffset, 0.0F);
/*  91 */     if (!solidBeam) {
/*  92 */       stack.method_46416(0.0F, halfHeight, 0.0F);
/*  93 */       stack.method_22907(class_7833.field_40714.rotationDegrees(180.0F));
/*     */     } 
/*  95 */     renderPart(stack, builder, color.fR, color.fG, color.fB, alpha, y, height, radius_1, radius_2, radius_3, radius_4, radius_5, radius_6, radius_7, radius_8, false);
/*  96 */     if (!solidBeam) {
/*  97 */       stack.method_22907(class_7833.field_40714.rotationDegrees(-180.0F));
/*     */     }
/*  99 */     renderPart(stack, builder, color.fR, color.fG, color.fB, alpha, y, height, radius_1, radius_2, radius_3, radius_4, radius_5, radius_6, radius_7, radius_8, solidBeam);
/* 100 */     stack.method_22909();
/*     */   }
/*     */   
/*     */   private static void renderPart(class_4587 stack, class_4588 builder, float red, float green, float blue, float alpha, float y, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8, boolean gradient) {
/* 104 */     if (gradient) {
/* 105 */       renderGradientPart(stack, builder, red, green, blue, alpha, y, height, radius_1, radius_2, radius_3, radius_4, radius_5, radius_6, radius_7, radius_8);
/*     */     } else {
/* 107 */       renderPart(stack, builder, red, green, blue, alpha, y, height, radius_1, radius_2, radius_3, radius_4, radius_5, radius_6, radius_7, radius_8);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void renderPart(class_4587 stack, class_4588 builder, float red, float green, float blue, float alpha, float y, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8) {
/* 112 */     class_4587.class_4665 matrixentry = stack.method_23760();
/* 113 */     Matrix3f matrixnormal = matrixentry.method_23762();
/* 114 */     renderQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_1, radius_2, radius_3, radius_4);
/* 115 */     renderQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_7, radius_8, radius_5, radius_6);
/* 116 */     renderQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_3, radius_4, radius_7, radius_8);
/* 117 */     renderQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_5, radius_6, radius_1, radius_2);
/*     */   }
/*     */   
/*     */   private static void renderGradientPart(class_4587 stack, class_4588 builder, float red, float green, float blue, float alpha, float y, float height, float radius_1, float radius_2, float radius_3, float radius_4, float radius_5, float radius_6, float radius_7, float radius_8) {
/* 121 */     class_4587.class_4665 matrixentry = stack.method_23760();
/* 122 */     Matrix3f matrixnormal = matrixentry.method_23762();
/* 123 */     renderUpwardsGradientQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_1, radius_2, radius_3, radius_4);
/* 124 */     renderUpwardsGradientQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_7, radius_8, radius_5, radius_6);
/* 125 */     renderUpwardsGradientQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_3, radius_4, radius_7, radius_8);
/* 126 */     renderUpwardsGradientQuad(matrixentry, matrixnormal, builder, red, green, blue, alpha, y, height, radius_5, radius_6, radius_1, radius_2);
/*     */   }
/*     */   
/*     */   private static void renderQuad(class_4587.class_4665 stack, Matrix3f normal, class_4588 builder, float red, float green, float blue, float alpha, float y, float height, float x1, float z1, float x0, float z0) {
/* 130 */     addVertex(builder, stack, normal, red, green, blue, alpha, y + height / 2.0F, x1, z1, 1.0F, 0.0F);
/* 131 */     addVertex(builder, stack, normal, red, green, blue, alpha, y, x1, z1, 1.0F, 1.0F);
/* 132 */     addVertex(builder, stack, normal, red, green, blue, alpha, y, x0, z0, 0.0F, 1.0F);
/* 133 */     addVertex(builder, stack, normal, red, green, blue, alpha, y + height / 2.0F, x0, z0, 0.0F, 0.0F);
/*     */   }
/*     */   
/*     */   private static void renderUpwardsGradientQuad(class_4587.class_4665 stack, Matrix3f normal, class_4588 builder, float red, float green, float blue, float alpha, float y, float height, float z1, float texu1, float z, float texu) {
/* 137 */     addVertex(builder, stack, normal, red, green, blue, 0.0F, y + height, z1, texu1, 1.0F, 0.0F);
/* 138 */     addVertex(builder, stack, normal, red, green, blue, alpha, y + height / 2.0F, z1, texu1, 1.0F, 1.0F);
/* 139 */     addVertex(builder, stack, normal, red, green, blue, alpha, y + height / 2.0F, z, texu, 0.0F, 1.0F);
/* 140 */     addVertex(builder, stack, normal, red, green, blue, 0.0F, y + height, z, texu, 0.0F, 0.0F);
/*     */   }
/*     */   
/*     */   private static void addVertex(class_4588 builder, class_4587.class_4665 stack, Matrix3f normal, float red, float green, float blue, float alpha, float y, float x, float z, float texu, float texv) {
/* 144 */     builder.method_22918(stack.method_23761(), x, y, z).method_22915(red, green, blue, alpha).method_22913(texu, texv).method_22922(class_4608.field_21444).method_60803(15728880).method_60831(stack, 0.0F, 1.0F, 0.0F);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\renderers\LootBeamRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */