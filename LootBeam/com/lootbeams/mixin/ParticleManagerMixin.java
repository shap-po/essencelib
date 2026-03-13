/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.lootbeams.vfx.VFXParticle;
/*    */ import com.mojang.blaze3d.systems.RenderSystem;
/*    */ import java.util.Map;
/*    */ import java.util.Queue;
/*    */ import net.minecraft.class_1060;
/*    */ import net.minecraft.class_128;
/*    */ import net.minecraft.class_129;
/*    */ import net.minecraft.class_148;
/*    */ import net.minecraft.class_286;
/*    */ import net.minecraft.class_287;
/*    */ import net.minecraft.class_289;
/*    */ import net.minecraft.class_3999;
/*    */ import net.minecraft.class_4184;
/*    */ import net.minecraft.class_4588;
/*    */ import net.minecraft.class_702;
/*    */ import net.minecraft.class_703;
/*    */ import net.minecraft.class_765;
/*    */ import net.minecraft.class_9801;
/*    */ import org.spongepowered.asm.mixin.Final;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({class_702.class})
/*    */ public class ParticleManagerMixin implements LootbeamsParticleManager {
/*    */   @Unique
/*    */   public boolean customParticlesRender = false;
/*    */   
/* 33 */   public void renderCustomParticles(class_765 lightmapTextureManager, class_4184 camera, float tickDelta) { class_702 particleManager = (class_702)this;
/* 34 */     this.customParticlesRender = true;
/* 35 */     particleManager.method_3049(lightmapTextureManager, camera, tickDelta);
/* 36 */     this.customParticlesRender = false; } @Shadow
/*    */   @Final
/*    */   private Map<class_3999, Queue<class_703>> field_3830; @Shadow
/*    */   @Final
/*    */   public class_1060 field_3831; @Inject(method = {"renderParticles"}, at = {@At("HEAD")}, cancellable = true)
/* 41 */   private void renderLootBeamParticles(class_765 lightmapTextureManager, class_4184 camera, float tickDelta, CallbackInfo ci) { if (this.customParticlesRender) {
/* 42 */       ci.cancel();
/*    */       
/* 44 */       Queue<class_703> queue = this.field_3830.get(VFXParticle.RENDER_TYPE);
/*    */       
/* 46 */       if (queue != null && !queue.isEmpty()) {
/* 47 */         lightmapTextureManager.method_3316();
/* 48 */         RenderSystem.enableDepthTest();
/*    */         
/* 50 */         class_289 tessellator = class_289.method_1348();
/* 51 */         class_287 bufferBuilder = VFXParticle.RENDER_TYPE.method_18130(tessellator, this.field_3831);
/* 52 */         if (bufferBuilder != null) {
/* 53 */           for (class_703 particle : queue) {
/*    */             try {
/* 55 */               particle.method_3074((class_4588)bufferBuilder, camera, tickDelta);
/* 56 */             } catch (Throwable ex) {
/* 57 */               class_128 crashReport = class_128.method_560(ex, "Rendering Particle");
/* 58 */               class_129 crashReportSection = crashReport.method_562("Particle being rendered");
/* 59 */               Objects.requireNonNull(particle); crashReportSection.method_577("Particle", particle::toString);
/* 60 */               throw new class_148(crashReport);
/*    */             } 
/*    */           } 
/*    */           
/* 64 */           class_9801 builtBuffer = bufferBuilder.method_60794();
/* 65 */           if (builtBuffer != null) {
/* 66 */             class_286.method_43433(builtBuffer);
/*    */           }
/*    */         } 
/*    */         
/* 70 */         VFXParticle.RENDER_TYPE.end(tessellator);
/* 71 */         lightmapTextureManager.method_3315();
/* 72 */         RenderSystem.disableDepthTest();
/*    */       } 
/*    */     }  }
/*    */ 
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\ParticleManagerMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */