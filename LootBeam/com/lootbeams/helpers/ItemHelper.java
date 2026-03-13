/*    */ package com.lootbeams.helpers;
/*    */ import java.util.List;
/*    */ import net.minecraft.class_1766;
/*    */ import net.minecraft.class_1770;
/*    */ import net.minecraft.class_1786;
/*    */ import net.minecraft.class_1792;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_1811;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_7923;
/*    */ import net.minecraft.class_8162;
/*    */ 
/*    */ public class ItemHelper {
/*    */   public static boolean isItemInRegistryList(List<String> registryNames, class_1792 item) {
/* 15 */     if (registryNames.size() > 0) {
/* 16 */       for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
/* 17 */         if (!id.contains(":") && 
/* 18 */           class_7923.field_41178.method_10221(item).method_12836().equals(id)) {
/* 19 */           return true;
/*    */         }
/*    */         
/* 22 */         class_2960 itemResource = class_2960.method_12829(id);
/* 23 */         if (itemResource != null && ((class_1792)class_7923.field_41178.method_10223(itemResource)).method_8389() == item.method_8389()) {
/* 24 */           return true;
/*    */         }
/*    */       } 
/*    */     }
/* 28 */     return false;
/*    */   }
/*    */   
/*    */   public static boolean isEquipmentItem(class_1799 itemStack) {
/* 32 */     List<Class<? extends class_1792>> equipmentClasses = Arrays.asList((Class<? extends class_1792>[])new Class[] { class_1829.class, class_1766.class, class_1738.class, class_1819.class, class_1811.class, class_1835.class, class_1744.class, class_1787.class, class_1786.class, class_1820.class, class_8162.class, class_1770.class });
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
/*    */ 
/*    */ 
/*    */     
/* 47 */     for (Class<? extends class_1792> item : equipmentClasses) {
/* 48 */       if (item.isAssignableFrom(itemStack.method_7909().getClass())) {
/* 49 */         return true;
/*    */       }
/*    */     } 
/*    */     
/* 53 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\ItemHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */