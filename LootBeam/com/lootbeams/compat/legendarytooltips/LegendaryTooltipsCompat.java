/*    */ package com.lootbeams.compat.legendarytooltips;
/*    */ 
/*    */ import net.fabricmc.loader.api.FabricLoader;
/*    */ import net.minecraft.class_1799;
/*    */ 
/*    */ public class LegendaryTooltipsCompat {
/*    */   public static boolean isLegendaryTooltipsLoaded() {
/*  8 */     return FabricLoader.getInstance().isModLoaded("legendarytooltips");
/*    */   }
/*    */   
/*    */   public static boolean showModelForItem(class_1799 itemStack) {
/*    */     try {
/* 13 */       return (
/* 14 */         (Boolean)Class.forName("com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig")
/* 15 */         .getMethod("showModelForItem", new Class[] { class_1799.class
/*    */           
/* 17 */           }).invoke(null, new Object[] { itemStack })).booleanValue();
/*    */ 
/*    */     
/*    */     }
/* 21 */     catch (Exception ex) {
/* 22 */       ex.printStackTrace();
/* 23 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\compat\legendarytooltips\LegendaryTooltipsCompat.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */