/*     */ package com.lootbeams.shaders;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class CustomShader
/*     */ {
/*  35 */   public static final CustomShader NONE = new CustomShader("NONE");
/*     */   
/*     */   private final String name;
/*     */   
/*  39 */   private static final List<CustomShader> dynamicValues = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*  43 */   private static final List<CustomShader> staticValues = new ArrayList<>(); static {
/*  44 */     staticValues.add(NONE);
/*     */   }
/*     */   
/*     */   private CustomShader(String name) {
/*  48 */     this.name = name;
/*     */   }
/*     */   
/*     */   public String name() {
/*  52 */     return this.name;
/*     */   }
/*     */   
/*     */   public static CustomShader addValue(String name) {
/*  56 */     synchronized (dynamicValues) {
/*  57 */       for (CustomShader value : values()) {
/*  58 */         if (value.name.equals(name)) {
/*  59 */           return value;
/*     */         }
/*     */       } 
/*     */       
/*  63 */       CustomShader newValue = new CustomShader(name);
/*  64 */       dynamicValues.add(newValue);
/*  65 */       return newValue;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void clearDynamicValues() {
/*  70 */     synchronized (dynamicValues) {
/*  71 */       dynamicValues.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static List<CustomShader> values() {
/*  76 */     synchronized (dynamicValues) {
/*  77 */       List<CustomShader> allValues = new ArrayList<>(staticValues);
/*  78 */       allValues.addAll(dynamicValues);
/*  79 */       return allValues;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static CustomShader valueOf(String name) {
/*  84 */     for (CustomShader shader : staticValues) {
/*  85 */       if (shader.name.equals(name)) {
/*  86 */         return shader;
/*     */       }
/*     */     } 
/*     */     
/*  90 */     synchronized (dynamicValues) {
/*  91 */       for (CustomShader shader : dynamicValues) {
/*  92 */         if (shader.name.equals(name)) {
/*  93 */           return shader;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  98 */     return addValue(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 103 */     return this.name;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\shaders\LootBeamShaders$CustomShader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */