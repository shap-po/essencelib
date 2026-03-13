/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.lootbeams.config.Configuration;
/*    */ import com.lootbeams.contexts.WorldRendererContext;
/*    */ import com.lootbeams.features.CustomLootBeamsConfig;
/*    */ import com.lootbeams.helpers.TextColorHelper;
/*    */ import com.lootbeams.managers.ItemEntityManager;
/*    */ import com.lootbeams.managers.RenderManager;
/*    */ import net.minecraft.class_1087;
/*    */ import net.minecraft.class_1297;
/*    */ import net.minecraft.class_1542;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_239;
/*    */ import net.minecraft.class_310;
/*    */ import net.minecraft.class_3959;
/*    */ import net.minecraft.class_3965;
/*    */ import net.minecraft.class_4587;
/*    */ import net.minecraft.class_4597;
/*    */ import net.minecraft.class_4618;
/*    */ import net.minecraft.class_5251;
/*    */ import net.minecraft.class_746;
/*    */ import net.minecraft.class_811;
/*    */ import net.minecraft.class_918;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({class_918.class})
/*    */ public class ItemRendererMixin {
/*    */   @Inject(method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void onRenderItem(class_1799 itemStack, class_811 transformationMode, boolean leftHanded, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay, class_1087 model, CallbackInfo ci) {
/* 33 */     Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*    */     
/* 35 */     if (!itemConfig.itemsGlow) {
/*    */       return;
/*    */     }
/*    */     
/* 39 */     class_1542 itemEntity = ItemEntityManager.getEntityForStack(itemStack);
/* 40 */     if (RenderManager.isRendering(itemEntity)) {
/*    */       return;
/*    */     }
/* 43 */     RenderManager.setRendering(itemEntity, true);
/*    */     
/* 45 */     class_310 client = class_310.method_1551();
/* 46 */     class_746 class_746 = client.field_1724;
/* 47 */     class_918 itemRenderer = client.method_1480();
/*    */     
/* 49 */     if (itemEntity != null && itemConfig.itemsGlow) {
/* 50 */       class_3965 class_3965 = client.field_1687.method_17742(new class_3959(class_746
/* 51 */             .method_5836(1.0F), itemEntity
/* 52 */             .method_19538(), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)class_746));
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 57 */       if (itemStack.method_7960() || class_3965.method_17783() == class_239.class_240.field_1332 || class_746
/* 58 */         .method_5858((class_1297)itemEntity) > itemConfig.renderDistance) {
/* 59 */         itemRenderer.method_23179(itemStack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
/*    */       } else {
/* 61 */         class_4618 outlineProvider = client.method_22940().method_23003();
/* 62 */         class_5251 textColor = TextColorHelper.getItemColor(itemStack);
/* 63 */         if (textColor != null) {
/* 64 */           int color = textColor.method_27716();
/* 65 */           int r = color >> 16 & 0xFF;
/* 66 */           int g = color >> 8 & 0xFF;
/* 67 */           int b = color & 0xFF;
/* 68 */           outlineProvider.method_23286(r, g, b, 255);
/*    */         } 
/* 70 */         itemRenderer.method_23179(itemStack, transformationMode, leftHanded, matrices, (class_4597)outlineProvider, light, overlay, model);
/* 71 */         RenderManager.addOnRenderEnd(getClass(), ctx -> {
/*    */               if (itemConfig.itemsGlow) {
/*    */                 (class_310.method_1551()).field_1769.field_20951.method_23003().method_23285();
/*    */                 
/*    */                 if ((class_310.method_1551()).field_1769.field_4059 != null) {
/*    */                   (class_310.method_1551()).field_1769.field_4059.method_1258((float)class_310.method_1551().method_47600());
/*    */                 }
/*    */                 class_310.method_1551().method_1522().method_1235(false);
/*    */               } 
/*    */             });
/*    */       } 
/* 82 */       RenderManager.setRendering(itemEntity, false);
/* 83 */       ci.cancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\ItemRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */