/*    */ package com.lootbeams.managers;
/*    */ 
/*    */ import com.lootbeams.LootBeams;
/*    */ import com.lootbeams.extensions.AnimatedTexture;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.Executor;
/*    */ import net.minecraft.class_1044;
/*    */ import net.minecraft.class_1059;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_310;
/*    */ import net.minecraft.class_3300;
/*    */ import net.minecraft.class_7766;
/*    */ 
/*    */ public class GlowEffectManager {
/* 16 */   public static final class_2960 GLOW_EFFECTS_PATH = LootBeams.id("glow_effects");
/* 17 */   public static final class_2960 ATLAS_ID = LootBeams.id("textures/atlas/glow_effects.png");
/* 18 */   public static final class_2960 GLOW_TEXTURE_ID = LootBeams.id("glow");
/* 19 */   public static final class_2960 GLOW_SHADER_TEXTURE_ID = LootBeams.id("glow_shaders");
/* 20 */   public static final GlowEffectTexture GLOW_TEXTURE = GlowEffectTexture.of(GLOW_TEXTURE_ID);
/* 21 */   public static final GlowEffectTexture GLOW_SHADER_TEXTURE = GlowEffectTexture.of(GLOW_SHADER_TEXTURE_ID);
/* 22 */   public static class_1059 ATLAS_TEXTURE = null;
/*    */   
/*    */   public static void onResourceManagerReload(class_3300 resourceManager, Executor prepareExecutor) {
/* 25 */     if (ATLAS_TEXTURE == null) {
/* 26 */       class_1059 atlasTexture = new class_1059(ATLAS_ID);
/* 27 */       class_310.method_1551().method_1531().method_4616(atlasTexture.method_24106(), (class_1044)atlasTexture);
/* 28 */       ATLAS_TEXTURE = atlasTexture;
/*    */     } 
/*    */     
/* 31 */     CompletableFuture<class_7766.class_7767> completableFuture = class_7766.method_45837(ATLAS_TEXTURE).method_52849(resourceManager, GLOW_EFFECTS_PATH, 0, prepareExecutor).thenCompose(class_7766.class_7767::method_45845);
/* 32 */     ATLAS_TEXTURE.method_4601();
/* 33 */     class_7766.class_7767 stitchResult = completableFuture.join();
/* 34 */     ATLAS_TEXTURE.method_45848(stitchResult);
/*    */   }
/*    */   
/*    */   public static List<GlowEffectTexture> getTextures() {
/* 38 */     return GlowEffectTexture.getAnimatedTextures(GlowEffectTexture.getAtlasTexture(), GlowEffectTexture.class);
/*    */   }
/*    */   
/*    */   public static class GlowEffectTexture extends AnimatedTexture {
/*    */     public GlowEffectTexture(class_2960 textureId) {
/* 43 */       super(textureId);
/*    */     }
/*    */     
/*    */     public static GlowEffectTexture of(class_2960 textureId) {
/* 47 */       return (GlowEffectTexture)of(textureId, GlowEffectTexture.class);
/*    */     }
/*    */     
/*    */     public static GlowEffectTexture of(String path, String namespace) {
/* 51 */       return (GlowEffectTexture)of(path, namespace, GlowEffectTexture.class);
/*    */     }
/*    */     
/*    */     public static class_1059 getAtlasTexture() {
/* 55 */       return GlowEffectManager.ATLAS_TEXTURE;
/*    */     }
/*    */ 
/*    */     
/*    */     public class_1059 getSpriteAtlasTexture() {
/* 60 */       return getAtlasTexture();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\GlowEffectManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */