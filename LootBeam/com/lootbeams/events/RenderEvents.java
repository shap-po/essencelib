/*    */ package com.lootbeams.events;
/*    */ 
/*    */ import com.lootbeams.contexts.WorldRendererContext;
/*    */ import net.fabricmc.fabric.api.event.Event;
/*    */ import net.fabricmc.fabric.api.event.EventFactory;
/*    */ import net.minecraft.class_332;
/*    */ import net.minecraft.class_9779;
/*    */ 
/*    */ public interface RenderEvents {
/* 10 */   public static final Event<HudRender> HUD = EventFactory.createArrayBacked(HudRender.class, listeners -> ());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 17 */   public static final Event<BeforeParticles> BEFORE_PARTICLES = EventFactory.createArrayBacked(BeforeParticles.class, listeners -> ());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 24 */   public static final Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(AfterTranslucent.class, listeners -> ());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   public static final Event<AfterWeather> AFTER_WEATHER = EventFactory.createArrayBacked(AfterWeather.class, listeners -> ());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 38 */   public static final Event<BeforeEnd> BEFORE_END = EventFactory.createArrayBacked(BeforeEnd.class, listeners -> ());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 45 */   public static final Event<End> END = EventFactory.createArrayBacked(End.class, listeners -> ());
/*    */   
/*    */   public static interface End {
/*    */     void onEnd(WorldRendererContext param1WorldRendererContext);
/*    */   }
/*    */   
/*    */   public static interface BeforeEnd {
/*    */     void beforeEnd(WorldRendererContext param1WorldRendererContext);
/*    */   }
/*    */   
/*    */   public static interface AfterWeather {
/*    */     void afterWeather(WorldRendererContext param1WorldRendererContext);
/*    */   }
/*    */   
/*    */   public static interface AfterTranslucent {
/*    */     void afterTranslucent(WorldRendererContext param1WorldRendererContext);
/*    */   }
/*    */   
/*    */   public static interface BeforeParticles {
/*    */     void beforeParticles(WorldRendererContext param1WorldRendererContext);
/*    */   }
/*    */   
/*    */   public static interface HudRender {
/*    */     void onHud(class_332 param1class_332, class_9779 param1class_9779);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\events\RenderEvents.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */