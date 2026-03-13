/*     */ package com.lootbeams.helpers;
/*     */ 
/*     */ import com.google.common.collect.Lists;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.compat.prism.PrismCompat;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.features.CustomLootBeamsConfig;
/*     */ import com.lootbeams.features.CustomRarity;
/*     */ import com.lootbeams.managers.CrashManager;
/*     */ import com.mojang.datafixers.util.Pair;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.stream.Collectors;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_1792;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2487;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_5223;
/*     */ import net.minecraft.class_5251;
/*     */ import net.minecraft.class_6862;
/*     */ import net.minecraft.class_6885;
/*     */ import net.minecraft.class_7923;
/*     */ import net.minecraft.class_9279;
/*     */ import net.minecraft.class_9334;
/*     */ 
/*     */ 
/*     */ public class TextColorHelper
/*     */ {
/*  34 */   private static Map<class_1799, class_5251> DC_MAP = new HashMap<>();
/*  35 */   private static String NBT_COLOR_KEY = "lootbeams.color";
/*  36 */   private static String NBT_ANIMATED_COLOR_KEY = "lootbeams.animated_color";
/*     */   
/*     */   public static class_5251 getItemColor(class_1799 itemStack) {
/*  39 */     if (CrashManager.CRASH_BLACKLIST.contains(itemStack)) {
/*  40 */       return class_5251.method_27718(class_124.field_1068);
/*     */     }
/*     */     
/*  43 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  48 */       class_5251 override = getColorFromItemOverrides(itemStack.method_7909());
/*  49 */       if (override != null) {
/*  50 */         return override;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  57 */       if (itemStack.method_57353().method_57832(class_9334.field_49628)) {
/*  58 */         class_9279 data = (class_9279)itemStack.method_57824(class_9334.field_49628);
/*     */         
/*  60 */         if (data != null) {
/*  61 */           class_2487 value = data.method_57461();
/*  62 */           if (value.method_10545(NBT_ANIMATED_COLOR_KEY) && PrismCompat.isPrismLoaded()) {
/*  63 */             if (DC_MAP.containsKey(itemStack)) {
/*  64 */               return class_5251.method_27717(((class_5251)DC_MAP.get(itemStack)).method_27716());
/*     */             }
/*  66 */             class_5251 dColor = PrismCompat.parseColor(value.method_10558(NBT_ANIMATED_COLOR_KEY));
/*  67 */             DC_MAP.put(itemStack, dColor);
/*  68 */             return class_5251.method_27717(dColor.method_27716());
/*     */           } 
/*  70 */           if (value.method_10545(NBT_COLOR_KEY)) {
/*  71 */             return (class_5251)class_5251.method_27719(value.method_10558(NBT_COLOR_KEY)).getOrThrow();
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*  77 */       if (itemConfig.renderNameColor) {
/*  78 */         class_5251 nameColor = getRawColor(itemStack.method_7964());
/*  79 */         if (!nameColor.equals(class_5251.method_27718(class_124.field_1068))) {
/*  80 */           return nameColor;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  85 */       if (itemConfig.renderRarityColor) {
/*  86 */         CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
/*  87 */         if (customRarity != null) {
/*  88 */           return class_5251.method_27717(customRarity.getColor());
/*     */         }
/*  90 */         if (itemStack.method_7932().method_58413() != null) {
/*  91 */           return class_5251.method_27718(itemStack.method_7932().method_58413());
/*     */         }
/*     */       } 
/*     */       
/*  95 */       return class_5251.method_27718(class_124.field_1068);
/*  96 */     } catch (Exception e) {
/*  97 */       CrashManager.LOGGER.error("Failed to get color for ({}), added to temporary blacklist", itemStack.method_7954());
/*  98 */       CrashManager.CRASH_BLACKLIST.add(itemStack);
/*  99 */       CrashManager.LOGGER.info("Temporary blacklist is now : ");
/* 100 */       for (class_1799 s : CrashManager.CRASH_BLACKLIST) {
/* 101 */         CrashManager.LOGGER.info(s.method_7954());
/*     */       }
/* 103 */       return class_5251.method_27718(class_124.field_1068);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class_5251 getColorFromItemOverrides(class_1792 i) {
/* 108 */     List<String> overrides = LootBeams.config.colorOverrides;
/*     */     
/* 110 */     if (overrides.isEmpty()) {
/* 111 */       return null;
/*     */     }
/* 113 */     for (String unparsed : overrides.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList())) {
/* 114 */       String[] configValue = unparsed.split("=");
/* 115 */       if (configValue.length == 2) {
/* 116 */         String nameIn = configValue[0];
/* 117 */         class_2960 registry = class_2960.method_12829(nameIn.replace("#", ""));
/* 118 */         class_5251 colorIn = null;
/*     */         try {
/* 120 */           if (PrismCompat.isPrismLoaded()) {
/* 121 */             if (DC_MAP.containsKey(i.method_7854())) {
/* 122 */               colorIn = class_5251.method_27717(((class_5251)DC_MAP.get(i.method_7854())).method_27716());
/*     */             } else {
/* 124 */               class_5251 dColor = PrismCompat.parseColor(configValue[1]);
/* 125 */               DC_MAP.put(i.method_7854(), dColor);
/* 126 */               colorIn = class_5251.method_27717(dColor.method_27716());
/*     */             } 
/*     */           } else {
/* 129 */             colorIn = (class_5251)class_5251.method_27719(configValue[1]).getOrThrow();
/*     */           } 
/* 131 */         } catch (Exception e) {
/* 132 */           CrashManager.LOGGER.error(String.format("Color overrides error! \"%s\" is not a valid hex color for \"%s\"", new Object[] { configValue[1], nameIn }));
/* 133 */           return null;
/*     */         } 
/*     */ 
/*     */         
/* 137 */         if (!nameIn.contains(":") && 
/* 138 */           class_7923.field_41178.method_10221(i).method_12836().equals(nameIn)) {
/* 139 */           return colorIn;
/*     */         }
/*     */ 
/*     */         
/* 143 */         if (registry != null) {
/*     */           
/* 145 */           if (nameIn.startsWith("#")) {
/*     */             
/* 147 */             Optional<class_6885.class_6888<class_1792>> tag = class_7923.field_41178.method_40272().filter(pair -> ((class_6862)pair.getFirst()).comp_327().equals(registry)).findFirst().map(Pair::getSecond);
/*     */             
/* 149 */             if (tag.isPresent() && ((class_6885.class_6888)tag.get()).method_40241(class_7923.field_41178.method_40264(class_7923.field_41178.method_29113(i).get()).get())) {
/* 150 */               return colorIn;
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/* 155 */           Optional<class_1792> registryItem = class_7923.field_41178.method_17966(registry);
/* 156 */           if (registryItem.isPresent() && ((class_1792)registryItem.get()).method_8389() == i.method_8389()) {
/* 157 */             return colorIn;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class_5251 getRawColor(class_2561 text) {
/* 171 */     List<class_2583> list = Lists.newArrayList();
/* 172 */     text.method_27658((acceptor, styleIn) -> { class_5223.method_27479(styleIn, acceptor, ()); return Optional.empty(); }class_2583.field_24360);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 179 */     if (!list.isEmpty() && ((class_2583)list.get(0)).method_10973() != null) {
/* 180 */       return ((class_2583)list.get(0)).method_10973();
/*     */     }
/* 182 */     return class_5251.method_27718(class_124.field_1068);
/*     */   }
/*     */   
/*     */   private static String compensateHex(String hex) {
/* 186 */     StringBuilder compensatedHex = new StringBuilder();
/* 187 */     for (char c : hex.toCharArray()) {
/* 188 */       compensatedHex.append(c).append(c);
/*     */     }
/* 190 */     return compensatedHex.toString();
/*     */   }
/*     */   
/*     */   public static int getColorFromHEX(String hex) {
/* 194 */     int defaultColor = Integer.parseInt("FFFFFF", 16);
/*     */     
/* 196 */     if (hex == null || hex.isEmpty()) {
/* 197 */       return defaultColor;
/*     */     }
/*     */     
/* 200 */     if (hex.contains("#")) {
/* 201 */       hex = hex.replace("#", "");
/*     */     }
/*     */     
/* 204 */     if (!hex.isEmpty()) {
/* 205 */       switch (hex.length()) {
/*     */         case 3:
/* 207 */           return Integer.parseInt(compensateHex(hex), 16);
/*     */         case 4:
/* 209 */           return Integer.parseInt(compensateHex(hex).substring(0, 6), 16);
/*     */         case 6:
/* 211 */           return Integer.parseInt(hex, 16);
/*     */         case 8:
/* 213 */           return Integer.parseInt(hex.substring(0, 6), 16);
/*     */       } 
/*     */     
/*     */     }
/* 217 */     return defaultColor;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\TextColorHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */