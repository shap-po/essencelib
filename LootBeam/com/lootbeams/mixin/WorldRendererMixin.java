/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
/*    */ import com.lootbeams.contexts.WorldRendererContext;
/*    */ import com.lootbeams.events.RenderEvents;
/*    */ import net.minecraft.class_279;
/*    */ import net.minecraft.class_4184;
/*    */ import net.minecraft.class_4587;
/*    */ import net.minecraft.class_4599;
/*    */ import net.minecraft.class_638;
/*    */ import net.minecraft.class_757;
/*    */ import net.minecraft.class_761;
/*    */ import net.minecraft.class_765;
/*    */ import net.minecraft.class_9779;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.joml.Matrix4f;
/*    */ import org.spongepowered.asm.mixin.Final;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.Unique;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({class_761.class})
/*    */ public class WorldRendererMixin {
/*    */   @Unique
/* 28 */   private WorldRendererContext context = new WorldRendererContext(); @Final
/*    */   @Shadow
/*    */   private class_4599 field_20951; @Shadow
/*    */   private class_638 field_4085; @Shadow
/*    */   @Nullable
/*    */   private class_279 field_25279;
/*    */   @Inject(method = {"render"}, at = {@At("HEAD")})
/*    */   private void beforeRender(class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, class_757 gameRenderer, class_765 lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
/* 36 */     this.context.prepare((class_761)this, tickCounter, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, projectionMatrix, positionMatrix, this.field_20951.method_23000(), this.field_4085.method_16107(), (this.field_25279 != null), this.field_4085);
/*    */   }
/*    */   
/*    */   @ModifyExpressionValue(method = {"render"}, at = {@At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack")})
/*    */   private class_4587 setMatrixStack(class_4587 matrixStack) {
/* 41 */     this.context.setMatrixStack(matrixStack);
/* 42 */     return matrixStack;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V")})
/*    */   private void beforeParticles(CallbackInfo ci) {
/* 53 */     ((RenderEvents.BeforeParticles)RenderEvents.BEFORE_PARTICLES.invoker()).beforeParticles(this.context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getCloudRenderModeValue()Lnet/minecraft/client/option/CloudRenderMode;")})
/*    */   private void afterTranslucent(CallbackInfo ci) {
/* 64 */     ((RenderEvents.AfterTranslucent)RenderEvents.AFTER_TRANSLUCENT.invoker()).afterTranslucent(this.context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V", shift = At.Shift.AFTER)})
/*    */   private void renderAfterWeather(CallbackInfo ci) {
/* 77 */     ((RenderEvents.AfterWeather)RenderEvents.AFTER_WEATHER.invoker()).afterWeather(this.context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/Camera;)V", shift = At.Shift.AFTER)})
/*    */   private void renderBeforeEnd(CallbackInfo ci) {
/* 89 */     ((RenderEvents.BeforeEnd)RenderEvents.BEFORE_END.invoker()).beforeEnd(this.context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"render"}, at = {@At("RETURN")})
/*    */   private void afterRender(CallbackInfo ci) {
/* 97 */     ((RenderEvents.End)RenderEvents.END.invoker()).onEnd(this.context);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\WorldRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */