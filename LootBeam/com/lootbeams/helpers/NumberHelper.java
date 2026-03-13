/*    */ package com.lootbeams.helpers;
/*    */ 
/*    */ import net.minecraft.class_3532;
/*    */ 
/*    */ public class NumberHelper {
/*    */   public static <T extends Number> double mapRange(T n, T fromStart, T fromEnd, T toStart, T toEnd) {
/*  7 */     return (n
/*  8 */       .doubleValue() - fromStart.doubleValue()) / (fromEnd
/*    */       
/* 10 */       .doubleValue() - fromStart.doubleValue()) * (toEnd
/* 11 */       .doubleValue() - toStart.doubleValue()) + toStart.doubleValue();
/*    */   }
/*    */   
/*    */   public static <T extends Number> double clampedMapRange(T n, T fromStart, T fromEnd, T toStart, T toEnd) {
/* 15 */     double value = mapRange(n, fromStart, fromEnd, toStart, toEnd);
/* 16 */     double min = Math.min(toStart.doubleValue(), toEnd.doubleValue());
/* 17 */     double max = Math.max(toStart.doubleValue(), toEnd.doubleValue());
/*    */     
/* 19 */     return class_3532.method_15350(value, min, max);
/*    */   }
/*    */   
/*    */   public static float smoothValue(float value, float currentTime, float duration) {
/* 23 */     return class_3532.method_16439(Math.min(currentTime, duration) / duration, 0.0F, value);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\NumberHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */