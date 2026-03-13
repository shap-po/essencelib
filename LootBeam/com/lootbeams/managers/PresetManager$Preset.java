/*    */ package com.lootbeams.managers;
/*    */ 
/*    */ import java.io.File;
/*    */ import net.minecraft.class_2960;
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
/*    */ public class Preset
/*    */ {
/*    */   private final PresetManager.PresetType type;
/*    */   private final String displayName;
/*    */   private final class_2960 identifier;
/*    */   private final File presetFile;
/*    */   
/*    */   public Preset(String displayName, class_2960 identifier) {
/* 35 */     this.type = PresetManager.PresetType.IDENTIFIER;
/* 36 */     this.displayName = displayName;
/* 37 */     this.identifier = identifier;
/* 38 */     this.presetFile = null;
/*    */   }
/*    */   
/*    */   public Preset(String displayName, File presetFile) {
/* 42 */     this.type = PresetManager.PresetType.FILE;
/* 43 */     this.displayName = displayName;
/* 44 */     this.identifier = null;
/* 45 */     this.presetFile = presetFile;
/*    */   }
/*    */   
/*    */   public String getDisplayName() {
/* 49 */     return this.displayName;
/*    */   }
/*    */   
/*    */   public class_2960 getIdentifier() {
/* 53 */     return this.identifier;
/*    */   }
/*    */   
/*    */   public File getPresetFile() {
/* 57 */     return this.presetFile;
/*    */   }
/*    */   
/*    */   public PresetManager.PresetType getType() {
/* 61 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return this.displayName;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\PresetManager$Preset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */