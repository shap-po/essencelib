/*    */ package com.lootbeams.renderers;
/*    */ import com.lootbeams.config.Configuration;
/*    */ import com.lootbeams.features.CustomLootBeamsConfig;
/*    */ import com.lootbeams.helpers.ViewHelper;
/*    */ import net.minecraft.class_1297;
/*    */ import net.minecraft.class_1533;
/*    */ import net.minecraft.class_1542;
/*    */ import net.minecraft.class_1657;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_239;
/*    */ import net.minecraft.class_310;
/*    */ import net.minecraft.class_332;
/*    */ import net.minecraft.class_3966;
/*    */ import net.minecraft.class_746;
/*    */ import net.minecraft.class_9334;
/*    */ import net.minecraft.class_9779;
/*    */ 
/*    */ public class HudRenderer {
/*    */   public static void onHudRender(class_332 drawContext, class_9779 tickCounter) {
/* 20 */     float tickDelta = tickCounter.method_60637(false);
/* 21 */     class_310 client = class_310.method_1551();
/*    */     
/* 23 */     if (ViewHelper.shouldRenderCrosshair(client)) {
/* 24 */       if (client.field_1765.method_17783() == class_239.class_240.field_1331) {
/* 25 */         class_1297 entity = ((class_3966)client.field_1765).method_17782();
/* 26 */         if (entity instanceof class_1533) { class_1533 itemFrameEntity = (class_1533)entity;
/* 27 */           class_1799 itemStack = itemFrameEntity.method_6940();
/* 28 */           Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*    */           
/* 30 */           boolean hasLabel = (!itemStack.method_7960() && itemStack.method_57826(class_9334.field_49631));
/*    */           
/* 32 */           if (!hasLabel || !itemConfig.itemFrameTooltips)
/*    */             return; 
/* 34 */           TooltipRenderer.renderWorldPositionTooltip(drawContext, entity, itemStack, tickDelta);
/*    */ 
/*    */           
/*    */           return; }
/*    */       
/*    */       } 
/*    */       
/* 41 */       class_746 class_746 = client.field_1724;
/* 42 */       class_239 result = TargetHelper.getEntityItem((class_1657)class_746);
/*    */       
/* 44 */       if (result != null && result.method_17783() == class_239.class_240.field_1331) {
/* 45 */         class_1297 entity = ((class_3966)result).method_17782();
/*    */         
/* 47 */         if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
/* 48 */           class_1799 itemStack = itemEntity.method_6983();
/* 49 */           Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*    */           
/* 51 */           boolean shouldRender = ViewHelper.shouldRenderOnItem(itemStack);
/* 52 */           if (!shouldRender || (itemConfig.requireOnGround && !entity.method_24828()))
/*    */             return; 
/* 54 */           TooltipRenderer.renderWorldPositionTooltip(drawContext, entity, itemStack, tickDelta); }
/*    */       
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\renderers\HudRenderer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */