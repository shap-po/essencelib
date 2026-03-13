/*    */ package com.lootbeams.managers;
/*    */ 
/*    */ import com.lootbeams.extensions.AnimatedTexture;
/*    */ import net.minecraft.class_1059;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_310;
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
/*    */ public class ParticleTexture
/*    */   extends AnimatedTexture
/*    */ {
/*    */   public ParticleTexture(class_2960 textureId) {
/* 32 */     super(textureId);
/*    */   }
/*    */   
/*    */   public static ParticleTexture of(class_2960 textureId) {
/* 36 */     return (ParticleTexture)of(textureId, ParticleTexture.class);
/*    */   }
/*    */   
/*    */   public static ParticleTexture of(String path, String namespace) {
/* 40 */     return (ParticleTexture)of(path, namespace, ParticleTexture.class);
/*    */   }
/*    */   
/*    */   public static class_1059 getAtlasTexture() {
/* 44 */     return (class_310.method_1551()).field_1713.field_18301;
/*    */   }
/*    */ 
/*    */   
/*    */   public class_1059 getSpriteAtlasTexture() {
/* 49 */     return getAtlasTexture();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\ParticleManager$ParticleTexture.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */