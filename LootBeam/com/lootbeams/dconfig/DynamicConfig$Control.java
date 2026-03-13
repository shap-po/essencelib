/*     */ package com.lootbeams.dconfig;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.text.MessageFormat;
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
/*     */ public class Control
/*     */ {
/*     */   public static class Category
/*     */   {
/*     */     public String key;
/*     */     public String name;
/*     */     public String saveKey;
/*     */     public boolean displayOnConfigScreen = true;
/*     */     public boolean isRootCategory = false;
/*     */     
/*     */     public Category(String key, String name, String saveKey) {
/*  51 */       this.key = key;
/*  52 */       this.name = name;
/*  53 */       this.saveKey = saveKey;
/*     */     }
/*     */     
/*     */     public Category setAsRoot(boolean isRootCategory) {
/*  57 */       this.isRootCategory = isRootCategory;
/*  58 */       return this;
/*     */     }
/*     */     
/*     */     public Category setDisplayOnConfigScreen(boolean displayOnConfigScreen) {
/*  62 */       this.displayOnConfigScreen = displayOnConfigScreen;
/*  63 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  68 */       return "DynamicConfig.Category[key=" + this.key + ", name=" + this.name + ", displayOnConfigScreen=" + this.displayOnConfigScreen + ", isRootCategory=" + this.isRootCategory + "]";
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Group {
/*     */     public static final String DEFAULT = "default";
/*     */   }
/*     */   
/*     */   public static class Field {
/*     */     public String key;
/*     */     public String saveKey;
/*     */     public DynamicConfig.Control.Category category;
/*  80 */     public String group = "default";
/*     */     public String name;
/*     */     public String description;
/*     */     public Class<?> type;
/*     */     public Type[] typeArguments;
/*     */     public Object defaultValue;
/*  86 */     public Object minValue = null;
/*  87 */     public Object maxValue = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean displayOnConfigScreen = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Field(String modId, String key, DynamicConfig.Control.Category category, Class<?> type, Type[] typeArguments, Object defaultValue) {
/*  98 */       this.key = key;
/*  99 */       this.saveKey = camelToSnake(key);
/* 100 */       this.category = category;
/* 101 */       this.name = MessageFormat.format("{0}.config.{1}.{2}", new Object[] { modId, category.key, key });
/* 102 */       this.description = MessageFormat.format("{0}.config.{1}.{2}.description", new Object[] { modId, category.key, key });
/* 103 */       this.type = type;
/* 104 */       this.typeArguments = typeArguments;
/* 105 */       this.defaultValue = defaultValue;
/*     */     }
/*     */     
/*     */     private String camelToSnake(String str) {
/* 109 */       return str
/* 110 */         .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
/* 111 */         .replaceAll("([A-Z])([A-Z][a-z])", "$1_$2")
/* 112 */         .toLowerCase();
/*     */     }
/*     */     
/*     */     public Field setMin(Object minValue) {
/* 116 */       this.minValue = minValue;
/* 117 */       return this;
/*     */     }
/*     */     
/*     */     public Field setMax(Object maxValue) {
/* 121 */       this.maxValue = maxValue;
/* 122 */       return this;
/*     */     }
/*     */     
/*     */     public Field setGroup(String group) {
/* 126 */       this.group = group;
/* 127 */       return this;
/*     */     }
/*     */     
/*     */     public Field setDisplayOnConfigScreen(boolean displayOnConfigScreen) {
/* 131 */       this.displayOnConfigScreen = displayOnConfigScreen;
/* 132 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 137 */       return "DynamicConfig.Field[key=" + this.key + ", saveKey=" + this.saveKey + ", category=" + String.valueOf(this.category) + ", name=" + this.name + ", description=" + this.description + ", type=" + String.valueOf(this.type) + ", typeArguments=" + String.valueOf(this.typeArguments) + ", defaultValue=" + String.valueOf(this.defaultValue) + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\DynamicConfig$Control.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */