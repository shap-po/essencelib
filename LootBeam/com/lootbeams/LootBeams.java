/*    */ package com.lootbeams;
/*    */ 
/*    */ import com.lootbeams.compat.iceberg.IcebergCompat;
/*    */ import com.lootbeams.config.Configuration;
/*    */ import com.lootbeams.config.JsonConfigAdapter;
/*    */ import com.lootbeams.dconfig.DynamicConfig;
/*    */ import com.lootbeams.dconfig.interfaces.DynamicConfigAdapter;
/*    */ import com.lootbeams.managers.ParticleManager;
/*    */ import net.fabricmc.api.ClientModInitializer;
/*    */ import net.minecraft.class_2960;
/*    */ 
/*    */ public class LootBeams implements ClientModInitializer {
/*    */   public static final String MODID = "lootbeams";
/*    */   public static DynamicConfig.ConfigManager<Configuration> configManager;
/*    */   public static Configuration config;
/*    */   
/*    */   public LootBeams() {
/* 18 */     configManager = DynamicConfig.load("lootbeams", Configuration.class, (DynamicConfigAdapter)new JsonConfigAdapter());
/* 19 */     config = (Configuration)configManager.getConfig();
/*    */     
/* 21 */     if (IcebergCompat.isIcebergLoaded()) {
/* 22 */       System.out.println(IcebergCompat.getPlatformName());
/*    */     }
/*    */   }
/*    */   
/*    */   public static class_2960 id(String name) {
/* 27 */     return class_2960.method_60655("lootbeams", name);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onInitializeClient() {
/* 32 */     ClientSetup.registerCoreShaderRegistrationEvents();
/* 33 */     ClientSetup.registerHudRenderEvents();
/* 34 */     ClientSetup.registerWorldRenderEvents();
/* 35 */     ClientSetup.registerEntityEvents();
/* 36 */     ClientSetup.registerCommands();
/* 37 */     ClientSetup.registerKeyBindings();
/* 38 */     ClientSetup.registerClientEvents();
/* 39 */     ClientSetup.registerCustomEvents();
/* 40 */     ParticleManager.registerParticles();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\LootBeams.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */