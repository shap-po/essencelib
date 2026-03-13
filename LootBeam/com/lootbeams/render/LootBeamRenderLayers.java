/*     */ package com.lootbeams.render;
/*     */ 
/*     */ import com.lootbeams.compat.iris.IrisCompat;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import com.mojang.blaze3d.platform.GlStateManager;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import net.minecraft.class_1921;
/*     */ import net.minecraft.class_290;
/*     */ import net.minecraft.class_293;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_4668;
/*     */ import net.minecraft.class_5944;
/*     */ 
/*     */ public class LootBeamRenderLayers {
/*  15 */   private static final class_4668.class_4685 LOOTBEAM_TRANSPARENCY = new class_4668.class_4685("loot_beam_transparency", () -> {
/*     */         RenderSystem.enableBlend();
/*     */         RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA.value, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA.value, GlStateManager.class_4535.ONE.value, GlStateManager.class_4534.ZERO.value);
/*     */       }() -> {
/*     */         RenderSystem.disableBlend();
/*     */         RenderSystem.defaultBlendFunc();
/*     */       });
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
/*     */   public static class_1921 lootBeamLightning() {
/*  38 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_34578(class_4668.field_29429).method_23616(class_4668.field_21350).method_23615(LOOTBEAM_TRANSPARENCY).method_23603(class_4668.field_21345).method_23617(false);
/*     */     
/*  40 */     return (class_1921)class_1921.method_24049("loot_beam_lightning", class_290.field_1590, class_293.class_5596.field_27382, 1536, false, true, state);
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
/*     */   public static class_1921 lootBeam(class_2960 texture) {
/*  58 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_34577((class_4668.class_5939)new class_4668.class_4683(texture, false, false)).method_23615(class_4668.field_21370).method_34578(class_4668.field_29409).method_23616(class_4668.field_21350).method_23603(class_4668.field_21345).method_23617(false);
/*     */     
/*  60 */     return (class_1921)class_1921.method_24049("loot_beam", class_290.field_1590, class_293.class_5596.field_27382, 1536, false, true, state);
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
/*     */   public static class_1921 lootBeamCenter() {
/*  77 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_23615(class_4668.field_21367).method_34578(class_4668.field_29429).method_23616(class_4668.field_21349).method_23610(class_4668.field_25282).method_23617(false);
/*     */     
/*  79 */     return (class_1921)class_1921.method_24049("loot_beam_center", class_290.field_1576, class_293.class_5596.field_27382, 1536, false, true, state);
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
/*     */   public static class_1921 droplight(class_2960 texture) {
/*  91 */     if (IrisCompat.isShaderPackInUse()) {
/*  92 */       return class_1921.method_42599(texture, false);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_34577((class_4668.class_5939)new class_4668.class_4683(texture, false, false)).method_34578(new class_4668.class_5942(() -> LootBeamShaders.getShader(LootBeamShaders.DroplightShader.DROPLIGHT))).method_23616(class_4668.field_21350).method_23603(class_4668.field_21345).method_23615(class_4668.field_21370).method_23617(false);
/*     */     
/* 103 */     return (class_1921)class_1921.method_24049("loot_beam_droplight", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM, class_293.class_5596.field_27382, 1536, false, true, state);
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
/*     */   public static class_1921 droplightAnimated(class_2960 texture) {
/* 121 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_34577((class_4668.class_5939)new class_4668.class_4683(texture, false, false)).method_34578(new class_4668.class_5942(() -> LootBeamShaders.getShader(LootBeamShaders.DroplightShader.DROPLIGHT_ANIMATED))).method_23616(class_4668.field_21350).method_23603(class_4668.field_21345).method_23615(class_4668.field_21370).method_23617(false);
/*     */     
/* 123 */     return (class_1921)class_1921.method_24049("loot_beam_droplight_animated", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM, class_293.class_5596.field_27382, 1536, false, true, state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class_1921 groundGlowEffect(class_2960 texture, boolean isColored, boolean useGlowGradient, LootBeamShaders.CustomShader customShader) {
/*     */     class_4668.class_5942 shaderProgram;
/*     */     class_293 vertexFormat;
/* 135 */     if (IrisCompat.isShaderPackInUse()) {
/* 136 */       return class_1921.method_42599(texture, false);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 141 */     class_5944 customShaderProgram = LootBeamShaders.getShader(customShader);
/*     */     
/* 143 */     if (customShader == LootBeamShaders.CustomShader.NONE || customShaderProgram == null) {
/* 144 */       if (isColored) {
/* 145 */         shaderProgram = new class_4668.class_5942(() -> LootBeamShaders.getShader(LootBeamShaders.Shader.GLOW_OVERLAY));
/* 146 */         vertexFormat = class_290.field_1575;
/*     */       }
/* 148 */       else if (useGlowGradient) {
/* 149 */         shaderProgram = new class_4668.class_5942(() -> LootBeamShaders.getShader(LootBeamShaders.DroplightShader.DROPLIGHT_GLOW));
/* 150 */         vertexFormat = CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER;
/*     */       } else {
/* 152 */         shaderProgram = new class_4668.class_5942(() -> LootBeamShaders.getShader(LootBeamShaders.Shader.ADD));
/* 153 */         vertexFormat = class_290.field_1575;
/*     */       } 
/*     */     } else {
/*     */       
/* 157 */       shaderProgram = new class_4668.class_5942(() -> customShaderProgram);
/* 158 */       vertexFormat = CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 167 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_34577((class_4668.class_5939)new class_4668.class_4683(texture, false, false)).method_34578(shaderProgram).method_23616(class_4668.field_21350).method_23603(class_4668.field_21345).method_23615(class_4668.field_21370).method_23617(false);
/*     */     
/* 169 */     return (class_1921)class_1921.method_24049("loot_beam_ground_glow", vertexFormat, class_293.class_5596.field_27382, 1536, false, true, state);
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
/*     */   public static class_1921 translucentNoCull(class_2960 texture) {
/* 187 */     class_1921.class_4688 state = class_1921.class_4688.method_23598().method_34577((class_4668.class_5939)new class_4668.class_4683(texture, false, false)).method_34578(class_4668.field_38344).method_23616(class_4668.field_21350).method_23603(class_4668.field_21345).method_23615(class_4668.field_21370).method_23617(false);
/*     */     
/* 189 */     return (class_1921)class_1921.method_24049("loot_beam_translucent", class_290.field_1580, class_293.class_5596.field_27382, 1536, false, true, state);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\render\LootBeamRenderLayers.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */