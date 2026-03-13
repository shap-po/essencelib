/*     */ package com.lootbeams.contexts;
/*     */ 
/*     */ import net.minecraft.class_3695;
/*     */ import net.minecraft.class_4184;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_638;
/*     */ import net.minecraft.class_757;
/*     */ import net.minecraft.class_761;
/*     */ import net.minecraft.class_765;
/*     */ import net.minecraft.class_9779;
/*     */ import org.joml.Matrix4f;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WorldRendererContext
/*     */ {
/*     */   private class_761 worldRenderer;
/*     */   private class_9779 tickCounter;
/*     */   private class_4587 matrixStack;
/*     */   private boolean blockOutlines;
/*     */   private class_4184 camera;
/*     */   private class_757 gameRenderer;
/*     */   
/*     */   public WorldRendererContext prepare(class_761 worldRenderer, class_9779 delta, boolean blockOutlines, class_4184 camera, class_757 gameRenderer, class_765 lightmapTextureManager, Matrix4f projectionMatrix, Matrix4f positionMatrix, class_4597.class_4598 consumers, class_3695 profiler, boolean advancedTranslucency, class_638 world) {
/*  27 */     this.worldRenderer = worldRenderer;
/*  28 */     this.tickCounter = delta;
/*  29 */     this.matrixStack = null;
/*  30 */     this.blockOutlines = blockOutlines;
/*  31 */     this.camera = camera;
/*  32 */     this.gameRenderer = gameRenderer;
/*  33 */     this.lightmapTextureManager = lightmapTextureManager;
/*  34 */     this.projectionMatrix = projectionMatrix;
/*  35 */     this.positionMatrix = positionMatrix;
/*  36 */     this.consumers = consumers;
/*  37 */     this.profiler = profiler;
/*  38 */     this.advancedTranslucency = advancedTranslucency;
/*  39 */     this.world = world;
/*     */     
/*  41 */     return this;
/*     */   }
/*     */   private class_765 lightmapTextureManager; private Matrix4f projectionMatrix; private Matrix4f positionMatrix; private class_4597.class_4598 consumers; private class_3695 profiler; private boolean advancedTranslucency; private class_638 world;
/*     */   public class_4587 getMatrixStack() {
/*  45 */     return this.matrixStack;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setMatrixStack(class_4587 matrixStack) {
/*  49 */     this.matrixStack = matrixStack;
/*  50 */     return this;
/*     */   }
/*     */   
/*     */   public class_4184 getCamera() {
/*  54 */     return this.camera;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setCamera(class_4184 camera) {
/*  58 */     this.camera = camera;
/*  59 */     return this;
/*     */   }
/*     */   
/*     */   public class_765 getLightmapTextureManager() {
/*  63 */     return this.lightmapTextureManager;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setLightmapTextureManager(class_765 lightmapTextureManager) {
/*  67 */     this.lightmapTextureManager = lightmapTextureManager;
/*  68 */     return this;
/*     */   }
/*     */   
/*     */   public class_761 getWorldRenderer() {
/*  72 */     return this.worldRenderer;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setWorldRenderer(class_761 worldRenderer) {
/*  76 */     this.worldRenderer = worldRenderer;
/*  77 */     return this;
/*     */   }
/*     */   
/*     */   public class_9779 getTickCounter() {
/*  81 */     return this.tickCounter;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setTickCounter(class_9779 tickCounter) {
/*  85 */     this.tickCounter = tickCounter;
/*  86 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isBlockOutlines() {
/*  90 */     return this.blockOutlines;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setBlockOutlines(boolean blockOutlines) {
/*  94 */     this.blockOutlines = blockOutlines;
/*  95 */     return this;
/*     */   }
/*     */   
/*     */   public class_757 getGameRenderer() {
/*  99 */     return this.gameRenderer;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setGameRenderer(class_757 gameRenderer) {
/* 103 */     this.gameRenderer = gameRenderer;
/* 104 */     return this;
/*     */   }
/*     */   
/*     */   public Matrix4f getProjectionMatrix() {
/* 108 */     return this.projectionMatrix;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setProjectionMatrix(Matrix4f projectionMatrix) {
/* 112 */     this.projectionMatrix = projectionMatrix;
/* 113 */     return this;
/*     */   }
/*     */   
/*     */   public Matrix4f getPositionMatrix() {
/* 117 */     return this.positionMatrix;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setPositionMatrix(Matrix4f positionMatrix) {
/* 121 */     this.positionMatrix = positionMatrix;
/* 122 */     return this;
/*     */   }
/*     */   
/*     */   public class_4597.class_4598 getConsumers() {
/* 126 */     return this.consumers;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setConsumers(class_4597.class_4598 consumers) {
/* 130 */     this.consumers = consumers;
/* 131 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isAdvancedTranslucency() {
/* 135 */     return this.advancedTranslucency;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setAdvancedTranslucency(boolean advancedTranslucency) {
/* 139 */     this.advancedTranslucency = advancedTranslucency;
/* 140 */     return this;
/*     */   }
/*     */   
/*     */   public class_638 getWorld() {
/* 144 */     return this.world;
/*     */   }
/*     */   
/*     */   public WorldRendererContext setWorld(class_638 world) {
/* 148 */     this.world = world;
/* 149 */     return this;
/*     */   }
/*     */   
/*     */   public class_3695 profiler() {
/* 153 */     return this.profiler;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\contexts\WorldRendererContext.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */