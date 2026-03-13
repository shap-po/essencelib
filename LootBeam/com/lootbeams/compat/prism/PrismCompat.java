/*    */ package com.lootbeams.compat.prism;
/*    */ 
/*    */ import java.util.List;
/*    */ import net.fabricmc.loader.api.FabricLoader;
/*    */ import net.minecraft.class_5251;
/*    */ 
/*    */ public class PrismCompat {
/*    */   public static boolean isPrismLoaded() {
/*  9 */     return FabricLoader.getInstance().isModLoaded("prism");
/*    */   }
/*    */   
/*    */   public static class_5251 parseColor(Object value) {
/*    */     try {
/* 14 */       return 
/* 15 */         (class_5251)Class.forName("com.anthonyhilyard.prism.util.ConfigHelper")
/* 16 */         .getMethod("parseColor", new Class[] { Object.class }).invoke(null, new Object[] { value });
/*    */ 
/*    */     
/*    */     }
/* 20 */     catch (Exception ex) {
/* 21 */       ex.printStackTrace();
/* 22 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static class_5251 applyModifiers(List<String> modifiers, class_5251 value) {
/*    */     try {
/* 28 */       return 
/* 29 */         (class_5251)Class.forName("com.anthonyhilyard.prism.util.ConfigHelper")
/* 30 */         .getMethod("applyModifiers", new Class[] { List.class, class_5251.class
/* 31 */           }).invoke(null, new Object[] { modifiers, value });
/* 32 */     } catch (Exception ex) {
/* 33 */       ex.printStackTrace();
/* 34 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\compat\prism\PrismCompat.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */