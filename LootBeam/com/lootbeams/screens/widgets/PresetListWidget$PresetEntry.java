/*     */ package com.lootbeams.screens.widgets;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_364;
/*     */ import net.minecraft.class_4185;
/*     */ import net.minecraft.class_4265;
/*     */ import net.minecraft.class_6379;
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
/*     */ public class PresetEntry
/*     */   extends class_4265.class_4266<PresetListWidget.PresetEntry>
/*     */ {
/*     */   private final String presetName;
/*     */   private final PresetButtonWidget button;
/*     */   private final PresetListWidget parentWidget;
/*     */   
/*     */   public PresetEntry(String presetName, PresetListWidget parentWidget, PresetButtonWidget.Type buttonType) {
/* 243 */     this.presetName = presetName;
/* 244 */     this.parentWidget = parentWidget;
/* 245 */     this
/*     */ 
/*     */       
/* 248 */       .button = PresetButtonWidget.customBuilder((class_2561)class_2561.method_43470(presetName), btn -> {  }).type(buttonType).build();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<? extends class_6379> method_37025() {
/* 253 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25402(double mouseX, double mouseY, int button) {
/* 258 */     if (button == 0) {
/* 259 */       if (this.parentWidget.selectedEntry != this) {
/* 260 */         this.parentWidget.selectedEntry = this;
/*     */       } else {
/* 262 */         this.parentWidget.selectedEntry = null;
/*     */       } 
/* 264 */       return true;
/*     */     } 
/* 266 */     return false;
/*     */   }
/*     */   
/*     */   public String getPresetName() {
/* 270 */     return this.presetName;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<? extends class_364> method_25396() {
/* 275 */     return List.of();
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_25343(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
/* 280 */     boolean currentEntrySelected = (this.parentWidget.getSelectedEntry() == this);
/* 281 */     this.button.method_55444(entryWidth, entryHeight, x, y);
/* 282 */     this.button.field_22763 = currentEntrySelected;
/* 283 */     this.button.method_25394(context, mouseX, mouseY, tickDelta);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\screens\widgets\PresetListWidget$PresetEntry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */