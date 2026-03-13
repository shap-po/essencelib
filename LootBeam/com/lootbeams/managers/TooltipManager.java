/*     */ package com.lootbeams.managers;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.features.CustomLootBeamsConfig;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_327;
/*     */ import net.minecraft.class_437;
/*     */ import net.minecraft.class_5250;
/*     */ import net.minecraft.class_5348;
/*     */ import net.minecraft.class_638;
/*     */ 
/*     */ public class TooltipManager {
/*  18 */   public static final Map<class_1799, List<class_2561>> TOOLTIP_CACHE = new ConcurrentHashMap<>();
/*     */   
/*     */   public static void onResourcesReload() {
/*  21 */     TOOLTIP_CACHE.clear();
/*     */   }
/*     */   
/*     */   public static void onEntityLoad(class_1542 item, class_638 world) {
/*  25 */     class_1799 itemStack = item.method_6983();
/*  26 */     if (!TOOLTIP_CACHE.containsKey(itemStack)) {
/*  27 */       TOOLTIP_CACHE.put(itemStack, getItemStackTooltip(item.method_6983()));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void onEntityUnload(class_1542 item, class_638 world) {
/*  32 */     TOOLTIP_CACHE.remove(item.method_6983());
/*     */   }
/*     */   
/*     */   public static List<class_2561> getTooltipFromCache(class_1799 itemStack) {
/*  36 */     if (!TOOLTIP_CACHE.containsKey(itemStack)) {
/*  37 */       List<class_2561> tooltip = getItemStackTooltip(itemStack);
/*  38 */       TOOLTIP_CACHE.put(itemStack, tooltip);
/*  39 */       return tooltip;
/*     */     } 
/*     */     
/*  42 */     return TOOLTIP_CACHE.get(itemStack);
/*     */   }
/*     */   
/*     */   public static List<class_2561> getItemStackTooltip(class_1799 itemStack) {
/*  46 */     return class_437.method_25408(class_310.method_1551(), itemStack);
/*     */   }
/*     */   
/*     */   public static class_2561 getTooltipLongestLine(class_1799 itemStack, List<class_2561> tooltipLines) {
/*  50 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*  51 */     class_310 client = class_310.method_1551();
/*  52 */     class_327 textRenderer = client.field_1772;
/*  53 */     class_2561 longestLine = tooltipLines.get(0);
/*  54 */     if (itemConfig.screenTooltipsRequireCrouch && client.field_1724.method_18276())
/*     */     {
/*  56 */       longestLine = tooltipLines.stream().max((a, b) -> textRenderer.method_27525((class_5348)a) - textRenderer.method_27525((class_5348)b)).orElse(tooltipLines.get(0));
/*     */     }
/*     */     
/*  59 */     return longestLine;
/*     */   }
/*     */   
/*     */   public static List<class_2561> getTooltipWithStackSize(class_1799 itemStack) {
/*  63 */     List<class_2561> tooltipLines = getTooltipFromCache(itemStack);
/*  64 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*  65 */     int count = itemStack.method_7947();
/*     */     
/*  67 */     if (itemConfig.renderStackcount && count > 1) {
/*  68 */       String countString = " x" + count;
/*  69 */       class_2561 firstLine = tooltipLines.get(0);
/*  70 */       String itemNameString = firstLine.getString();
/*     */       
/*  72 */       if (!itemNameString.contains(countString)) {
/*     */         
/*  74 */         class_5250 class_5250 = class_2561.method_43470(itemNameString + itemNameString).method_10862(firstLine.method_10866());
/*     */         
/*  76 */         tooltipLines.set(0, class_5250);
/*     */       } 
/*     */     } 
/*     */     
/*  80 */     return tooltipLines;
/*     */   }
/*     */   
/*     */   public static boolean canRenderTooltips(class_1799 itemStack) {
/*  84 */     class_310 client = class_310.method_1551();
/*  85 */     boolean isChatScreen = client.field_1755 instanceof net.minecraft.class_408;
/*  86 */     boolean inGame = (client.field_1755 == null);
/*  87 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*     */     
/*  89 */     return (itemConfig.advancedTooltips && (inGame || isChatScreen));
/*     */   }
/*     */   
/*     */   public static class OffsetContainer {
/*     */     private final int offset;
/*     */     private final int halfTooltipWidth;
/*     */     
/*     */     public OffsetContainer(int offset, int halfTooltipWidth) {
/*  97 */       this.offset = offset;
/*  98 */       this.halfTooltipWidth = halfTooltipWidth;
/*     */     }
/*     */     
/*     */     public int getOffset() {
/* 102 */       return this.offset;
/*     */     }
/*     */     
/*     */     public int getHalfTooltipWidth() {
/* 106 */       return this.halfTooltipWidth;
/*     */     }
/*     */     
/*     */     public int getTooltipWidth() {
/* 110 */       return this.halfTooltipWidth * 2;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\managers\TooltipManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */