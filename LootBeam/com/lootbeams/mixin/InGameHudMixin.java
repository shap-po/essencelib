/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.lootbeams.events.RenderEvents;
/*    */ import net.fabricmc.api.EnvType;
/*    */ import net.fabricmc.api.Environment;
/*    */ import net.minecraft.class_329;
/*    */ import net.minecraft.class_332;
/*    */ import net.minecraft.class_9779;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Environment(EnvType.CLIENT)
/*    */ @Mixin({class_329.class})
/*    */ public class InGameHudMixin
/*    */ {
/*    */   @Inject(method = {"render"}, at = {@At("TAIL")})
/*    */   public void render(class_332 drawContext, class_9779 tickCounter, CallbackInfo callbackInfo) {
/* 25 */     ((RenderEvents.HudRender)RenderEvents.HUD.invoker()).onHud(drawContext, tickCounter);
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\InGameHudMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */