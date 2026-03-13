/*    */ package com.lootbeams.helpers;
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
/*    */ public final class FloatRGB
/*    */   extends Record
/*    */ {
/*    */   private final float R;
/*    */   private final float G;
/*    */   private final float B;
/*    */   
/*    */   public final int hashCode() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lcom/lootbeams/helpers/ColorHelper$FloatRGB;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #45	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lcom/lootbeams/helpers/ColorHelper$FloatRGB;
/*    */   }
/*    */   
/*    */   public final boolean equals(Object o) {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lcom/lootbeams/helpers/ColorHelper$FloatRGB;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #45	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lcom/lootbeams/helpers/ColorHelper$FloatRGB;
/*    */     //   0	8	1	o	Ljava/lang/Object;
/*    */   }
/*    */   
/*    */   public FloatRGB(float R, float G, float B) {
/* 45 */     this.R = R; this.G = G; this.B = B; } public float R() { return this.R; } public float G() { return this.G; } public float B() { return this.B; }
/*    */    public FloatRGB lighter(float value) {
/* 47 */     return new FloatRGB(
/* 48 */         Math.min(this.R + value, 1.0F), 
/* 49 */         Math.min(this.G + value, 1.0F), 
/* 50 */         Math.min(this.B + value, 1.0F));
/*    */   }
/*    */ 
/*    */   
/*    */   public FloatRGB darken(float value) {
/* 55 */     return new FloatRGB(
/* 56 */         Math.max(this.R - value, 0.0F), 
/* 57 */         Math.max(this.G - value, 0.0F), 
/* 58 */         Math.max(this.B - value, 0.0F));
/*    */   }
/*    */ 
/*    */   
/*    */   public int pack() {
/* 63 */     return (int)(this.R * 255.0F) << 16 | (int)(this.G * 255.0F) << 8 | (int)(this.B * 255.0F);
/*    */   }
/*    */   
/*    */   public static FloatRGB of(ColorHelper.Color color) {
/* 67 */     return new FloatRGB(color.fR, color.fG, color.fB);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "RGB{R=" + this.R + ", G=" + this.G + ", B=" + this.B + "}";
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\ColorHelper$FloatRGB.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */