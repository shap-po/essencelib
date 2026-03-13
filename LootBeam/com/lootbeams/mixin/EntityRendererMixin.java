/*     */ package com.lootbeams.mixin;
/*     */ 
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.features.CustomLootBeamsConfig;
/*     */ import com.lootbeams.helpers.ItemHelper;
/*     */ import com.lootbeams.helpers.RarityHelper;
/*     */ import com.lootbeams.helpers.ViewHelper;
/*     */ import com.lootbeams.managers.RenderManager;
/*     */ import com.lootbeams.render.BeamRender;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1533;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_4587;
/*     */ import net.minecraft.class_4597;
/*     */ import net.minecraft.class_4604;
/*     */ import net.minecraft.class_897;
/*     */ import net.minecraft.class_898;
/*     */ import org.spongepowered.asm.mixin.Final;
/*     */ import org.spongepowered.asm.mixin.Mixin;
/*     */ import org.spongepowered.asm.mixin.Shadow;
/*     */ import org.spongepowered.asm.mixin.Unique;
/*     */ import org.spongepowered.asm.mixin.injection.At;
/*     */ import org.spongepowered.asm.mixin.injection.Inject;
/*     */ import org.spongepowered.asm.mixin.injection.ModifyVariable;
/*     */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*     */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*     */ 
/*     */ 
/*     */ @Mixin({class_897.class})
/*     */ public abstract class EntityRendererMixin<T extends class_1297>
/*     */ {
/*     */   @Inject(method = {"render"}, at = {@At("HEAD")})
/*     */   private void attemptRenderBeams(class_1297 entity, float entityYaw, float partialTick, class_4587 poseStack, class_4597 buffer, int packedLight, CallbackInfo ci) {
/*  38 */     if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
/*  39 */       this.entity = itemEntity;
/*     */       
/*  41 */       Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemEntity.method_6983());
/*     */       
/*  43 */       float renderDistance = itemConfig.renderDistance;
/*  44 */       if ((class_310.method_1551()).field_1724 == null || (class_310.method_1551()).field_1724.method_5739((class_1297)itemEntity) > renderDistance) {
/*     */         return;
/*     */       }
/*     */       
/*  48 */       boolean shouldRender = (ViewHelper.shouldRenderOnItem(itemEntity.method_6983()) && itemEntity.method_24828());
/*     */       
/*  50 */       if (shouldRender)
/*  51 */         RenderManager.addRenderAfterWeather((stack, consumer) -> {
/*     */               class_4597.class_4598 consumerProvider = class_310.method_1551().method_22940().method_23000();
/*     */               stack.method_22903();
/*     */               stack.method_22904(itemEntity.method_19538().method_10216(), itemEntity.method_19538().method_10214(), itemEntity.method_19538().method_10215());
/*     */               BeamRender.render(stack, consumerProvider, itemEntity, itemEntity.method_37908().method_8510(), partialTick);
/*     */               stack.method_22909();
/*     */             });  }
/*     */   
/*     */   }
/*     */   @Shadow
/*     */   @Final
/*     */   protected class_898 field_4676; @Unique
/*     */   class_1542 entity;
/*     */   
/*     */   @Inject(method = {"shouldRender"}, at = {@At("HEAD")}, cancellable = true)
/*     */   private void modifyRenderDistance(T entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
/*  67 */     if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
/*  68 */       class_1799 itemStack = itemEntity.method_6983();
/*  69 */       Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
/*     */       
/*  71 */       double maxDistance = itemConfig.renderDistance;
/*  72 */       double distanceSquared = this.field_4676.method_23168((class_1297)entity);
/*     */       
/*  74 */       if (distanceSquared < maxDistance * maxDistance) {
/*  75 */         cir.setReturnValue(Boolean.valueOf(true));
/*     */       } }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   @Inject(at = {@At("HEAD")}, method = {"renderLabelIfPresent"}, cancellable = true)
/*     */   protected void renderLabelIfPresent(T entity, class_2561 text, class_4587 matrices, class_4597 vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
/*  83 */     if (entity instanceof class_1533) { class_1533 itemFrameEntity = (class_1533)entity;
/*  84 */       Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemFrameEntity.method_6940());
/*     */       
/*  86 */       if (itemConfig.advancedTooltips && itemConfig.itemFrameTooltips) {
/*  87 */         ci.cancel();
/*     */       } }
/*     */   
/*     */   }
/*     */   
/*     */   @ModifyVariable(at = @At("HEAD"), method = {"render"}, ordinal = 0, argsOnly = true)
/*     */   public int render(int light) {
/*  94 */     if (this.entity != null && (
/*  95 */       LootBeams.config.allItems || (LootBeams.config.onlyEquipment && 
/*  96 */       ItemHelper.isEquipmentItem(this.entity.method_6983())) || (LootBeams.config.onlyRare && 
/*  97 */       RarityHelper.rarityCheck(this.entity.method_6983(), false)) || 
/*  98 */       ItemHelper.isItemInRegistryList(LootBeams.config.whitelist, this.entity.method_6983().method_7909()))) {
/*  99 */       light = 15728640;
/*     */     }
/*     */ 
/*     */     
/* 103 */     return light;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\EntityRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */