/*    */ package com.lootbeams.helpers;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Optional;
/*    */ import net.minecraft.class_1297;
/*    */ import net.minecraft.class_1542;
/*    */ import net.minecraft.class_1657;
/*    */ import net.minecraft.class_238;
/*    */ import net.minecraft.class_239;
/*    */ import net.minecraft.class_243;
/*    */ import net.minecraft.class_310;
/*    */ import net.minecraft.class_3966;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TargetHelper
/*    */ {
/*    */   public static boolean isLookingAt(class_1657 player, class_1297 target, double accuracy) {
/* 20 */     class_243 difference = new class_243(target.method_23317() - player.method_23317(), target.method_23320() - player.method_23320(), target.method_23321() - player.method_23321());
/* 21 */     double length = difference.method_1033();
/* 22 */     double dot = class_310.method_1551().method_1560().method_5720().method_1029().method_1026(difference.method_1029());
/* 23 */     return (dot > 1.0D - accuracy / length && player.method_6057(target));
/*    */   }
/*    */   
/*    */   public static class_239 getEntityItem(class_1657 player) {
/* 27 */     class_310 mc = class_310.method_1551();
/* 28 */     double distance = player.method_55754();
/* 29 */     float partialTicks = mc.method_60646().method_60637(true);
/* 30 */     class_243 position = player.method_5836(partialTicks);
/* 31 */     class_243 view = player.method_5828(partialTicks);
/* 32 */     if (mc.field_1765 != null && mc.field_1765.method_17783() != class_239.class_240.field_1333)
/* 33 */       distance = mc.field_1765.method_17784().method_1022(position); 
/* 34 */     return getEntityItem(player, position, position.method_1031(view.field_1352 * distance, view.field_1351 * distance, view.field_1350 * distance));
/*    */   }
/*    */   
/*    */   public static class_239 getEntityItem(class_1657 player, class_243 position, class_243 look) {
/* 38 */     class_243 include = look.method_1020(position);
/* 39 */     List<class_1297> list = player.method_37908().method_8335((class_1297)player, player.method_5829().method_1009(include.field_1352, include.field_1351, include.field_1350));
/*    */     
/* 41 */     double closestDistance = player.method_55754();
/* 42 */     class_1542 closestItem = null;
/*    */     
/* 44 */     for (int i = 0; i < list.size(); i++) {
/* 45 */       class_1297 entity = list.get(i);
/* 46 */       if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
/* 47 */         class_238 itemBox = entity.method_5829().method_1009(0.0D, 0.3D, 0.0D);
/* 48 */         Optional<class_243> intersection = itemBox.method_992(position, look);
/*    */         
/* 50 */         if (intersection.isPresent()) {
/* 51 */           double distance = position.method_1022(intersection.get());
/* 52 */           if (distance < closestDistance) {
/* 53 */             closestDistance = distance;
/* 54 */             closestItem = itemEntity;
/*    */           } 
/*    */         }  }
/*    */     
/*    */     } 
/*    */     
/* 60 */     if (closestItem != null) {
/* 61 */       return (class_239)new class_3966((class_1297)closestItem);
/*    */     }
/*    */     
/* 64 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\TargetHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */