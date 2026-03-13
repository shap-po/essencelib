/*    */ package com.lootbeams.helpers;
/*    */ 
/*    */ import com.lootbeams.LootBeams;
/*    */ import com.lootbeams.features.CustomRarity;
/*    */ import com.mojang.datafixers.util.Pair;
/*    */ import java.util.List;
/*    */ import java.util.Optional;
/*    */ import net.minecraft.class_1074;
/*    */ import net.minecraft.class_1792;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_1814;
/*    */ import net.minecraft.class_2960;
/*    */ import net.minecraft.class_6862;
/*    */ import net.minecraft.class_6885;
/*    */ import net.minecraft.class_7923;
/*    */ 
/*    */ public class RarityHelper
/*    */ {
/*    */   public static String getRarity(class_1799 stack) {
/* 20 */     String rarity = stack.method_7932().name().toLowerCase();
/* 21 */     rarity = rarity.replace(":", ".").replace("_", ".");
/* 22 */     CustomRarity customRarity = CustomRarity.fromItemStack(stack);
/* 23 */     if (customRarity != null) {
/* 24 */       return customRarity.getName();
/*    */     }
/* 26 */     String translatableKey = "lootbeams.rarity." + rarity;
/* 27 */     if (class_1074.method_4663(translatableKey)) {
/* 28 */       return class_1074.method_4662(translatableKey, new Object[0]);
/*    */     }
/* 30 */     return rarity;
/*    */   }
/*    */   
/*    */   public static boolean rarityCheck(class_1799 itemStack, boolean isRare) {
/* 34 */     CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
/* 35 */     if (customRarity != null) {
/* 36 */       return true;
/*    */     }
/* 38 */     return (isRare || itemStack.method_7932() != class_1814.field_8906);
/*    */   }
/*    */   
/*    */   public static boolean alwaysHasRarity(class_1799 item) {
/* 42 */     List<String> overrides = LootBeams.config.alwaysDrawRaritiesOn;
/* 43 */     if (overrides.isEmpty()) {
/* 44 */       return false;
/*    */     }
/* 46 */     for (String name : overrides.stream().filter(s -> !s.isEmpty()).toList()) {
/* 47 */       class_2960 registry = class_2960.method_12829(name.replace("#", ""));
/*    */ 
/*    */       
/* 50 */       if (!name.contains(":") && 
/* 51 */         class_7923.field_41178.method_10221(item.method_7909()).method_12836().equals(name)) {
/* 52 */         return true;
/*    */       }
/* 54 */       if (registry == null) {
/*    */         continue;
/*    */       }
/*    */       
/* 58 */       if (name.startsWith("#")) {
/*    */         
/* 60 */         Optional<class_6885.class_6888<class_1792>> tag = class_7923.field_41178.method_40272().filter(pair -> ((class_6862)pair.getFirst()).comp_327().equals(registry)).findFirst().map(Pair::getSecond);
/*    */         
/* 62 */         if (tag.isPresent() && ((class_6885.class_6888)tag.get()).method_40241(class_7923.field_41178.method_40264(class_7923.field_41178.method_29113(item.method_7909()).get()).get())) {
/* 63 */           return true;
/*    */         }
/*    */       } 
/*    */ 
/*    */       
/* 68 */       Optional<class_1792> registryItem = class_7923.field_41178.method_17966(registry);
/*    */       
/* 70 */       if (registryItem.isPresent() && ((class_1792)registryItem.get()).method_8389() == item.method_7909())
/* 71 */         return true; 
/*    */     } 
/* 73 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\RarityHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */