/*    */ package com.lootbeams.compat.modmenu;
/*    */ 
/*    */ import com.terraformersmc.modmenu.api.ConfigScreenFactory;
/*    */ import com.terraformersmc.modmenu.api.ModMenuApi;
/*    */ import net.fabricmc.loader.api.FabricLoader;
/*    */ 
/*    */ public class LootBeamsModMenuEntry
/*    */   implements ModMenuApi {
/*    */   public ConfigScreenFactory<?> getModConfigScreenFactory() {
/* 10 */     if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
/* 11 */       return LootBeamsConfigScreen::getConfigScreen;
/*    */     }
/* 13 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\compat\modmenu\LootBeamsModMenuEntry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */