/*     */ package com.lootbeams.screens.widgets;
/*     */ 
/*     */ import net.fabricmc.api.EnvType;
/*     */ import net.fabricmc.api.Environment;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_4185;
/*     */ import net.minecraft.class_7919;
/*     */ import org.jetbrains.annotations.Nullable;
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
/*     */ @Environment(EnvType.CLIENT)
/*     */ public class Builder
/*     */ {
/*     */   private final class_2561 message;
/*     */   private final class_4185.class_4241 onPress;
/*     */   @Nullable
/*     */   private class_7919 tooltip;
/*  93 */   private PresetButtonWidget.Type type = PresetButtonWidget.Type.Default;
/*     */   private int x;
/*     */   private int y;
/*  96 */   private int width = 150;
/*  97 */   private int height = 20;
/*  98 */   private class_4185.class_7841 narrationSupplier = PresetButtonWidget.access$000();
/*     */   
/*     */   public Builder(class_2561 message, class_4185.class_4241 onPress) {
/* 101 */     this.message = message;
/* 102 */     this.onPress = onPress;
/*     */   }
/*     */   
/*     */   public Builder position(int x, int y) {
/* 106 */     this.x = x;
/* 107 */     this.y = y;
/* 108 */     return this;
/*     */   }
/*     */   
/*     */   public Builder width(int width) {
/* 112 */     this.width = width;
/* 113 */     return this;
/*     */   }
/*     */   
/*     */   public Builder size(int width, int height) {
/* 117 */     this.width = width;
/* 118 */     this.height = height;
/* 119 */     return this;
/*     */   }
/*     */   
/*     */   public Builder dimensions(int x, int y, int width, int height) {
/* 123 */     return position(x, y).size(width, height);
/*     */   }
/*     */   
/*     */   public Builder tooltip(@Nullable class_7919 tooltip) {
/* 127 */     this.tooltip = tooltip;
/* 128 */     return this;
/*     */   }
/*     */   
/*     */   public Builder narrationSupplier(class_4185.class_7841 narrationSupplier) {
/* 132 */     this.narrationSupplier = narrationSupplier;
/* 133 */     return this;
/*     */   }
/*     */   
/*     */   public Builder type(PresetButtonWidget.Type type) {
/* 137 */     this.type = type;
/* 138 */     return this;
/*     */   }
/*     */   
/*     */   public PresetButtonWidget build() {
/* 142 */     PresetButtonWidget buttonWidget = new PresetButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier, this.type);
/* 143 */     buttonWidget.method_47400(this.tooltip);
/* 144 */     return buttonWidget;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\screens\widgets\PresetButtonWidget$Builder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */