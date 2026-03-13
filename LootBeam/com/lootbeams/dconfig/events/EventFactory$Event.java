/*    */ package com.lootbeams.dconfig.events;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Event<T>
/*    */ {
/* 19 */   private final List<T> listeners = new ArrayList<>();
/*    */   
/*    */   private final EventFactory.EventInvokerFactory<T> invokerFactory;
/*    */   private final Class<?> listenerClass;
/*    */   private T invoker;
/*    */   
/*    */   public Event(Class<? super T> listenerClass, EventFactory.EventInvokerFactory<T> invokerFactory) {
/* 26 */     this.listenerClass = listenerClass;
/* 27 */     this.invokerFactory = invokerFactory;
/* 28 */     updateInvoker();
/*    */   }
/*    */   
/*    */   public T invoker() {
/* 32 */     return this.invoker;
/*    */   }
/*    */   
/*    */   public void register(T listener) {
/* 36 */     this.listeners.add(listener);
/* 37 */     updateInvoker();
/*    */   }
/*    */ 
/*    */   
/*    */   private void updateInvoker() {
/* 42 */     T[] listenerArray = (T[])Array.newInstance(this.listenerClass, this.listeners.size());
/* 43 */     this.invoker = this.invokerFactory.create(this.listeners.toArray(listenerArray));
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\events\EventFactory$Event.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */