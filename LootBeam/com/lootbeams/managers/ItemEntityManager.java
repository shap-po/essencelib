/*    */ package com.lootbeams.managers;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.WeakHashMap;
/*    */ import net.minecraft.class_1542;
/*    */ import net.minecraft.class_1799;
/*    */ 
/*    */ public class ItemEntityManager
/*    */ {
/* 10 */   private static final Map<class_1799, class_1542> stackToEntityMap = new WeakHashMap<>();
/*    */   
/*    */   public static void track(class_1542 entity) {
/* 13 */     stackToEntityMap.put(entity.method_6983(), entity);
/*    */   }
/*    */   
/*    */   public static class_1542 getEntityForStack(class_1799 stack) {
/* 17 */     return stackToEntityMap.get(stack);
/*    */   }
/*    */   
/*    */   public static void untrack(class_1542 entity) {
/* 21 */     stackToEntityMap.values().removeIf(e -> (e == entity));
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\ItemEntityManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */