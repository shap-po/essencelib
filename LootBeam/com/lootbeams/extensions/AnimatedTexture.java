/*     */ package com.lootbeams.extensions;
/*     */ import com.lootbeams.helpers.StringHelper;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ import net.minecraft.class_1047;
/*     */ import net.minecraft.class_1058;
/*     */ import net.minecraft.class_1059;
/*     */ import net.minecraft.class_2960;
/*     */ 
/*     */ public abstract class AnimatedTexture {
/*  16 */   private static final Map<Class<?>, Map<String, AnimatedTexture>> TEXTURE_CACHE = new HashMap<>();
/*     */   private static final String COLORED_POSTFIX = "_colored";
/*     */   public class_2960 id;
/*     */   public String path;
/*     */   private boolean isSpriteSplitted = false;
/*  21 */   private int frameCount = 0;
/*     */   
/*     */   public AnimatedTexture(class_2960 id) {
/*  24 */     this.id = id;
/*  25 */     this.path = id.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isMissing(class_1058 sprite) {
/*  31 */     return (sprite.method_45851().method_45816() == class_1047.method_4539());
/*     */   }
/*     */   
/*     */   public int getFrameCount() {
/*  35 */     return this.frameCount;
/*     */   }
/*     */   
/*     */   public boolean isColored() {
/*  39 */     return this.path.endsWith("_colored");
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
/*     */   private void processSplittedSprite() {
/*  53 */     List<class_1058> spriteList = (getSpriteAtlasTexture()).field_5280.values().stream().filter(sprite -> sprite.method_45851().method_45816().toString().matches(Pattern.quote(this.id.toString()) + "_\\d+")).toList();
/*  54 */     if (!spriteList.isEmpty()) {
/*  55 */       this.isSpriteSplitted = true;
/*  56 */       this.frameCount = spriteList.size();
/*     */     } 
/*     */   }
/*     */   
/*     */   public class_1058 getSprite() {
/*  61 */     class_1058 sprite = getSpriteAtlasTexture().method_4608(this.id);
/*  62 */     if (isMissing(sprite)) {
/*  63 */       return getSprite(0);
/*     */     }
/*  65 */     return sprite;
/*     */   }
/*     */   
/*     */   public class_1058 getSprite(int frameIndex) {
/*  69 */     class_1058 sprite = getSpriteAtlasTexture().method_4608(class_2960.method_60654(this.id.toString() + "_" + this.id.toString()));
/*  70 */     if (!isMissing(sprite) && !isSplitted()) {
/*  71 */       processSplittedSprite();
/*     */     }
/*  73 */     return sprite;
/*     */   }
/*     */   
/*     */   public class_2960 getAtlasId() {
/*  77 */     return getSpriteAtlasTexture().method_24106();
/*     */   }
/*     */   
/*     */   public boolean isSplitted() {
/*  81 */     return this.isSpriteSplitted;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  86 */     return this.path;
/*     */   }
/*     */   
/*     */   public String getDisplayName() {
/*  90 */     String displayName = this.path;
/*  91 */     if (displayName.endsWith("_colored")) {
/*  92 */       displayName = displayName.substring(0, displayName.length() - "_colored".length());
/*     */     }
/*  94 */     return StringHelper.capitalize(
/*  95 */         String.join(" ", (CharSequence[])displayName
/*     */ 
/*     */           
/*  98 */           .replace(this.id.method_12836() + ":", "")
/*  99 */           .split("_")));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends AnimatedTexture> Map<String, AnimatedTexture> getTextureCacheMap(Class<T> type) {
/* 105 */     if (!TEXTURE_CACHE.containsKey(type)) {
/* 106 */       TEXTURE_CACHE.put(type, new HashMap<>());
/*     */     }
/* 108 */     return TEXTURE_CACHE.get(type);
/*     */   }
/*     */   
/*     */   public static String toResourcePath(String displayName, String namespace) {
/* 112 */     return namespace + ":" + namespace;
/*     */   }
/*     */   
/*     */   public static <T extends AnimatedTexture> T of(class_2960 id, Class<T> type) {
/* 116 */     String resourcePath = id.toString();
/* 117 */     return of(resourcePath, "", type);
/*     */   }
/*     */   
/*     */   public static <T extends AnimatedTexture> T of(String path, String namespace, Class<T> type) {
/* 121 */     String resourcePath = path;
/* 122 */     if (!resourcePath.contains(namespace)) {
/* 123 */       resourcePath = toResourcePath(resourcePath, namespace);
/*     */     }
/* 125 */     Map<String, AnimatedTexture> MAP = getTextureCacheMap(type);
/* 126 */     if (MAP.containsKey(resourcePath)) {
/* 127 */       return type.cast(MAP.get(resourcePath));
/*     */     }
/*     */     try {
/* 130 */       Constructor<T> constructor = type.getConstructor(new Class[] { class_2960.class });
/* 131 */       AnimatedTexture animatedTexture = (AnimatedTexture)constructor.newInstance(new Object[] { class_2960.method_60654(resourcePath) });
/* 132 */       MAP.put(resourcePath, animatedTexture);
/* 133 */       return (T)animatedTexture;
/* 134 */     } catch (Exception e) {
/* 135 */       throw new RuntimeException("Failed to instantiate texture", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static <T extends AnimatedTexture> List<T> getAnimatedTextures(class_1059 atlasTexture, Class<T> type) {
/* 140 */     List<T> animatedTextures = new ArrayList<>();
/*     */     
/* 142 */     for (class_1058 sprite : atlasTexture.field_5280.values()) {
/* 143 */       if (sprite.method_45851().method_45816().method_12836().equals("lootbeams")) {
/* 144 */         animatedTextures.add(of(sprite.method_45851().method_45816(), type));
/*     */       }
/*     */     } 
/*     */     
/* 148 */     animatedTextures.sort(Comparator.comparing(AnimatedTexture::getDisplayName));
/*     */     
/* 150 */     return animatedTextures;
/*     */   }
/*     */   
/*     */   public abstract class_1059 getSpriteAtlasTexture();
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\extensions\AnimatedTexture.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */