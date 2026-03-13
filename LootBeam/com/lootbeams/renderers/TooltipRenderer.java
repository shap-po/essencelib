/*     */ package com.lootbeams.renderers;
/*     */ import com.lootbeams.compat.iceberg.IcebergCompat;
/*     */ import com.lootbeams.compat.legendarytooltips.LegendaryTooltipsCompat;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.features.CustomLootBeamsConfig;
/*     */ import com.lootbeams.features.CustomRarity;
/*     */ import com.lootbeams.helpers.RarityHelper;
/*     */ import com.lootbeams.helpers.ViewHelper;
/*     */ import com.lootbeams.managers.TooltipManager;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_327;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_3532;
/*     */ import net.minecraft.class_5250;
/*     */ import net.minecraft.class_5348;
/*     */ import net.minecraft.class_5632;
/*     */ import net.minecraft.class_5684;
/*     */ import net.minecraft.class_746;
/*     */ import net.minecraft.class_768;
/*     */ import org.joml.Vector3f;
/*     */ 
/*     */ public class TooltipRenderer {
/*     */   public static void renderItemTooltip(class_332 context, class_327 font, class_1799 itemStack, int x, int y) {
/*  32 */     class_310 client = class_310.method_1551();
/*  33 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*     */     
/*  35 */     class_746 class_746 = client.field_1724;
/*  36 */     class_327 textRenderer = client.field_1772;
/*  37 */     CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
/*  38 */     List<class_2561> tooltipLines = TooltipManager.getTooltipWithStackSize(itemStack);
/*  39 */     Optional<class_5632> tooltipData = itemStack.method_32347();
/*  40 */     class_2561 longestLine = TooltipManager.getTooltipLongestLine(itemStack, tooltipLines);
/*  41 */     int halfWidth = textRenderer.method_27525((class_5348)longestLine) / 2;
/*  42 */     boolean alwaysRenderRarityOnItem = RarityHelper.alwaysHasRarity(itemStack);
/*  43 */     boolean legendaryTooltipsLoaded = LegendaryTooltipsCompat.isLegendaryTooltipsLoaded();
/*     */     
/*  45 */     x -= 12;
/*     */     
/*  47 */     if (!itemConfig.screenTooltipsRequireCrouch || class_746.method_18276()) {
/*  48 */       TooltipManager.OffsetContainer tooltipOffset = alignTooltipHorizontal(itemStack, tooltipLines, tooltipData, context, x, y, textRenderer, halfWidth);
/*  49 */       x -= tooltipOffset.getOffset();
/*     */       
/*  51 */       context.method_51437(textRenderer, tooltipLines, tooltipData, x, y);
/*     */     } else {
/*  53 */       class_5250 class_5250; String rarity = RarityHelper.getRarity(itemStack);
/*  54 */       class_2583 textStyle = itemStack.method_7954().method_10866();
/*  55 */       class_2561 t1 = tooltipLines.get(0);
/*  56 */       class_2561 t2 = null;
/*  57 */       tooltipLines = List.of(t1);
/*     */       
/*  59 */       if (itemConfig.renderItemRarity || alwaysRenderRarityOnItem) {
/*  60 */         class_5250 = class_2561.method_43470(rarity).method_10862(textStyle);
/*  61 */         tooltipLines = List.of(t1, class_5250);
/*     */       } 
/*     */       
/*  64 */       TooltipManager.OffsetContainer tooltipOffset = alignTooltipHorizontal(itemStack, tooltipLines, tooltipData, context, x, y, textRenderer, halfWidth);
/*  65 */       halfWidth = tooltipOffset.getHalfTooltipWidth();
/*     */       
/*  67 */       if ((itemConfig.renderItemRarity || alwaysRenderRarityOnItem) && class_5250 != null) {
/*  68 */         class_2561 class_2561; if (customRarity != null) {
/*  69 */           class_2561 = CustomRarity.toText(customRarity);
/*  70 */           tooltipLines = List.of(t1, class_2561);
/*     */         } 
/*     */         
/*  73 */         if (legendaryTooltipsLoaded && textRenderer.method_27525((class_5348)class_2561) < halfWidth * 2) {
/*  74 */           tooltipLines = List.of(t1, TextHelper.centeredLine(class_2561, textRenderer, halfWidth * 2));
/*     */         }
/*     */       } 
/*     */       
/*  78 */       tooltipOffset = alignTooltipHorizontal(itemStack, tooltipLines, tooltipData, context, x, y, textRenderer, halfWidth);
/*  79 */       halfWidth = tooltipOffset.getHalfTooltipWidth();
/*  80 */       x -= tooltipOffset.getOffset();
/*     */       
/*  82 */       if ((!itemConfig.renderItemRarity && !alwaysRenderRarityOnItem) || itemConfig.combineNameAndRarity || legendaryTooltipsLoaded) {
/*  83 */         context.method_51437(textRenderer, tooltipLines, tooltipData, x, y);
/*     */       } else {
/*  85 */         int rarityLineHalfWidth = textRenderer.method_1727(rarity) / 2;
/*  86 */         context.method_51437(textRenderer, List.of(tooltipLines.get(0)), tooltipData, x, y);
/*  87 */         Objects.requireNonNull(textRenderer); context.method_51437(textRenderer, List.of(tooltipLines.get(1)), tooltipData, x + halfWidth - rarityLineHalfWidth, y + 9 * 2);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void renderWorldPositionTooltip(class_332 drawContext, class_1297 entity, class_1799 itemStack, float tickDelta) {
/*  93 */     if (itemStack != null && !itemStack.method_7960()) {
/*  94 */       if (!TooltipManager.canRenderTooltips(itemStack)) {
/*     */         return;
/*     */       }
/*     */       
/*  98 */       class_310 client = class_310.method_1551();
/*  99 */       class_746 class_746 = client.field_1724;
/* 100 */       Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*     */       
/* 102 */       int windowScaledWidth = drawContext.method_51421();
/* 103 */       int windowScaledHeight = drawContext.method_51443();
/* 104 */       int x = windowScaledWidth / 2;
/* 105 */       int y = windowScaledHeight / 2;
/*     */       
/* 107 */       class_327 textRenderer = client.field_1772;
/* 108 */       List<class_2561> tooltipLines = TooltipManager.getTooltipWithStackSize(itemStack);
/*     */       
/* 110 */       if (itemConfig.worldspaceTooltips) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 115 */         class_243 tooltipShiftedPosition = new class_243(0.0D, Math.min(1.0D, class_746.method_5858(entity) * 0.025D) + itemConfig.nametagYOffset + (tooltipLines.size() / 100.0F), 0.0D);
/*     */ 
/*     */         
/* 118 */         class_243 tooltipWorldPos = entity.method_19538().method_1019(tooltipShiftedPosition);
/* 119 */         Vector3f desiredScreenSpacePos = ViewHelper.worldToScreenSpace(tooltipWorldPos, tickDelta);
/*     */ 
/*     */         
/* 122 */         Objects.requireNonNull(textRenderer);
/* 123 */         desiredScreenSpacePos = new Vector3f(class_3532.method_15363(desiredScreenSpacePos.x(), 0.0F, windowScaledWidth), class_3532.method_15363(desiredScreenSpacePos.y(), 0.0F, (windowScaledHeight - 9 * tooltipLines.size())), desiredScreenSpacePos.z());
/*     */         
/* 125 */         x = (int)desiredScreenSpacePos.x();
/* 126 */         y = (int)desiredScreenSpacePos.y();
/*     */       } 
/*     */       
/* 129 */       int guiScale = ((Integer)client.field_1690.method_42474().method_41753()).intValue();
/* 130 */       drawContext.method_51446(textRenderer, itemStack, x, y);
/* 131 */       client.field_1690.method_42474().method_41748(Integer.valueOf(guiScale));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static TooltipManager.OffsetContainer alignTooltipHorizontal(class_1799 itemStack, List<class_2561> tooltipLines, Optional<class_5632> tooltipData, class_332 context, int x, int y, class_327 textRenderer, int halfWidth) {
/* 136 */     boolean legendaryTooltipsLoaded = LegendaryTooltipsCompat.isLegendaryTooltipsLoaded();
/* 137 */     if (legendaryTooltipsLoaded) {
/* 138 */       List<class_5684> tooltipComponents = IcebergCompat.getTooltipComponents(itemStack, tooltipLines, tooltipData, x, context
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 143 */           .method_51421(), context
/* 144 */           .method_51443(), null, textRenderer, context
/*     */ 
/*     */           
/* 147 */           .method_51421());
/*     */       
/* 149 */       class_768 tooltipRect = IcebergCompat.getTooltipRect(itemStack, context, class_8001.field_41687, tooltipComponents, x, y, context
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 156 */           .method_51421(), context
/* 157 */           .method_51443(), context
/* 158 */           .method_51421(), textRenderer, 0, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 164 */       int halfTooltipWidth = tooltipRect.method_3319() / 2;
/* 165 */       int offset = halfTooltipWidth - 1;
/*     */       
/* 167 */       return new TooltipManager.OffsetContainer(offset, halfTooltipWidth);
/*     */     } 
/*     */ 
/*     */     
/* 171 */     return new TooltipManager.OffsetContainer(halfWidth, halfWidth);
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\renderers\TooltipRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */