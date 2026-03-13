/*    */ package com.lootbeams.render;
/*    */ import com.lootbeams.compat.iris.IrisCompat;
/*    */ import com.lootbeams.shaders.LootBeamShaders;
/*    */ import com.mojang.blaze3d.platform.GlStateManager;
/*    */ import com.mojang.blaze3d.systems.RenderSystem;
/*    */ import net.minecraft.class_1060;
/*    */ import net.minecraft.class_287;
/*    */ import net.minecraft.class_289;
/*    */ import net.minecraft.class_290;
/*    */ import net.minecraft.class_293;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_5944;
/*    */ import net.minecraft.class_757;
/*    */ 
/*    */ public class ParticleRenderType implements class_3999 {
/* 16 */   private LootBeamShaders.Shader shader = LootBeamShaders.Shader.PARTICLE_OVERLAY;
/*    */   
/*    */   private class_2960 texture;
/*    */   
/*    */   public ParticleRenderType setTexture(class_2960 texture) {
/* 21 */     this.texture = texture;
/* 22 */     return this;
/*    */   }
/*    */   
/*    */   public ParticleRenderType setShader(LootBeamShaders.Shader shader) {
/* 26 */     this.shader = shader;
/* 27 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public class_287 method_18130(class_289 builder, class_1060 textureManager) {
/* 32 */     RenderSystem.enableBlend();
/* 33 */     RenderSystem.enableCull();
/* 34 */     RenderSystem.depthFunc(515);
/* 35 */     GL11.glEnable(3042);
/* 36 */     RenderSystem.depthMask(false);
/* 37 */     RenderSystem.colorMask(true, true, true, true);
/*    */     
/* 39 */     boolean shadersLoaded = IrisCompat.isShaderPackInUse();
/*    */     
/* 41 */     RenderSystem.setShaderTexture(0, this.texture);
/* 42 */     if (shadersLoaded) {
/* 43 */       RenderSystem.setShader(class_757::method_42595);
/*    */     } else {
/* 45 */       RenderSystem.setShader(() -> LootBeamShaders.getShader(this.shader));
/*    */     } 
/*    */     
/* 48 */     RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 56 */     if (shadersLoaded) {
/* 57 */       return builder.method_60827(class_293.class_5596.field_27382, class_290.field_1580);
/*    */     }
/*    */     
/* 60 */     return builder.method_60827(class_293.class_5596.field_27382, class_290.field_1584);
/*    */   }
/*    */   
/*    */   public void end(class_289 tessellator) {
/* 64 */     tessellator.method_60828();
/* 65 */     RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
/*    */     
/* 67 */     RenderSystem.disableBlend();
/* 68 */     RenderSystem.depthMask(true);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\render\ParticleRenderType.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */