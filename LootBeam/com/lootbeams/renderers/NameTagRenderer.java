/*     */ package com.lootbeams.renderers;
/*     */ 
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.helpers.RarityHelper;
/*     */ import com.lootbeams.helpers.TargetHelper;
/*     */ import com.lootbeams.helpers.TextColorHelper;
/*     */ import com.lootbeams.managers.TooltipManager;
/*     */ import java.awt.Color;
/*     */ import java.util.List;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_327;
/*     */ import net.minecraft.class_3544;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_5251;
/*     */ import org.joml.Quaternionf;
/*     */ 
/*     */ public class NameTagRenderer {
/*     */   public static void renderNameTags(class_4597.class_4598 buffer, class_4587 matrixStack, class_1542 itemEntity, Configuration itemConfig, class_5251 color, float fadeAlpha, float currentGroundTime, long worldtime, float pticks) {
/*  27 */     if (!itemConfig.renderNametags) {
/*     */       return;
/*     */     }
/*     */     
/*  31 */     if (itemConfig.advancedTooltips) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  36 */     if ((class_310.method_1551()).field_1724.method_18276() || (itemConfig.renderNametagsOnlook && TargetHelper.isLookingAt((class_1657)(class_310.method_1551()).field_1724, (class_1297)itemEntity, itemConfig.nametagLookSensitivity))) {
/*     */       
/*  38 */       class_1799 itemStack = itemEntity.method_6983();
/*  39 */       float foregroundAlpha = itemConfig.nametagTextAlpha;
/*  40 */       float backgroundAlpha = itemConfig.nametagBackgroundAlpha;
/*  41 */       double yOffset = itemConfig.nametagYOffset;
/*  42 */       int foregroundColor = color.method_27716() & 0xFFFFFF | (int)(255.0F * foregroundAlpha) << 24;
/*  43 */       int backgroundColor = color.method_27716() & 0xFFFFFF | (int)(255.0F * backgroundAlpha) << 24;
/*     */       
/*  45 */       matrixStack.method_22903();
/*     */ 
/*     */       
/*  48 */       matrixStack.method_22904(0.0D, Math.min(1.0D, (class_310.method_1551()).field_1724.method_5858((class_1297)itemEntity) * 0.025D) + yOffset, 0.0D);
/*     */       
/*  50 */       Quaternionf entityRotationQuaternion = class_310.method_1551().method_1561().method_24197();
/*     */       
/*  52 */       matrixStack.method_22907(entityRotationQuaternion);
/*  53 */       matrixStack.method_22907(new Quaternionf(0.0D, Math.toRadians(90.0D), 0.0D, 0.0D));
/*     */       
/*  55 */       float nametagScale = itemConfig.nametagScale;
/*  56 */       float nametagScaleCompensation = 0.25F;
/*     */       
/*  58 */       matrixStack.method_22905(-0.02F * nametagScale * nametagScaleCompensation, -0.02F * nametagScale * nametagScaleCompensation, 0.02F * nametagScale * nametagScaleCompensation);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  65 */       class_327 fontrenderer = (class_310.method_1551()).field_1772;
/*  66 */       String itemName = class_3544.method_15440(itemStack.method_7964().getString());
/*  67 */       if (itemConfig.renderStackcount) {
/*  68 */         int count = itemStack.method_7947();
/*  69 */         if (count > 1) {
/*  70 */           itemName = itemName + " x" + itemName;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  75 */       matrixStack.method_46416(0.0F, 0.0F, -10.0F);
/*  76 */       renderText(itemStack, itemConfig, fontrenderer, matrixStack, (class_4597)buffer, itemName, foregroundColor, backgroundColor, backgroundAlpha);
/*  77 */       buffer.method_22993();
/*     */       
/*  79 */       boolean alwaysRenderRarityOnItem = RarityHelper.alwaysHasRarity(itemStack);
/*  80 */       if (itemConfig.renderItemRarity || alwaysRenderRarityOnItem) {
/*  81 */         renderRarity(itemStack, itemConfig, foregroundAlpha, backgroundAlpha, fontrenderer, matrixStack, (class_4597)buffer, alwaysRenderRarityOnItem);
/*  82 */         buffer.method_22993();
/*     */       } 
/*     */       
/*  85 */       matrixStack.method_22909();
/*     */     } 
/*     */   }
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
/*     */   private static void renderText(class_1799 itemStack, Configuration itemConfig, class_327 fontRenderer, class_4587 stack, class_4597 buffer, String text, int foregroundColor, int backgroundColor, float backgroundAlpha) {
/* 100 */     if (itemConfig.borders) {
/* 101 */       float w = -fontRenderer.method_1727(text) / 2.0F;
/* 102 */       int bg = (new Color(0, 0, 0, (int)(255.0F * backgroundAlpha))).getRGB();
/*     */ 
/*     */       
/* 105 */       class_2561 orderedText = class_2561.method_30163(text);
/* 106 */       fontRenderer.method_37296(orderedText
/* 107 */           .method_30937(), w, 0.0F, foregroundColor, bg, stack
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 112 */           .method_23760().method_23761(), buffer, 15728880);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 117 */       fontRenderer.method_27521(text, (float)(-fontRenderer.method_1727(text) / 2.0D), 0.0F, foregroundColor, false, stack.method_23760().method_23761(), buffer, class_327.class_6415.field_33993, backgroundColor, 15728864);
/*     */     } 
/*     */   }
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
/*     */   private static void renderRarity(class_1799 itemStack, Configuration itemConfig, float foregroundAlpha, float backgroundAlpha, class_327 fontRenderer, class_4587 stack, class_4597 buffer, boolean alwaysRenderRarityOnItem) {
/* 131 */     stack.method_22904(0.0D, 10.0D, 0.0D);
/* 132 */     stack.method_22905(0.75F, 0.75F, 0.75F);
/*     */     
/* 134 */     List<class_2561> tooltip = TooltipManager.getTooltipFromCache(itemStack);
/*     */     
/* 136 */     if (tooltip.isEmpty()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 141 */     if (tooltip.size() > 1) {
/* 142 */       class_2561 tooltipRarity = tooltip.get(1);
/* 143 */       String rarityString = tooltipRarity.getString();
/*     */       
/* 145 */       if (itemConfig.customRarities.contains(rarityString) || alwaysRenderRarityOnItem) {
/* 146 */         class_5251 rarityColor = LootBeams.config.whiteRarities ? class_5251.method_27718(class_124.field_1068) : TextColorHelper.getRawColor(tooltipRarity);
/* 147 */         int foregroundColor = rarityColor.method_27716() & 0xFFFFFF | (int)(255.0F * foregroundAlpha) << 24;
/* 148 */         int backgroundColor = rarityColor.method_27716() & 0xFFFFFF | (int)(255.0F * backgroundAlpha) << 24;
/* 149 */         renderText(itemStack, itemConfig, fontRenderer, stack, buffer, rarityString, foregroundColor, backgroundColor, backgroundAlpha);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 154 */     if (itemConfig.vanillaRarities || alwaysRenderRarityOnItem) {
/* 155 */       String rarity = RarityHelper.getRarity(itemStack);
/* 156 */       class_5251 rarityColor = LootBeams.config.whiteRarities ? class_5251.method_27718(class_124.field_1068) : TextColorHelper.getItemColor(itemStack);
/* 157 */       float R = (rarityColor.method_27716() >> 16 & 0xFF) / 255.0F;
/* 158 */       float G = (rarityColor.method_27716() >> 8 & 0xFF) / 255.0F;
/* 159 */       float B = (rarityColor.method_27716() & 0xFF) / 255.0F;
/* 160 */       int foregroundColor = (new Color(R, G, B, (int)foregroundAlpha)).getRGB();
/* 161 */       int backgroundColor = (new Color(R, G, B, (int)backgroundAlpha)).getRGB();
/* 162 */       renderText(itemStack, itemConfig, fontRenderer, stack, buffer, rarity, foregroundColor, backgroundColor, backgroundAlpha);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\renderers\NameTagRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */