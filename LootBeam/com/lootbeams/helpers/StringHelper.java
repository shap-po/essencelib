/*    */ package com.lootbeams.helpers;
/*    */ 
/*    */ public class StringHelper {
/*    */   public static String capitalize(String str) {
/*  5 */     if (str == null || str.isEmpty()) {
/*  6 */       return "";
/*    */     }
/*    */     
/*  9 */     return str.substring(0, 1).toUpperCase() + str.substring(0, 1).toUpperCase();
/*    */   }
/*    */   
/*    */   public static String camelToSnake(String str) {
/* 13 */     return str
/* 14 */       .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
/* 15 */       .replaceAll("([A-Z])([A-Z][a-z])", "$1_$2")
/* 16 */       .toLowerCase();
/*    */   }
/*    */   
/*    */   public static String toBinaryName(String mapName) {
/* 20 */     return "L" + mapName.replace('.', '/') + ";";
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\StringHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */