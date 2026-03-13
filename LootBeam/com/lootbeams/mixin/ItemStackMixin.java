/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.lootbeams.config.Configuration;
/*    */ import com.lootbeams.features.CustomLootBeamsConfig;
/*    */ import com.lootbeams.features.CustomRarity;
/*    */ import java.util.List;
/*    */ import net.minecraft.class_1657;
/*    */ import net.minecraft.class_1792;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_1836;
/*    */ import net.minecraft.class_2561;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Unique;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({class_1799.class})
/*    */ public class ItemStackMixin {
/*    */   @Unique
/*    */   private boolean isTooltipModificationActive = false;
/*    */   
/*    */   @Inject(method = {"getTooltip"}, at = {@At("RETURN")}, cancellable = true)
/*    */   private void modifyTooltip(class_1792.class_9635 context, @Nullable class_1657 player, class_1836 type, CallbackInfoReturnable<List<class_2561>> cir) {
/* 26 */     class_1799 itemStack = (class_1799)this;
/*    */     
/* 28 */     List<class_2561> tooltip = (List<class_2561>)cir.getReturnValue();
/*    */     
/* 30 */     if (!this.isTooltipModificationActive) {
/* 31 */       this.isTooltipModificationActive = true;
/*    */       try {
/* 33 */         CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
/* 34 */         Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*    */         
/* 36 */         if (itemConfig.renderItemRarity && itemConfig.renderItemRarityInTooltip && customRarity != null) {
/* 37 */           tooltip.add(CustomRarity.toText(customRarity));
/*    */         }
/*    */       } finally {
/* 40 */         this.isTooltipModificationActive = false;
/*    */       } 
/*    */     } 
/*    */     
/* 44 */     cir.setReturnValue(tooltip);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\ItemStackMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */