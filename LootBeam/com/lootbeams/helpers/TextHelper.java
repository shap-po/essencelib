/*    */ package com.lootbeams.helpers;
/*    */ 
/*    */ import net.minecraft.class_2561;
/*    */ import net.minecraft.class_327;
/*    */ 
/*    */ public class TextHelper {
/*    */   public static class_2561 centeredLine(class_2561 text, class_327 textRenderer, int width) {
/*  8 */     String centeredText = text.getString();
/*    */     
/* 10 */     while (textRenderer.method_1727(centeredText) < width)
/*    */     {
/* 12 */       centeredText = " " + centeredText + " ";
/*    */     }
/*    */     
/* 15 */     return (class_2561)class_2561.method_43470(centeredText).method_10862(text.method_10866());
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\TextHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */