/*    */ package com.lootbeams.render;
/*    */ 
/*    */ import com.lootbeams.compat.iris.IrisCompat;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.minecraft.class_293;
/*    */ import net.minecraft.class_296;
/*    */ 
/*    */ 
/*    */ public class CustomVertexFormats
/*    */ {
/*    */   public static class_296 COLOR1;
/*    */   public static class_296 UV_CENTER;
/*    */   public static class_296 UV_SIZE;
/*    */   public static class_296 CUSTOM_DATA;
/*    */   public static class_293 POSITION_TEX_COLOR0_COLOR1_CUSTOM;
/*    */   public static class_293 POSITION_TEX_COLOR0_COLOR1_CENTER;
/* 18 */   private static List<Integer> EMPTY_INDEXES = new ArrayList<>();
/*    */   
/*    */   private static int getAvailableIndex() {
/* 21 */     return ((Integer)EMPTY_INDEXES.removeFirst()).intValue();
/*    */   }
/*    */   
/*    */   static {
/* 25 */     for (int i = 0; i < 32; i++) {
/* 26 */       class_296 element = class_296.method_60844(i);
/* 27 */       if (!IrisCompat.isIrisLoaded() || !IrisCompat.getVertexFormatsIndexes().contains(Integer.valueOf(i)))
/*    */       {
/*    */         
/* 30 */         if (element == null) {
/* 31 */           EMPTY_INDEXES.add(Integer.valueOf(i));
/*    */         }
/*    */       }
/*    */     } 
/* 35 */     COLOR1 = class_296.method_60845(getAvailableIndex(), 0, class_296.class_297.field_1624, class_296.class_298.field_1632, 4);
/* 36 */     UV_CENTER = class_296.method_60845(getAvailableIndex(), 0, class_296.class_297.field_1623, class_296.class_298.field_1636, 2);
/* 37 */     UV_SIZE = class_296.method_60845(getAvailableIndex(), 0, class_296.class_297.field_1623, class_296.class_298.field_1636, 2);
/* 38 */     CUSTOM_DATA = class_296.method_60845(getAvailableIndex(), 0, class_296.class_297.field_1623, class_296.class_298.field_20782, 4);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 45 */     POSITION_TEX_COLOR0_COLOR1_CUSTOM = class_293.method_60833().method_60842("Position", class_296.field_52107).method_60842("UV0", class_296.field_52109).method_60842("Color0", class_296.field_52108).method_60842("Color1", COLOR1).method_60842("CustomData", CUSTOM_DATA).method_60840();
/* 46 */     POSITION_TEX_COLOR0_COLOR1_CENTER = class_293.method_60833().method_60842("Position", class_296.field_52107).method_60842("UV0", class_296.field_52109).method_60842("Color0", class_296.field_52108).method_60842("Color1", COLOR1).method_60842("CenterUV", UV_CENTER).method_60842("SizeUV", UV_SIZE).method_60842("GradientBounds", CUSTOM_DATA).method_60840();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\render\CustomVertexFormats.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */