/*    */ package com.lootbeams.mixin;
/*    */ 
/*    */ import com.lootbeams.extensions.LootbeamsBufferBuilder;
/*    */ import com.lootbeams.render.CustomVertexFormats;
/*    */ import net.minecraft.class_287;
/*    */ import net.minecraft.class_296;
/*    */ import org.lwjgl.system.MemoryUtil;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ 
/*    */ @Mixin({class_287.class})
/*    */ public abstract class BufferBuilderMixin
/*    */   implements LootbeamsBufferBuilder
/*    */ {
/*    */   public LootbeamsBufferBuilder color1(int red, int green, int blue, int alpha) {
/* 16 */     long l = method_60798(CustomVertexFormats.COLOR1);
/* 17 */     if (l != -1L) {
/* 18 */       MemoryUtil.memPutByte(l, (byte)red);
/* 19 */       MemoryUtil.memPutByte(l + 1L, (byte)green);
/* 20 */       MemoryUtil.memPutByte(l + 2L, (byte)blue);
/* 21 */       MemoryUtil.memPutByte(l + 3L, (byte)alpha);
/*    */     } 
/*    */     
/* 24 */     return this;
/*    */   } @Shadow
/*    */   protected abstract long method_60798(class_296 paramclass_296);
/*    */   public LootbeamsBufferBuilder uvCenter(float u, float v) {
/* 28 */     long l = method_60798(CustomVertexFormats.UV_CENTER);
/* 29 */     if (l != -1L) {
/* 30 */       MemoryUtil.memPutFloat(l, u);
/* 31 */       MemoryUtil.memPutFloat(l + 4L, v);
/*    */     } 
/*    */     
/* 34 */     return this;
/*    */   }
/*    */   
/*    */   public LootbeamsBufferBuilder uvSize(float w, float h) {
/* 38 */     long l = method_60798(CustomVertexFormats.UV_SIZE);
/* 39 */     if (l != -1L) {
/* 40 */       MemoryUtil.memPutFloat(l, w);
/* 41 */       MemoryUtil.memPutFloat(l + 4L, h);
/*    */     } 
/*    */     
/* 44 */     return this;
/*    */   }
/*    */   
/*    */   public LootbeamsBufferBuilder shortCustomData(float data0, float data1) {
/* 48 */     long l = method_60798(CustomVertexFormats.CUSTOM_DATA);
/* 49 */     if (l != -1L) {
/* 50 */       MemoryUtil.memPutFloat(l, data0);
/* 51 */       MemoryUtil.memPutFloat(l + 4L, data1);
/* 52 */       MemoryUtil.memPutFloat(l + 8L, 0.0F);
/* 53 */       MemoryUtil.memPutFloat(l + 12L, 0.0F);
/*    */     } 
/*    */     
/* 56 */     return this;
/*    */   }
/*    */   
/*    */   public LootbeamsBufferBuilder longCustomData(float data0, float data1, float data2, float data3) {
/* 60 */     long l = method_60798(CustomVertexFormats.CUSTOM_DATA);
/* 61 */     if (l != -1L) {
/* 62 */       MemoryUtil.memPutFloat(l, data0);
/* 63 */       MemoryUtil.memPutFloat(l + 4L, data1);
/* 64 */       MemoryUtil.memPutFloat(l + 8L, data2);
/* 65 */       MemoryUtil.memPutFloat(l + 12L, data3);
/*    */     } 
/*    */     
/* 68 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\mixin\BufferBuilderMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */