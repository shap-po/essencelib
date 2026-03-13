/*     */ package com.lootbeams.managers;
/*     */ 
/*     */ import com.lootbeams.contexts.WorldRendererContext;
/*     */ import com.lootbeams.extensions.LootbeamsParticleManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_638;
/*     */ 
/*     */ public class RenderManager
/*     */ {
/*  20 */   public static final List<class_1542> LIGHT_CACHE = new ArrayList<>();
/*  21 */   private static final List<BiConsumer<class_4587, class_4597.class_4598>> RENDER_BEFORE_PARTICLES = new ArrayList<>();
/*  22 */   private static final List<BiConsumer<class_4587, class_4597.class_4598>> RENDER_AFTER_TRANSLUCENT = new ArrayList<>();
/*  23 */   private static final List<BiConsumer<class_4587, class_4597.class_4598>> RENDER_AFTER_WEATHER = new ArrayList<>();
/*  24 */   private static final List<BiConsumer<class_4587, class_4597.class_4598>> RENDER_BEFORE_END = new ArrayList<>();
/*  25 */   private static final Map<class_1542, Boolean> ITEM_RENDER_STATES = new HashMap<>();
/*  26 */   private static final Map<Class<?>, Consumer<WorldRendererContext>> END_RENDERS = new HashMap<>();
/*     */   
/*     */   public static void onWorldRenderBeforeParticles(WorldRendererContext worldRendererContext) {
/*  29 */     renderBeforeParticles(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera().method_19326(), worldRendererContext.getConsumers());
/*     */   }
/*     */   
/*     */   public static void onWorldRenderAfterTranslucent(WorldRendererContext worldRendererContext) {
/*  33 */     renderAfterTranslucent(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera().method_19326(), worldRendererContext.getConsumers());
/*     */   }
/*     */   
/*     */   public static void onWorldRenderAfterWeather(WorldRendererContext worldRendererContext) {
/*  37 */     renderAfterWeather(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera().method_19326(), worldRendererContext.getConsumers());
/*  38 */     LootbeamsParticleManager particleManager = (LootbeamsParticleManager)(class_310.method_1551()).field_1713;
/*  39 */     if (particleManager != null) {
/*  40 */       particleManager.renderCustomParticles(worldRendererContext.getLightmapTextureManager(), worldRendererContext.getCamera(), worldRendererContext.getTickCounter().method_60637(false));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void onWorldRenderBeforeEnd(WorldRendererContext worldRendererContext) {
/*  45 */     renderBeforeEnd(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera().method_19326(), worldRendererContext.getConsumers());
/*     */   }
/*     */   
/*     */   public static void onWorldRenderEnd(WorldRendererContext worldRendererContext) {
/*  49 */     afterRender(worldRendererContext);
/*  50 */     RENDER_BEFORE_PARTICLES.clear();
/*  51 */     RENDER_AFTER_TRANSLUCENT.clear();
/*  52 */     RENDER_AFTER_WEATHER.clear();
/*  53 */     RENDER_BEFORE_END.clear();
/*  54 */     END_RENDERS.clear();
/*     */   }
/*     */   
/*     */   public static void onEntityLoad(class_1542 itemEntity, class_638 world) {
/*  58 */     if (!LIGHT_CACHE.contains(itemEntity)) {
/*  59 */       LIGHT_CACHE.add(itemEntity);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void onEntityUnload(class_1542 itemEntity, class_638 world) {
/*  64 */     LIGHT_CACHE.remove(itemEntity);
/*     */   }
/*     */   
/*     */   public static void renderAfter(class_4587 stack, class_243 cameraPos, class_4597.class_4598 immediate, List<BiConsumer<class_4587, class_4597.class_4598>> consumers) {
/*  68 */     if (stack == null) {
/*     */       return;
/*     */     }
/*     */     
/*  72 */     stack.method_22903();
/*  73 */     stack.method_22904(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350);
/*  74 */     consumers.forEach(consumer -> consumer.accept(stack, immediate));
/*  75 */     stack.method_22909();
/*     */   }
/*     */   
/*     */   public static void afterRender(WorldRendererContext context) {
/*  79 */     END_RENDERS.values().forEach(consumer -> consumer.accept(context));
/*     */   }
/*     */   
/*     */   public static void renderBeforeParticles(class_4587 stack, class_243 cameraPos, class_4597.class_4598 immediate) {
/*  83 */     renderAfter(stack, cameraPos, immediate, RENDER_BEFORE_PARTICLES);
/*     */   }
/*     */   
/*     */   public static void renderAfterTranslucent(class_4587 stack, class_243 cameraPos, class_4597.class_4598 immediate) {
/*  87 */     renderAfter(stack, cameraPos, immediate, RENDER_AFTER_TRANSLUCENT);
/*     */   }
/*     */   
/*     */   public static void renderAfterWeather(class_4587 stack, class_243 cameraPos, class_4597.class_4598 immediate) {
/*  91 */     renderAfter(stack, cameraPos, immediate, RENDER_AFTER_WEATHER);
/*     */   }
/*     */   
/*     */   public static void renderBeforeEnd(class_4587 stack, class_243 cameraPos, class_4597.class_4598 immediate) {
/*  95 */     renderAfter(stack, cameraPos, immediate, RENDER_BEFORE_END);
/*     */   }
/*     */   
/*     */   public static void addRenderBeforeParticles(BiConsumer<class_4587, class_4597.class_4598> render) {
/*  99 */     RENDER_BEFORE_PARTICLES.add(render);
/*     */   }
/*     */   
/*     */   public static void addRenderAfterTranslucent(BiConsumer<class_4587, class_4597.class_4598> render) {
/* 103 */     RENDER_AFTER_TRANSLUCENT.add(render);
/*     */   }
/*     */   
/*     */   public static void setRendering(class_1542 itemEntity, boolean isRendering) {
/* 107 */     ITEM_RENDER_STATES.put(itemEntity, Boolean.valueOf(isRendering));
/*     */   }
/*     */   
/*     */   public static boolean isRendering(class_1542 itemEntity) {
/* 111 */     return ((Boolean)ITEM_RENDER_STATES.getOrDefault(itemEntity, Boolean.valueOf(false))).booleanValue();
/*     */   }
/*     */   
/*     */   public static void addRenderAfterWeather(BiConsumer<class_4587, class_4597.class_4598> render) {
/* 115 */     RENDER_AFTER_WEATHER.add(render);
/*     */   }
/*     */   
/*     */   public static void addRenderBeforeEnd(BiConsumer<class_4587, class_4597.class_4598> render) {
/* 119 */     RENDER_BEFORE_END.add(render);
/*     */   }
/*     */   
/*     */   public static void addOnRenderEnd(Class<?> parentClass, Consumer<WorldRendererContext> render) {
/* 123 */     if (!END_RENDERS.containsKey(parentClass))
/* 124 */       END_RENDERS.put(parentClass, render); 
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\RenderManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */