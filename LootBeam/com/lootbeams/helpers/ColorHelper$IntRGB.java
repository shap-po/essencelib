/*    */ package com.lootbeams.helpers;
/*    */ public final class IntRGB extends Record { private final int R; private final int G; private final int B;
/*    */   public final int hashCode() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lcom/lootbeams/helpers/ColorHelper$IntRGB;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #10	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lcom/lootbeams/helpers/ColorHelper$IntRGB;
/*    */   }
/*    */   public final boolean equals(Object o) {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lcom/lootbeams/helpers/ColorHelper$IntRGB;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #10	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lcom/lootbeams/helpers/ColorHelper$IntRGB;
/*    */     //   0	8	1	o	Ljava/lang/Object;
/*    */   }
/*    */   
/* 10 */   public IntRGB(int R, int G, int B) { this.R = R; this.G = G; this.B = B; } public int R() { return this.R; } public int G() { return this.G; } public int B() { return this.B; }
/*    */    public IntRGB lighter(int value) {
/* 12 */     return new IntRGB(
/* 13 */         Math.min(this.R + value, 255), 
/* 14 */         Math.min(this.G + value, 255), 
/* 15 */         Math.min(this.B + value, 255));
/*    */   }
/*    */ 
/*    */   
/*    */   public IntRGB darken(int value) {
/* 20 */     return new IntRGB(
/* 21 */         Math.max(this.R - value, 0), 
/* 22 */         Math.max(this.G - value, 0), 
/* 23 */         Math.max(this.B - value, 0));
/*    */   }
/*    */ 
/*    */   
/*    */   public int pack() {
/* 28 */     return this.R << 16 | this.G << 8 | this.B;
/*    */   }
/*    */   
/*    */   public static IntRGB of(ColorHelper.Color color) {
/* 32 */     return new IntRGB(color.R, color.G, color.B);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "RGB{R=" + this.R + ", G=" + this.G + ", B=" + this.B + "}";
/*    */   } }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\ColorHelper$IntRGB.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */