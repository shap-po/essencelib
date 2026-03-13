/*    */ package com.lootbeams.listeners;
/*    */ 
/*    */ import com.lootbeams.LootBeams;
/*    */ import com.lootbeams.events.ResourceEvents;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.Executor;
/*    */ import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_3300;
/*    */ import net.minecraft.class_3302;
/*    */ import net.minecraft.class_3695;
/*    */ import net.minecraft.class_3902;
/*    */ 
/*    */ public class ResourceReloadListener
/*    */   implements IdentifiableResourceReloadListener, class_3302 {
/*    */   public CompletableFuture<Void> method_25931(class_3302.class_4045 synchronizer, class_3300 manager, class_3695 prepareProfiler, class_3695 applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
/* 19 */     return synchronizer.method_18352(class_3902.field_17274).thenRunAsync(() -> { applyProfiler.method_16065(); applyProfiler.method_15396("listener"); reload(synchronizer, manager, prepareExecutor, applyExecutor); applyProfiler.method_15407(); applyProfiler.method_16066(); }applyExecutor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void reload(class_3302.class_4045 synchronizer, class_3300 manager, Executor prepareExecutor, Executor applyExecutor) {
/* 29 */     ((ResourceEvents.ResourceReload)ResourceEvents.RESOURCE_RELOAD.invoker()).onResourceReload(synchronizer, manager, prepareExecutor, applyExecutor);
/*    */   }
/*    */ 
/*    */   
/*    */   public class_2960 getFabricId() {
/* 34 */     return LootBeams.id("reload_resources");
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<class_2960> getFabricDependencies() {
/* 39 */     return Collections.emptyList();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\listeners\ResourceReloadListener.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */