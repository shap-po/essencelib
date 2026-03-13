/*    */ package com.lootbeams.vfx;
/*    */ 
/*    */ import com.lootbeams.managers.ParticleManager;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_2396;
/*    */ import net.minecraft.class_2400;
/*    */ import net.minecraft.class_243;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VFXParticleType
/*    */   extends class_2400
/*    */ {
/*    */   public ParticleManager.ParticleTexture texture;
/*    */   public float red;
/*    */   public float green;
/*    */   public float blue;
/*    */   public float alpha;
/*    */   public int lifetime;
/*    */   
/*    */   public VFXParticleType(boolean alwaysShow) {
/* 23 */     super(alwaysShow);
/*    */   }
/*    */   public float size; public class_243 sourcePos; public float gravity; public boolean collision; public boolean fullbright; public class_1799 itemStack;
/*    */   public VFXParticleType setTexture(ParticleManager.ParticleTexture texture) {
/* 27 */     this.texture = texture;
/* 28 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setColor(float red, float green, float blue, float alpha) {
/* 32 */     this.red = red;
/* 33 */     this.green = green;
/* 34 */     this.blue = blue;
/* 35 */     this.alpha = alpha;
/* 36 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setLifetime(int lifetime) {
/* 40 */     this.lifetime = lifetime;
/* 41 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setSize(float size) {
/* 45 */     this.size = size;
/* 46 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setSourcePos(class_243 sourcePos) {
/* 50 */     this.sourcePos = sourcePos;
/* 51 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setGravity(float gravity) {
/* 55 */     this.gravity = gravity;
/* 56 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setCollision(boolean collision) {
/* 60 */     this.collision = collision;
/* 61 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setFullbright(boolean fullbright) {
/* 65 */     this.fullbright = fullbright;
/* 66 */     return this;
/*    */   }
/*    */   
/*    */   public VFXParticleType setItemStack(class_1799 itemStack) {
/* 70 */     this.itemStack = itemStack;
/* 71 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\vfx\VFXParticleType.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */