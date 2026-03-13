/*     */ package com.lootbeams.screens;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.managers.PresetManager;
/*     */ import com.lootbeams.screens.widgets.PresetButtonWidget;
/*     */ import com.lootbeams.screens.widgets.PresetListWidget;
/*     */ import com.lootbeams.screens.widgets.PresetTextFieldWidget;
/*     */ import net.fabricmc.api.EnvType;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_364;
/*     */ import net.minecraft.class_368;
/*     */ import net.minecraft.class_370;
/*     */ import net.minecraft.class_4185;
/*     */ import net.minecraft.class_437;
/*     */ 
/*     */ @Environment(EnvType.CLIENT)
/*     */ public class LootBeamsPresetManagerScreen extends class_437 {
/*  19 */   private static final class_2960 MODAL_TEXTURE = LootBeams.id("textures/gui/screen/modal.png");
/*     */   
/*     */   private static final int BACKGROUND_WIDTH = 184;
/*     */   private static final int BACKGROUND_HEIGHT = 152;
/*     */   private class_437 parent;
/*     */   private PresetTextFieldWidget presetNameField;
/*     */   private PresetButtonWidget savePresetButton;
/*     */   private PresetButtonWidget loadPresetButton;
/*     */   private PresetListWidget presetListWidget;
/*     */   private PresetListWidget.PresetEntry addEntry;
/*     */   
/*     */   public LootBeamsPresetManagerScreen(class_437 parent) {
/*  31 */     super((class_2561)class_2561.method_43471("lootbeams.screen.presets.title"));
/*  32 */     this.parent = parent;
/*     */   }
/*     */   
/*     */   private void reinitPresetListWidget() {
/*  36 */     PresetManager.getPresetFiles();
/*  37 */     this.presetListWidget.method_25339();
/*  38 */     this.presetListWidget.addEntries(PresetManager.getPresetNames());
/*  39 */     this.addEntry = this.presetListWidget.addEntry("+", PresetButtonWidget.Type.Add);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void method_25426() {
/*  44 */     int topPos = this.field_22790 / 2 - 76;
/*  45 */     int bottomPos = this.field_22790 / 2 + 76;
/*  46 */     int leftPos = this.field_22789 / 2 - 92;
/*  47 */     int rightPos = this.field_22789 / 2 + 92;
/*  48 */     int topHeaderPos = topPos + 29;
/*     */     
/*  50 */     this.presetListWidget = new PresetListWidget(this.field_22787, 84, 118, topHeaderPos + 2, 20);
/*  51 */     this.presetListWidget.method_48229(leftPos + 5, topHeaderPos + 2);
/*  52 */     reinitPresetListWidget();
/*  53 */     method_37063((class_364)this.presetListWidget);
/*     */     
/*  55 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  61 */       .presetNameField = new PresetTextFieldWidget(this.field_22793, rightPos - 84, topHeaderPos + 10, 78, 22, (class_2561)class_2561.method_43471("lootbeams.screen.presets.presetNameTextField"));
/*     */     
/*  63 */     this.presetNameField.setPlaceholder((class_2561)class_2561.method_43471("lootbeams.screen.presets.presetNameTextField"));
/*  64 */     this.presetNameField.setMaxLength(64);
/*  65 */     method_25429((class_364)this.presetNameField);
/*  66 */     this.presetNameField.setChangedListener(value -> {
/*     */           if (this.savePresetButton != null) {
/*     */             this.savePresetButton.field_22763 = !value.trim().isEmpty();
/*     */           }
/*     */         });
/*     */     
/*  72 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  96 */       .savePresetButton = PresetButtonWidget.customBuilder((class_2561)class_2561.method_43471("lootbeams.screen.presets.button.save"), btn -> { String presetName = this.presetNameField.getText().trim(); if (presetName.isEmpty()) { try { this.field_22787.method_1566().method_1999((class_368)class_370.method_29047(this.field_22787, class_370.class_9037.field_47586, class_2561.method_30163("LootBeams"), (class_2561)class_2561.method_43471("lootbeams.notification.savePreset.notEnteredPresetName"))); } catch (Exception e) { e.printStackTrace(); }  return; }  PresetManager.savePreset(PresetManager.getPresetFileName(presetName)); reinitPresetListWidget(); btn.field_22763 = false; }).position(rightPos - 83, topHeaderPos + 47).size(76, 19).build();
/*  97 */     this.savePresetButton.field_22763 = false;
/*     */     
/*  99 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 109 */       .loadPresetButton = PresetButtonWidget.customBuilder((class_2561)class_2561.method_43471("lootbeams.screen.presets.button.load"), btn -> { PresetListWidget.PresetEntry presetEntry = this.presetListWidget.getSelectedEntry(); if (presetEntry != null) { String presetName = presetEntry.getPresetName(); PresetManager.loadAndApplyPreset(presetName); }  }).position(rightPos - 83, topHeaderPos + 47).size(76, 19).build();
/* 110 */     this.loadPresetButton.field_22763 = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 119 */     PresetButtonWidget cancelButton = PresetButtonWidget.customBuilder((class_2561)class_2561.method_43471("lootbeams.screen.presets.button.cancel"), btn -> class_310.method_1551().method_1507(this.parent)).position(rightPos - 83, bottomPos - 27).size(76, 19).type(PresetButtonWidget.Type.Cancel).build();
/*     */     
/* 121 */     method_25429((class_364)this.savePresetButton);
/* 122 */     method_25429((class_364)this.loadPresetButton);
/* 123 */     method_37063((class_364)cancelButton);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25421() {
/* 128 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
/* 133 */     if (keyCode == 258 || keyCode == 264 || keyCode == 265)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     return super.method_25404(keyCode, scanCode, modifiers);
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
/* 147 */     method_25420(context, mouseX, mouseY, delta);
/* 148 */     super.method_25394(context, mouseX, mouseY, delta);
/* 149 */     if (this.presetListWidget.getSelectedEntry() == this.addEntry) {
/* 150 */       this.presetNameField.method_25394(context, mouseX, mouseY, delta);
/* 151 */       this.savePresetButton.method_25394(context, mouseX, mouseY, delta);
/*     */     } else {
/* 153 */       PresetListWidget.PresetEntry presetEntry = this.presetListWidget.getSelectedEntry();
/* 154 */       this.loadPresetButton.field_22763 = (presetEntry != null && !presetEntry.getPresetName().equals(LootBeams.config.selectedPreset));
/* 155 */       this.loadPresetButton.method_25394(context, mouseX, mouseY, delta);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
/* 161 */     super.method_25420(context, mouseX, mouseY, delta);
/* 162 */     int i = (this.field_22789 - 184) / 2;
/* 163 */     int j = (this.field_22790 - 152) / 2;
/* 164 */     context.method_25302(MODAL_TEXTURE, i, j, 0, 0, 184, 152);
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_25419() {
/* 169 */     this.field_22787.method_1507(this.parent);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\screens\LootBeamsPresetManagerScreen.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */