/*     */ package com.lootbeams.screens.widgets;
/*     */ 
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.helpers.RenderHelper;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import net.fabricmc.api.EnvType;
/*     */ import net.fabricmc.api.Environment;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_350;
/*     */ import net.minecraft.class_3532;
/*     */ import net.minecraft.class_364;
/*     */ import net.minecraft.class_4185;
/*     */ import net.minecraft.class_4265;
/*     */ import net.minecraft.class_6379;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ @Environment(EnvType.CLIENT)
/*     */ public class PresetListWidget extends class_4265<PresetListWidget.PresetEntry> {
/*     */   private PresetEntry selectedEntry;
/*     */   private boolean isScrolling;
/*     */   private double dragOffsetY;
/*     */   @Nullable
/*     */   private PresetEntry hoveredEntry;
/*  30 */   private static final class_2960 SCROLLER_TEXTURE = LootBeams.id("textures/gui/scroller/scroller.png");
/*  31 */   private static final class_2960 SCROLLER_BACKGROUND_TEXTURE = LootBeams.id("textures/gui/scroller/scroller_background.png");
/*     */   
/*     */   public PresetListWidget(class_310 client, int width, int height, int y, int itemHeight) {
/*  34 */     super(client, width, height, y, itemHeight);
/*  35 */     this.isScrolling = false;
/*     */   }
/*     */   
/*     */   public void addEntries(Iterable<String> presetNames) {
/*  39 */     for (String presetName : presetNames) {
/*  40 */       PresetEntry entry = new PresetEntry(presetName, this, PresetButtonWidget.Type.Default);
/*  41 */       method_25321((class_350.class_351)entry);
/*  42 */       if (presetName.equals(LootBeams.config.selectedPreset)) {
/*  43 */         this.selectedEntry = entry;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void method_25339() {
/*  49 */     super.method_25339();
/*     */   }
/*     */   
/*     */   public PresetEntry addEntry(String name, PresetButtonWidget.Type buttonType) {
/*  53 */     PresetEntry entry = new PresetEntry(name, this, buttonType);
/*  54 */     method_25321((class_350.class_351)entry);
/*  55 */     return entry;
/*     */   }
/*     */   
/*     */   private int getScrollbarWidth() {
/*  59 */     return 6;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int method_25329() {
/*  64 */     return method_46426() + method_25368() - getScrollbarWidth() + 4;
/*     */   }
/*     */ 
/*     */   
/*     */   public int method_25322() {
/*  69 */     return method_25368() - getScrollbarWidth() - 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public int method_25342() {
/*  74 */     return method_46426() + 3;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int method_25337(int index) {
/*  79 */     return method_46427() + 2 - (int)method_25341() + index * this.field_22741 + this.field_22748;
/*     */   }
/*     */ 
/*     */   
/*     */   public int method_25331() {
/*  84 */     return Math.max(0, method_25317() - this.field_22759 + 2);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void method_25311(class_332 context, int mouseX, int mouseY, float delta) {
/*  89 */     int i = method_25342();
/*  90 */     int j = method_25322();
/*  91 */     int k = this.field_22741 - 2;
/*  92 */     int l = method_25340();
/*     */     
/*  94 */     for (int m = 0; m < l; m++) {
/*  95 */       int n = method_25337(m);
/*  96 */       int o = method_25319(m);
/*  97 */       if (o >= method_46427() && n <= method_55443()) {
/*  98 */         method_44397(context, mouseX, mouseY, delta, m, i, n, j, k);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void method_44397(class_332 context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
/* 105 */     PresetEntry entry = (PresetEntry)method_25326(index);
/* 106 */     entry.method_49568(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
/* 107 */     if (method_25332(index)) {
/* 108 */       int i = method_25370() ? -1 : -8355712;
/* 109 */       method_44398(context, y, entryWidth, entryHeight, i, -16777216);
/*     */     } 
/*     */     
/* 112 */     entry.method_25343(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
/*     */   }
/*     */   
/*     */   private void renderScroller(class_332 context, int x, int y, int width, int height) {
/* 116 */     int topHeight = 6;
/* 117 */     int bottomHeight = 6;
/* 118 */     int middleHeight = 18;
/*     */     
/* 120 */     RenderHelper.blit(context, SCROLLER_TEXTURE, x, y, width, topHeight, 0, 0, width, topHeight);
/*     */     
/* 122 */     int remainingHeight = height - topHeight - bottomHeight;
/* 123 */     int middleRepeats = remainingHeight / middleHeight;
/* 124 */     int middleRemainder = remainingHeight % middleHeight;
/*     */     
/* 126 */     for (int i = 0; i < middleRepeats; i++) {
/* 127 */       RenderHelper.blit(context, SCROLLER_TEXTURE, x, y + topHeight + i * middleHeight, width, middleHeight, 0, 7, width, middleHeight);
/*     */     }
/*     */     
/* 130 */     if (middleRemainder > 0) {
/* 131 */       RenderHelper.blit(context, SCROLLER_TEXTURE, x, y + topHeight + middleRepeats * middleHeight, width, middleRemainder, 0, 7, width, middleRemainder);
/*     */     }
/*     */     
/* 134 */     RenderHelper.blit(context, SCROLLER_TEXTURE, x, y + height - bottomHeight, width, bottomHeight, 0, 26, width, bottomHeight);
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_48579(class_332 context, int mouseX, int mouseY, float delta) {
/* 139 */     this.hoveredEntry = method_25405(mouseX, mouseY) ? (PresetEntry)method_25308(mouseX, mouseY) : null;
/* 140 */     method_57715(context);
/* 141 */     method_49603(context);
/* 142 */     method_25311(context, mouseX, mouseY, delta);
/* 143 */     context.method_44380();
/* 144 */     method_57713(context);
/* 145 */     if (method_57717()) {
/* 146 */       int i = method_25329();
/* 147 */       int j = (int)((this.field_22759 * this.field_22759) / method_25317());
/* 148 */       j = class_3532.method_15340(j, 32, this.field_22759 - 8);
/* 149 */       int k = (int)method_25341() * (this.field_22759 - j) / method_25331() + method_46427();
/* 150 */       if (k < method_46427()) {
/* 151 */         k = method_46427();
/*     */       }
/*     */       
/* 154 */       RenderSystem.enableBlend();
/* 155 */       RenderHelper.blit(context, SCROLLER_BACKGROUND_TEXTURE, i, method_46427() - 1, 6, method_25364() + 2, 0, 0);
/* 156 */       renderScroller(context, i, k - 1, 6, j + 2);
/* 157 */       RenderSystem.disableBlend();
/*     */     } 
/*     */     
/* 160 */     method_25320(context, mouseX, mouseY);
/* 161 */     RenderSystem.disableBlend();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void method_57715(class_332 context) {}
/*     */ 
/*     */   
/*     */   protected void method_57713(class_332 context) {}
/*     */   
/*     */   public PresetEntry getSelectedEntry() {
/* 171 */     return this.selectedEntry;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25403(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
/* 176 */     if (!this.isScrolling) {
/* 177 */       return false;
/*     */     }
/*     */     
/* 180 */     if (mouseY >= method_46427() && mouseY <= method_55443()) {
/* 181 */       double scrollRange = method_25331();
/* 182 */       double scrollbarRange = method_25364();
/*     */       
/* 184 */       double scrollPercentage = (mouseY - method_46427() - this.dragOffsetY) / (scrollbarRange - getScrollbarHeight());
/* 185 */       method_25307(scrollRange * scrollPercentage);
/*     */       
/* 187 */       return true;
/*     */     } 
/*     */     
/* 190 */     return super.method_25403(mouseX, mouseY, button, deltaX, deltaY);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25402(double mouseX, double mouseY, int button) {
/* 195 */     if (button == 0) {
/* 196 */       int scrollbarY = getScrollbarY();
/* 197 */       int scrollbarHeight = getScrollbarHeight();
/*     */       
/* 199 */       if (mouseX >= method_25329() && mouseX <= (method_25329() + getScrollbarWidth()) && mouseY >= scrollbarY && mouseY <= (scrollbarY + scrollbarHeight)) {
/*     */ 
/*     */         
/* 202 */         this.dragOffsetY = mouseY - scrollbarY;
/* 203 */         this.isScrolling = true;
/* 204 */         return true;
/*     */       } 
/*     */     } 
/* 207 */     return super.method_25402(mouseX, mouseY, button);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25406(double mouseX, double mouseY, int button) {
/* 212 */     if (button == 0) {
/* 213 */       this.isScrolling = false;
/*     */     }
/* 215 */     return super.method_25406(mouseX, mouseY, button);
/*     */   }
/*     */   
/*     */   private int getScrollbarY() {
/* 219 */     int scrollbarHeight = getScrollbarHeight();
/* 220 */     int maxScroll = method_25331();
/* 221 */     if (maxScroll <= 0) {
/* 222 */       return method_46427();
/*     */     }
/*     */     
/* 225 */     int scrollbarPositionY = (int)method_25341() * (this.field_22759 - scrollbarHeight) / maxScroll + method_46427();
/* 226 */     if (scrollbarPositionY < method_46427()) {
/* 227 */       scrollbarPositionY = method_46427();
/*     */     }
/* 229 */     return scrollbarPositionY;
/*     */   }
/*     */   
/*     */   private int getScrollbarHeight() {
/* 233 */     int j = (int)((this.field_22759 * this.field_22759) / method_25317());
/* 234 */     return class_3532.method_15340(j, 32, this.field_22759 - 8);
/*     */   }
/*     */   
/*     */   public static class PresetEntry extends class_4265.class_4266<PresetEntry> {
/*     */     private final String presetName;
/*     */     private final PresetButtonWidget button;
/*     */     private final PresetListWidget parentWidget;
/*     */     
/*     */     public PresetEntry(String presetName, PresetListWidget parentWidget, PresetButtonWidget.Type buttonType) {
/* 243 */       this.presetName = presetName;
/* 244 */       this.parentWidget = parentWidget;
/* 245 */       this
/*     */ 
/*     */         
/* 248 */         .button = PresetButtonWidget.customBuilder((class_2561)class_2561.method_43470(presetName), btn -> {  }).type(buttonType).build();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<? extends class_6379> method_37025() {
/* 253 */       return Collections.emptyList();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean method_25402(double mouseX, double mouseY, int button) {
/* 258 */       if (button == 0) {
/* 259 */         if (this.parentWidget.selectedEntry != this) {
/* 260 */           this.parentWidget.selectedEntry = this;
/*     */         } else {
/* 262 */           this.parentWidget.selectedEntry = null;
/*     */         } 
/* 264 */         return true;
/*     */       } 
/* 266 */       return false;
/*     */     }
/*     */     
/*     */     public String getPresetName() {
/* 270 */       return this.presetName;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<? extends class_364> method_25396() {
/* 275 */       return List.of();
/*     */     }
/*     */ 
/*     */     
/*     */     public void method_25343(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
/* 280 */       boolean currentEntrySelected = (this.parentWidget.getSelectedEntry() == this);
/* 281 */       this.button.method_55444(entryWidth, entryHeight, x, y);
/* 282 */       this.button.field_22763 = currentEntrySelected;
/* 283 */       this.button.method_25394(context, mouseX, mouseY, tickDelta);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\screens\widgets\PresetListWidget.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */