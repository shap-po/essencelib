/*     */ package com.lootbeams.features;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.compat.prism.PrismCompat;
/*     */ import com.lootbeams.helpers.TextColorHelper;
/*     */ import com.lootbeams.utils.Selectors;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2487;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_3298;
/*     */ import net.minecraft.class_3300;
/*     */ import net.minecraft.class_3518;
/*     */ import net.minecraft.class_5251;
/*     */ import net.minecraft.class_7225;
/*     */ import net.minecraft.class_9279;
/*     */ import net.minecraft.class_9334;
/*     */ 
/*     */ public class CustomRarity {
/*     */   private static final String CUSTOM_RARITIES_FILE = "custom_rarities.json";
/*     */   private static final String CUSTOM_RARITIES_JSON_KEY = "custom_rarities";
/*  30 */   private static final Map<String, CustomRarity> RARITY_CACHE = new HashMap<>();
/*     */   
/*     */   private static final String CUSTOM_RARITY_KEY = "custom_rarity";
/*     */   
/*     */   private static final String CUSTOM_RARITY_ID_KEY = "id";
/*     */   private static final String CUSTOM_RARITY_NAME_KEY = "name";
/*     */   private static final String CUSTOM_RARITY_COLOR_KEY = "color";
/*     */   private static final String CUSTOM_RARITY_ANIMATED_COLOR_KEY = "animated_color";
/*     */   private static final String CUSTOM_RARITY_CONFIG_KEY = "config";
/*     */   private static final String CUSTOM_RARITY_SELECTORS_KEY = "selectors";
/*     */   public static final String ITEM_CUSTOM_RARITY_ID_KEY = "custom_rarity_id";
/*  41 */   private String name = "";
/*  42 */   private int color = 16777215;
/*  43 */   private class_5251 animatedColor = null;
/*  44 */   private JsonObject rarityConfig = null;
/*  45 */   private List<String> selectors = new ArrayList<>();
/*     */   
/*     */   public CustomRarity(String rarityName) {
/*  48 */     this.name = rarityName;
/*     */   }
/*     */   
/*     */   public CustomRarity setColor(int rarityColor) {
/*  52 */     this.color = rarityColor;
/*  53 */     return this;
/*     */   }
/*     */   
/*     */   public CustomRarity setAnimatedColor(class_5251 prismColor) {
/*  57 */     this.animatedColor = prismColor;
/*  58 */     return this;
/*     */   }
/*     */   
/*     */   public CustomRarity setConfig(JsonObject rarityConfig) {
/*  62 */     this.rarityConfig = rarityConfig;
/*  63 */     return this;
/*     */   }
/*     */   
/*     */   public CustomRarity setSelectors(List<String> selectors) {
/*  67 */     this.selectors = selectors;
/*  68 */     return this;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  72 */     return this.name;
/*     */   }
/*     */   
/*     */   public int getColor() {
/*  76 */     if (this.animatedColor != null) {
/*  77 */       return this.animatedColor.method_27716();
/*     */     }
/*  79 */     return this.color;
/*     */   }
/*     */   
/*     */   public boolean hasRarityConfig() {
/*  83 */     return (this.rarityConfig != null);
/*     */   }
/*     */   
/*     */   public List<String> getSelectors() {
/*  87 */     return this.selectors;
/*     */   }
/*     */   
/*     */   public JsonObject getRarityConfig() {
/*  91 */     return this.rarityConfig;
/*     */   }
/*     */   
/*     */   public static CustomRarity fromSelectors(class_1799 itemStack) {
/*  95 */     for (CustomRarity rarity : RARITY_CACHE.values()) {
/*  96 */       if (!rarity.getSelectors().isEmpty() && (class_310.method_1551()).field_1687 != null) {
/*  97 */         for (String selector : rarity.getSelectors()) {
/*  98 */           if (Selectors.validateSelector(selector) && Selectors.itemMatches(itemStack, selector, (class_7225.class_7874)(class_310.method_1551()).field_1687.method_30349())) {
/*  99 */             return rarity;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 105 */     return null;
/*     */   }
/*     */   
/*     */   public static CustomRarity fromItemStack(class_1799 itemStack) {
/* 109 */     if (itemStack.method_57353().method_57832(class_9334.field_49628)) {
/* 110 */       class_9279 itemCustomData = (class_9279)itemStack.method_57824(class_9334.field_49628);
/*     */       
/* 112 */       if (itemCustomData != null) {
/* 113 */         class_2487 customDataNbt = itemCustomData.method_57461();
/*     */         
/* 115 */         if (!customDataNbt.method_33133()) {
/* 116 */           String customRarityId = customDataNbt.method_10558("custom_rarity_id");
/* 117 */           class_2487 customRarity = customDataNbt.method_10562("custom_rarity");
/*     */           
/* 119 */           if (!customRarity.method_33133()) {
/* 120 */             String rarityName = customRarity.method_10558("name");
/* 121 */             String hexColor = customRarity.method_10558("color");
/* 122 */             String animatedHexColor = customRarity.method_10558("animated_color");
/*     */             
/* 124 */             if (!rarityName.isEmpty()) {
/* 125 */               CustomRarity newRarity = new CustomRarity(class_2561.method_43471(rarityName).getString());
/*     */               
/* 127 */               if (!hexColor.isEmpty()) {
/* 128 */                 newRarity.setColor(TextColorHelper.getColorFromHEX(hexColor));
/*     */               }
/* 130 */               if (!animatedHexColor.isEmpty() && PrismCompat.isPrismLoaded()) {
/* 131 */                 class_5251 prismColor = PrismCompat.parseColor(animatedHexColor);
/* 132 */                 newRarity.setAnimatedColor(prismColor);
/* 133 */                 newRarity.setColor(prismColor.method_27716());
/*     */               } 
/*     */               
/* 136 */               return newRarity;
/*     */             } 
/* 138 */           } else if (!customRarityId.isEmpty() && RARITY_CACHE.containsKey(customRarityId)) {
/* 139 */             return RARITY_CACHE.get(customRarityId);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 145 */     CustomRarity rarity = fromSelectors(itemStack);
/* 146 */     if (rarity != null) {
/* 147 */       return rarity;
/*     */     }
/*     */     
/* 150 */     return null;
/*     */   }
/*     */   
/*     */   public static class_2561 toText(CustomRarity customRarity) {
/* 154 */     class_2583 newStyle = class_2583.field_24360.method_10978(Boolean.valueOf(false)).method_36139(customRarity.getColor());
/* 155 */     return (class_2561)class_2561.method_43470(customRarity.getName()).method_10862(newStyle);
/*     */   }
/*     */   
/*     */   public static void onResourcesReload(class_3300 resourceManager) {
/* 159 */     RARITY_CACHE.clear();
/* 160 */     class_3298 customRaritiesFile = resourceManager.method_14486(LootBeams.id("custom_rarities.json")).orElse(null);
/*     */     
/* 162 */     if (customRaritiesFile != null) {
/*     */       try {
/* 164 */         JsonObject customRaritiesJSON = class_3518.method_15255(customRaritiesFile.method_43039());
/*     */         
/* 166 */         if (customRaritiesJSON.has("custom_rarities")) {
/* 167 */           JsonArray customRarities = class_3518.method_15261(customRaritiesJSON, "custom_rarities");
/*     */           
/* 169 */           if (!customRarities.isEmpty()) {
/* 170 */             for (JsonElement jsonElement : customRarities) {
/* 171 */               if (jsonElement.isJsonObject()) {
/* 172 */                 JsonObject jsonObject = jsonElement.getAsJsonObject();
/* 173 */                 if (jsonObject.has("id")) {
/* 174 */                   String rarityId = class_3518.method_15265(jsonObject, "id");
/* 175 */                   String rarityName = class_3518.method_15265(jsonObject, "name");
/* 176 */                   String rarityColor = class_3518.method_15265(jsonObject, "color");
/* 177 */                   String rarityAnimatedColor = class_3518.method_15253(jsonObject, "animated_color", "");
/* 178 */                   JsonArray raritySelectors = class_3518.method_15292(jsonObject, "selectors", new JsonArray());
/* 179 */                   JsonObject rarityConfig = class_3518.method_15296(jsonObject, "config");
/*     */                   
/* 181 */                   if (!rarityId.isEmpty() && !rarityName.isEmpty()) {
/* 182 */                     CustomRarity newRarity = new CustomRarity(class_2561.method_43471(rarityName).getString());
/*     */                     
/* 184 */                     if (!rarityColor.isEmpty()) {
/* 185 */                       newRarity.setColor(TextColorHelper.getColorFromHEX(rarityColor));
/*     */                     }
/* 187 */                     if (!rarityAnimatedColor.isEmpty() && PrismCompat.isPrismLoaded()) {
/* 188 */                       class_5251 prismColor = PrismCompat.parseColor(rarityAnimatedColor);
/* 189 */                       newRarity.setAnimatedColor(prismColor);
/* 190 */                       newRarity.setColor(prismColor.method_27716());
/*     */                     } 
/* 192 */                     if (!raritySelectors.isEmpty()) {
/* 193 */                       List<String> selectors = new ArrayList<>();
/* 194 */                       for (JsonElement selectorElement : raritySelectors) {
/* 195 */                         selectors.add(class_3518.method_15287(selectorElement, "selector"));
/*     */                       }
/* 197 */                       if (!selectors.isEmpty()) {
/* 198 */                         newRarity.setSelectors(selectors);
/*     */                       }
/*     */                     } 
/* 201 */                     if (!rarityConfig.isEmpty()) {
/* 202 */                       newRarity.setConfig(rarityConfig);
/*     */                     }
/*     */                     
/* 205 */                     RARITY_CACHE.put(rarityId, newRarity);
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           }
/*     */         } 
/* 212 */       } catch (Exception ex) {
/* 213 */         ex.printStackTrace();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 220 */     return getClass().getName() + "[name=" + getClass().getName() + ", color=" + this.name + ", rarityConfig=" + this.color + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\features\CustomRarity.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */