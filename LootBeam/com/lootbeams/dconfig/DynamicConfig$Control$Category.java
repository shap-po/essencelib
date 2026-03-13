/*    */ package com.lootbeams.dconfig;
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
/*    */ public class Category
/*    */ {
/*    */   public String key;
/*    */   public String name;
/*    */   public String saveKey;
/*    */   public boolean displayOnConfigScreen = true;
/*    */   public boolean isRootCategory = false;
/*    */   
/*    */   public Category(String key, String name, String saveKey) {
/* 51 */     this.key = key;
/* 52 */     this.name = name;
/* 53 */     this.saveKey = saveKey;
/*    */   }
/*    */   
/*    */   public Category setAsRoot(boolean isRootCategory) {
/* 57 */     this.isRootCategory = isRootCategory;
/* 58 */     return this;
/*    */   }
/*    */   
/*    */   public Category setDisplayOnConfigScreen(boolean displayOnConfigScreen) {
/* 62 */     this.displayOnConfigScreen = displayOnConfigScreen;
/* 63 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return "DynamicConfig.Category[key=" + this.key + ", name=" + this.name + ", displayOnConfigScreen=" + this.displayOnConfigScreen + ", isRootCategory=" + this.isRootCategory + "]";
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\DynamicConfig$Control$Category.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */