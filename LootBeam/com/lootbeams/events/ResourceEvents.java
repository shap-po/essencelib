/*    */ package com.lootbeams.events;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import net.fabricmc.fabric.api.event.Event;
/*    */ import net.fabricmc.fabric.api.event.EventFactory;
/*    */ import net.minecraft.class_3300;
/*    */ import net.minecraft.class_3302;
/*    */ 
/*    */ public interface ResourceEvents
/*    */ {
/* 11 */   public static final Event<ResourceReload> RESOURCE_RELOAD = EventFactory.createArrayBacked(ResourceReload.class, listeners -> ());
/*    */   
/*    */   public static interface ResourceReload {
/*    */     void onResourceReload(class_3302.class_4045 param1class_4045, class_3300 param1class_3300, Executor param1Executor1, Executor param1Executor2);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\events\ResourceEvents.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */