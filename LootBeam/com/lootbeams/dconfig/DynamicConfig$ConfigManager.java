/*     */ package com.lootbeams.dconfig;
/*     */ 
/*     */ import com.lootbeams.dconfig.events.ConfigEvents;
/*     */ import com.lootbeams.dconfig.interfaces.DynamicConfigAdapter;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class ConfigManager<T>
/*     */ {
/* 152 */   private String MOD_ID = "";
/*     */ 
/*     */ 
/*     */   
/*     */   private T config;
/*     */ 
/*     */ 
/*     */   
/*     */   private List<DynamicConfig.Control.Field> fields;
/*     */ 
/*     */ 
/*     */   
/*     */   private final DynamicConfigAdapter<T> adapter;
/*     */ 
/*     */   
/*     */   private final Map<String, Object> FIELD_VALUE_CACHE;
/*     */ 
/*     */ 
/*     */   
/*     */   public String getConfigFilePath() {
/* 172 */     return this.adapter.getConfigFilePath();
/*     */   }
/*     */   
/*     */   public <T> void printConfig(T config) throws IllegalAccessException {
/* 176 */     Field[] classFields = config.getClass().getDeclaredFields();
/*     */     
/* 178 */     for (Field field : classFields) {
/* 179 */       System.out.println(field.getName() + ": " + field.getName());
/*     */     }
/*     */   }
/*     */   
/*     */   public void printFields() {
/* 184 */     for (DynamicConfig.Control.Field field : this.fields) {
/* 185 */       System.out.println(field);
/*     */     }
/*     */   }
/*     */   
/*     */   private <T> List<DynamicConfig.Control.Field> loadFields(Class<T> configClass) throws IllegalAccessException {
/* 190 */     Objects.requireNonNull(configClass);
/* 191 */     Map<String, DynamicConfig.Control.Category> categories = new HashMap<>();
/*     */ 
/*     */     
/* 194 */     for (Class<?> innerClass : configClass.getDeclaredClasses()) {
/* 195 */       for (Field field : innerClass.getDeclaredFields()) {
/* 196 */         if (field.isAnnotationPresent((Class)DynamicConfig.Category.class)) {
/* 197 */           DynamicConfig.Category categoryData = field.<DynamicConfig.Category>getAnnotation(DynamicConfig.Category.class);
/* 198 */           String categoryKey = (String)field.get((Object)null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 205 */           DynamicConfig.Control.Category category = (new DynamicConfig.Control.Category(categoryKey, categoryData.name(), categoryData.key())).setDisplayOnConfigScreen(categoryData.display()).setAsRoot(categoryData.root());
/*     */           
/* 207 */           categories.put(categoryKey, category);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 216 */     List<DynamicConfig.Control.Field> fields = new ArrayList<>();
/* 217 */     Field[] classFields = configClass.getDeclaredFields();
/*     */     
/* 219 */     for (Field field : classFields) {
/* 220 */       if (field.isAnnotationPresent((Class)DynamicConfig.Field.class)) {
/* 221 */         DynamicConfig.Field dynamicField = field.<DynamicConfig.Field>getAnnotation(DynamicConfig.Field.class);
/* 222 */         String categoryKey = dynamicField.category();
/* 223 */         DynamicConfig.Control.Category category = categories.get(categoryKey);
/* 224 */         String group = dynamicField.group();
/* 225 */         boolean displayOnConfigScreen = dynamicField.display();
/*     */         
/* 227 */         Class<?> type = field.getType();
/* 228 */         Type[] typeArguments = null;
/*     */         
/* 230 */         Object defaultValue = null;
/*     */         
/* 232 */         Type genericType = field.getGenericType();
/* 233 */         if (genericType instanceof ParameterizedType) { ParameterizedType parameterizedType = (ParameterizedType)genericType;
/* 234 */           typeArguments = parameterizedType.getActualTypeArguments(); }
/*     */ 
/*     */         
/*     */         try {
/* 238 */           defaultValue = field.get(this.config);
/* 239 */         } catch (IllegalAccessException e) {
/* 240 */           e.printStackTrace();
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 250 */         DynamicConfig.Control.Field configField = (new DynamicConfig.Control.Field(this.MOD_ID, field.getName(), category, type, typeArguments, defaultValue)).setGroup(group).setDisplayOnConfigScreen(displayOnConfigScreen);
/*     */         
/* 252 */         double minValue = dynamicField.min();
/* 253 */         double maxValue = dynamicField.max();
/*     */         
/* 255 */         if (type == int.class) {
/* 256 */           configField.setMin(Integer.valueOf((int)minValue));
/* 257 */           configField.setMax(Integer.valueOf((int)maxValue));
/* 258 */         } else if (type == float.class) {
/* 259 */           configField.setMin(Float.valueOf((float)minValue));
/* 260 */           configField.setMax(Float.valueOf((float)maxValue));
/* 261 */         } else if (type == double.class) {
/* 262 */           configField.setMin(Double.valueOf(minValue));
/* 263 */           configField.setMax(Double.valueOf(maxValue));
/* 264 */         } else if (type == long.class) {
/* 265 */           configField.setMin(Long.valueOf((long)minValue));
/* 266 */           configField.setMax(Long.valueOf((long)maxValue));
/*     */         } 
/*     */         
/* 269 */         fields.add(configField);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 274 */     return fields;
/*     */   }
/*     */   
/*     */   public T getConfig() {
/* 278 */     return this.config;
/*     */   }
/*     */   
/*     */   public List<DynamicConfig.Control.Field> getFields() {
/* 282 */     return this.fields;
/*     */   }
/*     */   
/*     */   public DynamicConfig.Control.Field getField(String fieldKey) {
/* 286 */     return this.fields.stream()
/* 287 */       .filter(field -> field.key.equals(fieldKey))
/* 288 */       .findFirst()
/* 289 */       .orElse(null);
/*     */   }
/*     */   
/*     */   public List<DynamicConfig.Control.Field> getFieldsByCategory(String category) {
/* 293 */     List<DynamicConfig.Control.Field> fields = new ArrayList<>();
/*     */     
/* 295 */     for (DynamicConfig.Control.Field field : this.fields) {
/* 296 */       if (field.category.key.equals(category)) {
/* 297 */         fields.add(field);
/*     */       }
/*     */     } 
/*     */     
/* 301 */     return fields;
/*     */   }
/*     */   
/*     */   public List<DynamicConfig.Control.Field> getFieldsByGroup(String group) {
/* 305 */     List<DynamicConfig.Control.Field> fields = new ArrayList<>();
/*     */     
/* 307 */     for (DynamicConfig.Control.Field field : this.fields) {
/* 308 */       if (field.group.equals(group)) {
/* 309 */         fields.add(field);
/*     */       }
/*     */     } 
/*     */     
/* 313 */     return fields;
/*     */   }
/*     */   
/*     */   public List<DynamicConfig.Control.Field> getFieldsByCategoryAndGroup(String category, String group) {
/* 317 */     return getFieldsByCategory(category).stream().filter(field -> field.group.equals(group))
/*     */       
/* 319 */       .toList();
/*     */   }
/*     */   
/* 322 */   public ConfigManager(String modId, Class<T> configClass, DynamicConfigAdapter<T> adapter) { this.FIELD_VALUE_CACHE = new HashMap<>(); this.MOD_ID = modId; this.adapter = adapter; try { this.config = configClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]); this.fields = loadFields(configClass); }
/*     */     catch (Exception e)
/*     */     { e.printStackTrace(); }
/* 325 */      this.adapter.initialize(this); } public void clearFieldValueCache() { this.FIELD_VALUE_CACHE.clear(); }
/*     */ 
/*     */   
/*     */   public <V> V getFieldValue(String fieldName) {
/* 329 */     if (this.FIELD_VALUE_CACHE.containsKey(fieldName)) {
/* 330 */       return (V)this.FIELD_VALUE_CACHE.get(fieldName);
/*     */     }
/*     */     
/*     */     try {
/* 334 */       Field configField = this.config.getClass().getDeclaredField(fieldName);
/* 335 */       Object value = configField.get(this.config);
/* 336 */       this.FIELD_VALUE_CACHE.put(fieldName, value);
/* 337 */       return (V)value;
/* 338 */     } catch (Exception ex) {
/* 339 */       ex.printStackTrace();
/*     */ 
/*     */       
/* 342 */       return null;
/*     */     } 
/*     */   }
/*     */   public void setFieldValue(String fieldName, Object value) {
/*     */     try {
/* 347 */       Field configField = this.config.getClass().getDeclaredField(fieldName);
/* 348 */       configField.setAccessible(true);
/* 349 */       configField.set(this.config, value);
/* 350 */       if (this.FIELD_VALUE_CACHE.containsKey(fieldName)) {
/* 351 */         this.FIELD_VALUE_CACHE.put(fieldName, value);
/*     */       }
/* 353 */     } catch (Exception ex) {
/* 354 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public ConfigManager<T> load(InputStream inputStream) {
/* 359 */     return this.adapter.load(this, inputStream);
/*     */   }
/*     */   
/*     */   public ConfigManager<T> load(File configFile) {
/* 363 */     if (!configFile.isFile()) {
/* 364 */       this.adapter.save(this, configFile);
/*     */     }
/*     */     
/* 367 */     return this.adapter.load(this, configFile);
/*     */   }
/*     */   
/*     */   public ConfigManager<T> load() {
/* 371 */     return load(this.adapter.getConfigFile());
/*     */   }
/*     */   
/*     */   public void save(File configFile) {
/* 375 */     DynamicConfigAdapter.SaveResult result = this.adapter.save(this, configFile);
/*     */     
/* 377 */     if (result == DynamicConfigAdapter.SaveResult.SUCCESS) {
/* 378 */       ((ConfigEvents.ConfigSave)ConfigEvents.SAVE.invoker()).onConfigSave();
/*     */     }
/*     */   }
/*     */   
/*     */   public void save() {
/* 383 */     save(this.adapter.getConfigFile());
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface SaveConsumer<T, U> {
/*     */     T accept(U param2U);
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface LoadConsumer<T, U, V> {
/*     */     T accept(U param2U, V param2V);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\DynamicConfig$ConfigManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */