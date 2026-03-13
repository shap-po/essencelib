/*     */ package com.lootbeams.features;
/*     */ 
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.dconfig.DynamicConfig;
/*     */ import com.lootbeams.managers.GlowEffectManager;
/*     */ import com.lootbeams.managers.ParticleManager;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2487;
/*     */ import net.minecraft.class_3518;
/*     */ import net.minecraft.class_9279;
/*     */ import net.minecraft.class_9334;
/*     */ 
/*     */ public class CustomLootBeamsConfig
/*     */ {
/*     */   private static final String CONFIG_KEY = "lootbeams_config";
/*  25 */   private static final Map<class_1799, Configuration> CONFIG_CACHE = new WeakHashMap<>();
/*     */   
/*     */   public static void clearConfigCache() {
/*  28 */     CONFIG_CACHE.clear();
/*     */   }
/*     */   
/*     */   public static void onConfigurationChange() {
/*  32 */     clearConfigCache();
/*     */   }
/*     */   
/*     */   public static void onResourcesReload() {
/*  36 */     clearConfigCache();
/*     */   }
/*     */   
/*     */   private static Configuration getConfigCopy(Configuration configInstance) {
/*  40 */     Configuration configCopy = new Configuration();
/*     */     
/*  42 */     for (Field field : configInstance.getClass().getDeclaredFields()) {
/*     */       try {
/*  44 */         Field fieldRef = configCopy.getClass().getDeclaredField(field.getName());
/*  45 */         fieldRef.setAccessible(true);
/*  46 */         fieldRef.set(configCopy, field.get(configInstance));
/*  47 */       } catch (Exception ex) {
/*  48 */         ex.printStackTrace();
/*     */       } 
/*     */     } 
/*     */     
/*  52 */     return configCopy;
/*     */   }
/*     */   
/*     */   private static Configuration processConfig(class_2487 customConfig) {
/*  56 */     Configuration configCopy = getConfigCopy(LootBeams.config);
/*     */     
/*  58 */     List<DynamicConfig.Control.Field> fieldsToProcess = LootBeams.configManager.getFieldsByGroup("visual");
/*     */     
/*  60 */     for (DynamicConfig.Control.Field field : fieldsToProcess) {
/*     */       try {
/*  62 */         Field fieldRef = configCopy.getClass().getDeclaredField(field.key);
/*  63 */         fieldRef.setAccessible(true);
/*     */         
/*  65 */         if (!customConfig.method_33133() && customConfig.method_10545(field.saveKey)) {
/*  66 */           if (field.type == boolean.class) {
/*  67 */             fieldRef.set(configCopy, Boolean.valueOf(customConfig.method_10577(field.saveKey)));
/*     */           }
/*  69 */           if (field.type == int.class) {
/*  70 */             fieldRef.set(configCopy, Integer.valueOf(customConfig.method_10550(field.saveKey)));
/*     */           }
/*  72 */           if (field.type == long.class) {
/*  73 */             fieldRef.set(configCopy, Long.valueOf(customConfig.method_10537(field.saveKey)));
/*     */           }
/*  75 */           if (field.type == float.class) {
/*  76 */             fieldRef.set(configCopy, Float.valueOf(customConfig.method_10583(field.saveKey)));
/*     */           }
/*  78 */           if (field.type == double.class) {
/*  79 */             fieldRef.set(configCopy, Double.valueOf(customConfig.method_10574(field.saveKey)));
/*     */           }
/*  81 */           if (field.type == String.class) {
/*  82 */             fieldRef.set(configCopy, customConfig.method_10558(field.saveKey));
/*     */           }
/*  84 */           if (field.type == List.class) {
/*  85 */             String value = customConfig.method_10558(field.saveKey);
/*  86 */             List<String> values = Arrays.<String>stream(value.split(",")).map(String::trim).toList();
/*  87 */             fieldRef.set(configCopy, values);
/*     */           } 
/*  89 */           if (field.type == ParticleManager.ParticleTexture.class) {
/*  90 */             String textureKey = customConfig.method_10558(field.saveKey);
/*  91 */             if (textureKey.contains(":")) {
/*  92 */               String[] parts = textureKey.split(":");
/*  93 */               fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, parts[0]));
/*     */             
/*     */             }
/*     */             else {
/*     */               
/*  98 */               fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, "lootbeams"));
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 104 */           if (field.type == GlowEffectManager.GlowEffectTexture.class) {
/* 105 */             String textureKey = customConfig.method_10558(field.saveKey);
/* 106 */             if (textureKey.contains(":")) {
/* 107 */               String[] parts = textureKey.split(":");
/* 108 */               fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, parts[0]));
/*     */             
/*     */             }
/*     */             else {
/*     */               
/* 113 */               fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, "lootbeams"));
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 119 */           if (field.type == LootBeamShaders.Shader.class) {
/* 120 */             fieldRef.set(configCopy, LootBeamShaders.Shader.valueOf(customConfig
/* 121 */                   .method_10558(field.saveKey).toUpperCase()));
/*     */           }
/*     */           
/* 124 */           if (field.type == LootBeamShaders.CustomShader.class) {
/* 125 */             fieldRef.set(configCopy, LootBeamShaders.CustomShader.valueOf(customConfig
/* 126 */                   .method_10558(field.saveKey).toUpperCase()));
/*     */           }
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 132 */         fieldRef.set(configCopy, LootBeams.configManager.getFieldValue(field.key));
/* 133 */       } catch (Exception ex) {
/* 134 */         ex.printStackTrace();
/*     */       } 
/*     */     } 
/*     */     
/* 138 */     return configCopy;
/*     */   }
/*     */   
/*     */   private static Configuration processConfig(JsonObject customConfig) {
/* 142 */     Configuration configCopy = getConfigCopy(LootBeams.config);
/*     */     
/* 144 */     List<DynamicConfig.Control.Field> fieldsToProcess = LootBeams.configManager.getFieldsByGroup("visual");
/*     */     
/* 146 */     for (DynamicConfig.Control.Field field : fieldsToProcess) {
/*     */       try {
/* 148 */         Field fieldRef = configCopy.getClass().getDeclaredField(field.key);
/* 149 */         fieldRef.setAccessible(true);
/*     */         
/* 151 */         if (!customConfig.isEmpty() && customConfig.has(field.saveKey)) {
/* 152 */           if (field.type == boolean.class) {
/* 153 */             fieldRef.set(configCopy, Boolean.valueOf(class_3518.method_15270(customConfig, field.saveKey)));
/*     */           }
/* 155 */           if (field.type == int.class) {
/* 156 */             fieldRef.set(configCopy, Integer.valueOf(class_3518.method_15260(customConfig, field.saveKey)));
/*     */           }
/* 158 */           if (field.type == long.class) {
/* 159 */             fieldRef.set(configCopy, Long.valueOf(class_3518.method_22449(customConfig, field.saveKey)));
/*     */           }
/* 161 */           if (field.type == float.class) {
/* 162 */             fieldRef.set(configCopy, Float.valueOf(class_3518.method_15259(customConfig, field.saveKey)));
/*     */           }
/* 164 */           if (field.type == double.class) {
/* 165 */             fieldRef.set(configCopy, Double.valueOf(class_3518.method_34927(customConfig, field.saveKey)));
/*     */           }
/* 167 */           if (field.type == String.class) {
/* 168 */             fieldRef.set(configCopy, class_3518.method_15265(customConfig, field.saveKey));
/*     */           }
/* 170 */           if (field.type == List.class) {
/*     */ 
/*     */ 
/*     */             
/* 174 */             List<String> values = class_3518.method_15261(customConfig, field.saveKey).asList().stream().map(JsonElement::getAsString).toList();
/* 175 */             fieldRef.set(configCopy, values);
/*     */           } 
/* 177 */           if (field.type == ParticleManager.ParticleTexture.class) {
/* 178 */             String textureKey = class_3518.method_15265(customConfig, field.saveKey);
/* 179 */             if (textureKey.contains(":")) {
/* 180 */               String[] parts = textureKey.split(":");
/* 181 */               fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, parts[0]));
/*     */             
/*     */             }
/*     */             else {
/*     */               
/* 186 */               fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, "lootbeams"));
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 192 */           if (field.type == GlowEffectManager.GlowEffectTexture.class) {
/* 193 */             String textureKey = class_3518.method_15265(customConfig, field.saveKey);
/* 194 */             if (textureKey.contains(":")) {
/* 195 */               String[] parts = textureKey.split(":");
/* 196 */               fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, parts[0]));
/*     */             
/*     */             }
/*     */             else {
/*     */               
/* 201 */               fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, "lootbeams"));
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 207 */           if (field.type == LootBeamShaders.Shader.class) {
/* 208 */             fieldRef.set(configCopy, LootBeamShaders.Shader.valueOf(
/* 209 */                   class_3518.method_15265(customConfig, field.saveKey).toUpperCase()));
/*     */           }
/*     */           
/* 212 */           if (field.type == LootBeamShaders.CustomShader.class) {
/* 213 */             fieldRef.set(configCopy, LootBeamShaders.CustomShader.valueOf(
/* 214 */                   class_3518.method_15265(customConfig, field.saveKey).toUpperCase()));
/*     */           }
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 220 */         fieldRef.set(configCopy, LootBeams.configManager.getFieldValue(field.key));
/* 221 */       } catch (Exception ex) {
/* 222 */         ex.printStackTrace();
/*     */       } 
/*     */     } 
/*     */     
/* 226 */     return configCopy;
/*     */   }
/*     */   
/*     */   public static Configuration fromItemStack(class_1799 itemStack) {
/* 230 */     if (CONFIG_CACHE.containsKey(itemStack)) {
/* 231 */       return CONFIG_CACHE.get(itemStack);
/*     */     }
/*     */     
/* 234 */     if (itemStack.method_57353().method_57832(class_9334.field_49628)) {
/* 235 */       class_9279 itemCustomData = (class_9279)itemStack.method_57824(class_9334.field_49628);
/*     */       
/* 237 */       if (itemCustomData != null) {
/* 238 */         class_2487 customDataNbt = itemCustomData.method_57461();
/*     */         
/* 240 */         if (!customDataNbt.method_33133()) {
/* 241 */           class_2487 customConfig = customDataNbt.method_10562("lootbeams_config");
/*     */           
/* 243 */           if (!customConfig.method_33133()) {
/* 244 */             Configuration newConfig = processConfig(customConfig);
/* 245 */             CONFIG_CACHE.put(itemStack, newConfig);
/* 246 */             return newConfig;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 253 */     CustomRarity itemRarity = CustomRarity.fromItemStack(itemStack);
/* 254 */     if (itemRarity != null && itemRarity.hasRarityConfig()) {
/* 255 */       Configuration newConfig = processConfig(itemRarity.getRarityConfig());
/* 256 */       CONFIG_CACHE.put(itemStack, newConfig);
/* 257 */       return newConfig;
/*     */     } 
/*     */     
/* 260 */     return LootBeams.config;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\features\CustomLootBeamsConfig.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */