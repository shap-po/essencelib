/*    */ package com.lootbeams.managers;
/*    */ 
/*    */ import com.lootbeams.extensions.AnimatedTexture;
/*    */ import net.minecraft.class_1059;
/*    */ import net.minecraft.class_2960;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GlowEffectTexture
/*    */   extends AnimatedTexture
/*    */ {
/*    */   public GlowEffectTexture(class_2960 textureId) {
/* 43 */     super(textureId);
/*    */   }
/*    */   
/*    */   public static GlowEffectTexture of(class_2960 textureId) {
/* 47 */     return (GlowEffectTexture)of(textureId, GlowEffectTexture.class);
/*    */   }
/*    */   
/*    */   public static GlowEffectTexture of(String path, String namespace) {
/* 51 */     return (GlowEffectTexture)of(path, namespace, GlowEffectTexture.class);
/*    */   }
/*    */   
/*    */   public static class_1059 getAtlasTexture() {
/* 55 */     return GlowEffectManager.ATLAS_TEXTURE;
/*    */   }
/*    */ 
/*    */   
/*    */   public class_1059 getSpriteAtlasTexture() {
/* 60 */     return getAtlasTexture();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\GlowEffectManager$GlowEffectTexture.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */