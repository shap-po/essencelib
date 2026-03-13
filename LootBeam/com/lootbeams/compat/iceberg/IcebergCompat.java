/*     */ package com.lootbeams.compat.iceberg;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import net.fabricmc.loader.api.FabricLoader;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_327;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_5632;
/*     */ import net.minecraft.class_5684;
/*     */ import net.minecraft.class_768;
/*     */ import net.minecraft.class_8000;
/*     */ 
/*     */ public class IcebergCompat
/*     */ {
/*     */   public static boolean isIcebergLoaded() {
/*  18 */     return FabricLoader.getInstance().isModLoaded("iceberg");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getPlatformName() {
/*     */     try {
/*  25 */       Object platformHelper = Class.forName("com.anthonyhilyard.iceberg.services.Services").getMethod("getPlatformHelper", new Class[0]).invoke(null, new Object[0]);
/*     */       
/*  27 */       return (String)Class.forName("com.anthonyhilyard.iceberg.services.IPlatformHelper").getMethod("getPlatformName", new Class[0]).invoke(platformHelper, new Object[0]);
/*  28 */     } catch (Exception ex) {
/*  29 */       ex.printStackTrace();
/*  30 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class_768 getTooltipRect(class_1799 itemStack, class_332 context, class_8000 positioner, List<class_5684> components, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, class_327 textRenderer, int minWidth, boolean centeredTitle) {
/*     */     try {
/*  36 */       return 
/*  37 */         (class_768)Class.forName("com.anthonyhilyard.iceberg.util.Tooltips")
/*  38 */         .getMethod("calculateRect", new Class[] {
/*     */ 
/*     */ 
/*     */             
/*     */             class_1799.class, class_332.class, class_8000.class, List.class, int.class, int.class, int.class, int.class, int.class, class_327.class,
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             int.class, boolean.class
/*     */ 
/*     */ 
/*     */           
/*  51 */           }).invoke(null, new Object[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  57 */             itemStack, context, positioner, components, Integer.valueOf(mouseX), 
/*  58 */             Integer.valueOf(mouseY), 
/*  59 */             Integer.valueOf(screenWidth), 
/*  60 */             Integer.valueOf(screenHeight), 
/*  61 */             Integer.valueOf(maxTextWidth), textRenderer,
/*     */             
/*  63 */             Integer.valueOf(minWidth), 
/*  64 */             Boolean.valueOf(centeredTitle)
/*     */           });
/*  66 */     } catch (Exception ex) {
/*  67 */       ex.printStackTrace();
/*  68 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<class_5684> getTooltipComponents(class_1799 itemStack, List<? extends class_2561> textElements, Optional<class_5632> tooltipData, int mouseX, int screenWidth, int screenHeight, class_327 forcedFont, class_327 fallbackFont, int maxWidth) {
/*     */     try {
/*  75 */       return 
/*  76 */         (List<class_5684>)Class.forName("com.anthonyhilyard.iceberg.util.Tooltips")
/*  77 */         .getMethod("gatherTooltipComponents", new Class[] {
/*     */ 
/*     */ 
/*     */             
/*     */             class_1799.class, List.class, Optional.class, int.class, int.class, int.class, class_327.class, class_327.class, int.class
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  87 */           }).invoke(null, new Object[] {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  92 */             itemStack, textElements, tooltipData, Integer.valueOf(mouseX), 
/*  93 */             Integer.valueOf(screenWidth), 
/*  94 */             Integer.valueOf(screenHeight), forcedFont, fallbackFont, 
/*     */ 
/*     */             
/*  97 */             Integer.valueOf(maxWidth)
/*     */           });
/*  99 */     } catch (Exception ex) {
/* 100 */       ex.printStackTrace();
/* 101 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\compat\iceberg\IcebergCompat.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */