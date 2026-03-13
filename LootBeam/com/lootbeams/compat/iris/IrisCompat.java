/*    */ package com.lootbeams.compat.iris;
/*    */ 
/*    */ import java.util.List;
/*    */ import net.fabricmc.loader.api.FabricLoader;
/*    */ 
/*    */ 
/*    */ public class IrisCompat
/*    */ {
/*    */   public static boolean isIrisLoaded() {
/* 10 */     return FabricLoader.getInstance().isModLoaded("iris");
/*    */   }
/*    */   
/*    */   public static List<Integer> getVertexFormatsIndexes() {
/* 14 */     return List.of(Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14));
/*    */   }
/*    */   
/*    */   public static boolean isShaderPackInUse() {
/* 18 */     if (!isIrisLoaded()) {
/* 19 */       return false;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 25 */       Object irisApiInstance = Class.forName("net.irisshaders.iris.api.v0.IrisApi").getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
/*    */       
/* 27 */       return ((Boolean)Class.forName("net.irisshaders.iris.api.v0.IrisApi").getMethod("isShaderPackInUse", new Class[0]).invoke(irisApiInstance, new Object[0])).booleanValue();
/* 28 */     } catch (Exception ex) {
/* 29 */       ex.printStackTrace();
/* 30 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\compat\iris\IrisCompat.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */