/*    */ package com.lootbeams.managers;
/*    */ 
/*    */ import com.lootbeams.LootBeams;
/*    */ import com.lootbeams.extensions.AnimatedTexture;
/*    */ import com.lootbeams.vfx.VFXParticleType;
/*    */ import java.util.List;
/*    */ import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
/*    */ import net.minecraft.class_1059;
/*    */ import net.minecraft.class_2378;
/*    */ import net.minecraft.class_2396;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_310;
/*    */ import net.minecraft.class_7923;
/*    */ 
/*    */ public class ParticleManager
/*    */ {
/* 17 */   public static final class_2960 GLOW_TEXTURE_ID = LootBeams.id("glow");
/* 18 */   public static final ParticleTexture GLOW_TEXTURE = ParticleTexture.of(GLOW_TEXTURE_ID);
/*    */   public static VFXParticleType GLOW_PARTICLE;
/*    */   
/*    */   public static void registerParticles() {
/* 22 */     GLOW_PARTICLE = (VFXParticleType)class_2378.method_10230(class_7923.field_41180, GLOW_TEXTURE_ID, new VFXParticleType(true));
/* 23 */     ParticleFactoryRegistry.getInstance().register((class_2396)GLOW_PARTICLE, com.lootbeams.vfx.VFXParticle.Factory::new);
/*    */   }
/*    */   
/*    */   public static List<ParticleTexture> getTextures() {
/* 27 */     return ParticleTexture.getAnimatedTextures(ParticleTexture.getAtlasTexture(), ParticleTexture.class);
/*    */   }
/*    */   
/*    */   public static class ParticleTexture extends AnimatedTexture {
/*    */     public ParticleTexture(class_2960 textureId) {
/* 32 */       super(textureId);
/*    */     }
/*    */     
/*    */     public static ParticleTexture of(class_2960 textureId) {
/* 36 */       return (ParticleTexture)of(textureId, ParticleTexture.class);
/*    */     }
/*    */     
/*    */     public static ParticleTexture of(String path, String namespace) {
/* 40 */       return (ParticleTexture)of(path, namespace, ParticleTexture.class);
/*    */     }
/*    */     
/*    */     public static class_1059 getAtlasTexture() {
/* 44 */       return (class_310.method_1551()).field_1713.field_18301;
/*    */     }
/*    */ 
/*    */     
/*    */     public class_1059 getSpriteAtlasTexture() {
/* 49 */       return getAtlasTexture();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\ParticleManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */