/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.lootbeams.managers.TooltipManager;
/*    */ import com.lootbeams.renderers.TooltipRenderer;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_327;
/*    */ import net.minecraft.class_332;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Mixin({class_332.class})
/*    */ public class DrawContextMixin
/*    */ {
/*    */   @Inject(method = {"drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"}, at = {@At("HEAD")}, cancellable = true)
/*    */   protected void drawItemTooltipHandler(class_327 font, class_1799 itemStack, int x, int y, CallbackInfo ci) {
/* 22 */     if (!TooltipManager.canRenderTooltips(itemStack)) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 27 */     class_332 ctx = (class_332)this;
/*    */     
/* 29 */     TooltipRenderer.renderItemTooltip(ctx, font, itemStack, x, y);
/* 30 */     ci.cancel();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\DrawContextMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */