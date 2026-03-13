/*     */ package com.lootbeams.managers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OffsetContainer
/*     */ {
/*     */   private final int offset;
/*     */   private final int halfTooltipWidth;
/*     */   
/*     */   public OffsetContainer(int offset, int halfTooltipWidth) {
/*  97 */     this.offset = offset;
/*  98 */     this.halfTooltipWidth = halfTooltipWidth;
/*     */   }
/*     */   
/*     */   public int getOffset() {
/* 102 */     return this.offset;
/*     */   }
/*     */   
/*     */   public int getHalfTooltipWidth() {
/* 106 */     return this.halfTooltipWidth;
/*     */   }
/*     */   
/*     */   public int getTooltipWidth() {
/* 110 */     return this.halfTooltipWidth * 2;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\TooltipManager$OffsetContainer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */