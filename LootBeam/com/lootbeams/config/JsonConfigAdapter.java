/*     */ package com.lootbeams.config;
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.lootbeams.dconfig.DynamicConfig;
/*     */ import com.lootbeams.dconfig.interfaces.DynamicConfigAdapter;
/*     */ import com.lootbeams.features.BeamOpacityOnApproach;
/*     */ import com.lootbeams.features.BeamSizeOnApproach;
/*     */ import com.lootbeams.managers.GlowEffectManager;
/*     */ import com.lootbeams.managers.ParticleManager;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import net.minecraft.class_3518;
/*     */ 
/*     */ public class JsonConfigAdapter implements DynamicConfigAdapter<Configuration> {
/*  23 */   private final Map<Class<?>, DynamicConfig.ConfigManager.LoadConsumer<Object, DynamicConfig.Control.Field, JsonObject>> TYPE_LOADERS = new HashMap<>();
/*  24 */   private final Map<Class<?>, DynamicConfig.ConfigManager.SaveConsumer<JsonElement, Object>> TYPE_SAVERS = new HashMap<>();
/*     */   
/*     */   public void registerTypeLoadConsumer(Class<?> type, DynamicConfig.ConfigManager.LoadConsumer<Object, DynamicConfig.Control.Field, JsonObject> consumer) {
/*  27 */     if (this.TYPE_LOADERS.containsKey(type)) {
/*     */       return;
/*     */     }
/*  30 */     this.TYPE_LOADERS.put(type, consumer);
/*     */   }
/*     */   
/*     */   public void registerTypeSaveConsumer(Class<?> type, DynamicConfig.ConfigManager.SaveConsumer<JsonElement, Object> consumer) {
/*  34 */     if (this.TYPE_SAVERS.containsKey(type)) {
/*     */       return;
/*     */     }
/*  37 */     this.TYPE_SAVERS.put(type, consumer);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getConfigFilePath() {
/*  42 */     return "lootbeams/config.json";
/*     */   }
/*     */ 
/*     */   
/*     */   public File getConfigFile() {
/*  47 */     return FabricLoader.getInstance().getConfigDir().resolve(getConfigFilePath()).toFile();
/*     */   }
/*     */   
/*     */   private JsonArray toArray(List<String> list) {
/*  51 */     JsonArray jsonArray = new JsonArray();
/*  52 */     for (String str : list) {
/*  53 */       jsonArray.add((JsonElement)new JsonPrimitive(str));
/*     */     }
/*     */     
/*  56 */     return jsonArray;
/*     */   }
/*     */   
/*     */   private List<String> toList(JsonArray jsonArray) {
/*  60 */     return jsonArray
/*  61 */       .asList().stream().map(JsonElement::getAsString)
/*  62 */       .toList();
/*     */   }
/*     */ 
/*     */   
/*     */   public void initialize(DynamicConfig.ConfigManager<Configuration> configManager) {
/*  67 */     registerTypeLoadConsumer(boolean.class, (field, group) -> Boolean.valueOf(class_3518.method_15258(group, field.saveKey, ((Boolean)field.defaultValue).booleanValue())));
/*  68 */     registerTypeLoadConsumer(int.class, (field, group) -> Integer.valueOf(class_3518.method_15282(group, field.saveKey, ((Integer)field.defaultValue).intValue())));
/*  69 */     registerTypeLoadConsumer(float.class, (field, group) -> Float.valueOf(class_3518.method_15277(group, field.saveKey, ((Float)field.defaultValue).floatValue())));
/*  70 */     registerTypeLoadConsumer(String.class, (field, group) -> class_3518.method_15253(group, field.saveKey, (String)field.defaultValue));
/*  71 */     registerTypeLoadConsumer(List.class, (field, group) -> toList(class_3518.method_15292(group, field.saveKey, toArray((List<String>)field.defaultValue))));
/*     */     
/*  73 */     registerTypeSaveConsumer(boolean.class, value -> new JsonPrimitive(Boolean.valueOf(((Boolean)value).booleanValue())));
/*  74 */     registerTypeSaveConsumer(int.class, value -> new JsonPrimitive(Integer.valueOf(((Integer)value).intValue())));
/*  75 */     registerTypeSaveConsumer(float.class, value -> new JsonPrimitive(Float.valueOf(((Float)value).floatValue())));
/*  76 */     registerTypeSaveConsumer(String.class, value -> new JsonPrimitive((String)value));
/*  77 */     registerTypeSaveConsumer(List.class, value -> toArray((List<String>)value));
/*     */ 
/*     */     
/*  80 */     registerTypeLoadConsumer(ParticleManager.ParticleTexture.class, (field, group) -> ParticleManager.ParticleTexture.of(class_3518.method_15253(group, field.saveKey, ((ParticleManager.ParticleTexture)field.defaultValue).path), "lootbeams"));
/*     */ 
/*     */ 
/*     */     
/*  84 */     registerTypeSaveConsumer(ParticleManager.ParticleTexture.class, value -> new JsonPrimitive(((ParticleManager.ParticleTexture)value).path));
/*     */     
/*  86 */     registerTypeLoadConsumer(GlowEffectManager.GlowEffectTexture.class, (field, group) -> GlowEffectManager.GlowEffectTexture.of(class_3518.method_15253(group, field.saveKey, ((GlowEffectManager.GlowEffectTexture)field.defaultValue).path), "lootbeams"));
/*     */ 
/*     */ 
/*     */     
/*  90 */     registerTypeSaveConsumer(GlowEffectManager.GlowEffectTexture.class, value -> new JsonPrimitive(((GlowEffectManager.GlowEffectTexture)value).path));
/*     */     
/*  92 */     registerTypeLoadConsumer(LootBeamShaders.Shader.class, (field, group) -> LootBeamShaders.Shader.valueOf(class_3518.method_15253(group, field.saveKey, ((LootBeamShaders.Shader)field.defaultValue).name()).toUpperCase()));
/*     */ 
/*     */     
/*  95 */     registerTypeSaveConsumer(LootBeamShaders.Shader.class, value -> new JsonPrimitive(((LootBeamShaders.Shader)value).name().toLowerCase()));
/*     */     
/*  97 */     registerTypeLoadConsumer(LootBeamShaders.CustomShader.class, (field, group) -> LootBeamShaders.CustomShader.valueOf(class_3518.method_15253(group, field.saveKey, ((LootBeamShaders.CustomShader)field.defaultValue).name()).toUpperCase()));
/*     */ 
/*     */     
/* 100 */     registerTypeSaveConsumer(LootBeamShaders.CustomShader.class, value -> new JsonPrimitive(((LootBeamShaders.CustomShader)value).name().toLowerCase()));
/*     */     
/* 102 */     registerTypeLoadConsumer(BeamOpacityOnApproach.class, (field, group) -> BeamOpacityOnApproach.valueOf(class_3518.method_15253(group, field.saveKey, ((BeamOpacityOnApproach)field.defaultValue).name()).toUpperCase()));
/*     */ 
/*     */     
/* 105 */     registerTypeSaveConsumer(BeamOpacityOnApproach.class, value -> new JsonPrimitive(((BeamOpacityOnApproach)value).name().toLowerCase()));
/*     */     
/* 107 */     registerTypeLoadConsumer(BeamSizeOnApproach.class, (field, group) -> BeamSizeOnApproach.valueOf(class_3518.method_15253(group, field.saveKey, ((BeamSizeOnApproach)field.defaultValue).name()).toUpperCase()));
/*     */ 
/*     */     
/* 110 */     registerTypeSaveConsumer(BeamSizeOnApproach.class, value -> new JsonPrimitive(((BeamSizeOnApproach)value).name().toLowerCase()));
/*     */   }
/*     */   
/*     */   public DynamicConfig.ConfigManager<Configuration> loadJson(DynamicConfig.ConfigManager<Configuration> configManager, JsonObject jsonObject) {
/* 114 */     if (jsonObject == null) {
/* 115 */       return configManager;
/*     */     }
/*     */     
/*     */     try {
/* 119 */       Map<String, JsonObject> groups = new HashMap<>();
/*     */       
/* 121 */       for (DynamicConfig.Control.Field field : configManager.getFields()) {
/* 122 */         if (!groups.containsKey(field.category.saveKey)) {
/* 123 */           groups.put(field.category.saveKey, class_3518.method_15281(jsonObject, field.category.saveKey, new JsonObject()));
/*     */         }
/*     */         
/* 126 */         JsonObject group = groups.get(field.category.saveKey);
/*     */         
/* 128 */         if (group == null || group.isJsonNull() || !group.has(field.saveKey)) {
/*     */           continue;
/*     */         }
/*     */         
/* 132 */         DynamicConfig.ConfigManager.LoadConsumer<Object, DynamicConfig.Control.Field, JsonObject> loadConsumer = this.TYPE_LOADERS.get(field.type);
/* 133 */         if (loadConsumer != null) {
/* 134 */           configManager.setFieldValue(field.key, loadConsumer.accept(field, group));
/*     */         }
/*     */       } 
/* 137 */     } catch (Exception e) {
/* 138 */       CrashManager.LOGGER.warn("cannot load config file", e);
/*     */     } 
/*     */     
/* 141 */     configManager.clearFieldValueCache();
/*     */     
/* 143 */     return configManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public DynamicConfig.ConfigManager<Configuration> load(DynamicConfig.ConfigManager<Configuration> configManager, InputStream inputStream) {
/* 148 */     JsonObject jsonObject = class_3518.method_15255(new InputStreamReader(inputStream));
/* 149 */     return loadJson(configManager, jsonObject);
/*     */   }
/*     */ 
/*     */   
/*     */   public DynamicConfig.ConfigManager<Configuration> load(DynamicConfig.ConfigManager<Configuration> configManager, File configFile) {
/*     */     try {
/* 155 */       JsonObject jsonObject = class_3518.method_15255(new InputStreamReader(new FileInputStream(configFile), "UTF8"));
/* 156 */       return loadJson(configManager, jsonObject);
/* 157 */     } catch (Exception e) {
/* 158 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DynamicConfigAdapter.SaveResult save(DynamicConfig.ConfigManager<Configuration> configManager, File saveFile) {
/* 164 */     JsonObject rootObject = new JsonObject();
/*     */     
/* 166 */     for (DynamicConfig.Control.Field field : configManager.getFields()) {
/* 167 */       DynamicConfig.ConfigManager.SaveConsumer<JsonElement, Object> saveConsumer = this.TYPE_SAVERS.get(field.type);
/* 168 */       if (saveConsumer != null) {
/*     */         try {
/* 170 */           Object value = configManager.getFieldValue(field.key);
/*     */           
/* 172 */           if (!rootObject.has(field.category.saveKey)) {
/* 173 */             rootObject.add(field.category.saveKey, (JsonElement)new JsonObject());
/*     */           }
/*     */           
/* 176 */           JsonObject category = rootObject.getAsJsonObject(field.category.saveKey);
/* 177 */           category.add(field.saveKey, (JsonElement)saveConsumer.accept(value));
/* 178 */         } catch (Exception ex) {
/* 179 */           ex.printStackTrace();
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 184 */     saveFile = FileHelper.createPathIfNotExists(saveFile.getPath()); 
/* 185 */     try { FileWriter jsonWriter = new FileWriter(saveFile); 
/* 186 */       try { Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
/* 187 */         gson.toJson((JsonElement)rootObject, jsonWriter);
/* 188 */         DynamicConfigAdapter.SaveResult saveResult = DynamicConfigAdapter.SaveResult.SUCCESS;
/* 189 */         jsonWriter.close(); return saveResult; } catch (Throwable throwable) { try { jsonWriter.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/* 190 */     { CrashManager.LOGGER.warn("Cannot save config file", e);
/* 191 */       return DynamicConfigAdapter.SaveResult.FAIL; }
/*     */   
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\config\JsonConfigAdapter.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */