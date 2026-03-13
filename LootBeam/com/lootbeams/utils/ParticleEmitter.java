/*     */ package com.lootbeams.utils;
/*     */ 
/*     */ import com.lootbeams.config.Configuration;
/*     */ import com.lootbeams.helpers.ColorHelper;
/*     */ import com.lootbeams.helpers.RarityHelper;
/*     */ import com.lootbeams.managers.ParticleManager;
/*     */ import com.lootbeams.vfx.VFXParticleType;
/*     */ import java.util.Random;
/*     */ import java.util.WeakHashMap;
/*     */ import net.minecraft.class_1542;
/*     */ import net.minecraft.class_2394;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_5251;
/*     */ 
/*     */ public class ParticleEmitter {
/*  17 */   private static final Random RANDOM = new Random();
/*     */   private static final float TICK_PER_SECOND = 20.0F;
/*  19 */   private static final WeakHashMap<class_1542, Float> nextParticleSpawnTicks = new WeakHashMap<>();
/*     */   
/*     */   public static void createParticlesForItem(class_1542 itemEntity, Configuration itemConfig, int entityTime, class_5251 color, float alpha, float pticks) {
/*  22 */     if (!itemConfig.particles) {
/*     */       return;
/*     */     }
/*     */     
/*  26 */     if (itemConfig.particleRareOnly && !RarityHelper.rarityCheck(itemEntity.method_6983(), false)) {
/*     */       return;
/*     */     }
/*     */     
/*  30 */     if (class_310.method_1551().method_1493()) {
/*     */       return;
/*     */     }
/*     */     
/*  34 */     if (!nextParticleSpawnTicks.containsKey(itemEntity)) {
/*  35 */       nextParticleSpawnTicks.put(itemEntity, Float.valueOf(20.0F / itemConfig.particleCount));
/*     */     }
/*     */     
/*  38 */     ColorHelper.Color particleColor = ColorHelper.Color.of(color);
/*  39 */     float particleCount = itemConfig.particleCount;
/*  40 */     float currentTick = entityTime % 20.0F;
/*  41 */     float currentTickValue = currentTick + pticks;
/*  42 */     float ticksPerOneParticle = 20.0F / particleCount;
/*     */     
/*  44 */     if (currentTick == 0.0F && ((Float)nextParticleSpawnTicks.get(itemEntity)).floatValue() >= 20.0F) {
/*  45 */       nextParticleSpawnTicks.put(itemEntity, Float.valueOf(ticksPerOneParticle));
/*     */     }
/*     */     
/*  48 */     if (currentTickValue >= ((Float)nextParticleSpawnTicks.get(itemEntity)).floatValue()) {
/*  49 */       nextParticleSpawnTicks.put(itemEntity, Float.valueOf(((float)Math.floor((currentTickValue / ticksPerOneParticle)) + 1.0F) * ticksPerOneParticle));
/*     */ 
/*     */ 
/*     */       
/*  53 */       float particleSize = itemConfig.particleRandomSize ? RANDOM.nextFloat(0.25F * itemConfig.particleSize, 1.1F * itemConfig.particleSize) : itemConfig.particleSize;
/*  54 */       float particleSpeed = itemConfig.particleSpeed;
/*  55 */       float particleRadius = itemConfig.particleRadius;
/*  56 */       float randomnessIntensity = itemConfig.randomnessIntensity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  64 */       class_243 randomDir = (new class_243(RANDOM.nextDouble((-particleSpeed / 2.0F), (particleSpeed / 2.0F)), itemConfig.particleRandomY ? RANDOM.nextDouble((particleSpeed / 2.0F), particleSpeed) : particleSpeed, RANDOM.nextDouble((-particleSpeed / 2.0F), (particleSpeed / 2.0F)))).method_18805(randomnessIntensity, randomnessIntensity, randomnessIntensity);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  73 */       class_243 particleDir = (new class_243(itemConfig.particleDirectionX, itemConfig.particleDirectionY, itemConfig.particleDirectionZ)).method_18806(randomDir);
/*     */       
/*  75 */       ParticleManager.ParticleTexture texture = itemConfig.particleTexture;
/*     */ 
/*     */ 
/*     */       
/*  79 */       double particleY = itemConfig.particleRandomY ? RANDOM.nextDouble(itemEntity.method_23318() + itemConfig.particleYOffset - (particleRadius / 3.0F), itemEntity.method_23318() + itemConfig.particleYOffset + (particleRadius / 3.0F)) : (itemEntity.method_23318() + itemConfig.particleYOffset + (particleSize / 10.0F));
/*     */       
/*  81 */       addParticle(texture, particleColor.fR, particleColor.fG, particleColor.fB, alpha, itemConfig.particleLifetime, particleSize, new class_243(RANDOM
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  90 */             .nextDouble(itemEntity.method_23317() - particleRadius, itemEntity.method_23317() + particleRadius), particleY, RANDOM
/*     */             
/*  92 */             .nextDouble(itemEntity.method_23321() - particleRadius, itemEntity.method_23321() + particleRadius)), particleDir, itemEntity);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addParticle(ParticleManager.ParticleTexture texture, float red, float green, float blue, float alpha, int lifetime, float size, class_243 pos, class_243 motion, class_1542 itemEntity) {
/* 101 */     class_310 mc = class_310.method_1551();
/*     */ 
/*     */     
/* 104 */     alpha *= 1.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     VFXParticleType glowParticle = ParticleManager.GLOW_PARTICLE.setTexture(texture).setColor(red, green, blue, alpha).setLifetime(lifetime).setSize(size).setSourcePos(itemEntity.method_19538()).setGravity(0.0F).setCollision(false).setFullbright(true).setItemStack(itemEntity.method_6983());
/*     */     
/* 116 */     if (mc.field_1687 != null)
/* 117 */       mc.field_1687.method_8406((class_2394)glowParticle, pos.field_1352, pos.field_1351, pos.field_1350, motion.field_1352, motion.field_1351, motion.field_1350); 
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeam\\utils\ParticleEmitter.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */