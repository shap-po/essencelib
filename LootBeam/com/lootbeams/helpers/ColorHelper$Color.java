/*     */ package com.lootbeams.helpers;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.BiFunction;
/*     */ import net.minecraft.class_5251;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Color
/*     */ {
/* 154 */   public int R = 255;
/* 155 */   public int G = 255;
/* 156 */   public int B = 255;
/* 157 */   public int A = 255;
/* 158 */   public float fR = 1.0F;
/* 159 */   public float fG = 1.0F;
/* 160 */   public float fB = 1.0F;
/* 161 */   public float fA = 1.0F;
/*     */   
/* 163 */   public int H = 0;
/* 164 */   public int S = 0;
/* 165 */   public int V = 255;
/* 166 */   public float fH = 0.0F;
/* 167 */   public float fS = 0.0F;
/* 168 */   public float fV = 1.0F;
/*     */   
/*     */   public Color(int color) {
/* 171 */     setRgb(color);
/* 172 */     updateHsvValues();
/*     */     
/* 174 */     this.A = ColorHelper.getAlpha(color);
/* 175 */     this.fA = this.A / 255.0F;
/*     */   }
/*     */   
/*     */   public void setRgb(int color) {
/* 179 */     this.R = ColorHelper.getRed(color);
/* 180 */     this.G = ColorHelper.getGreen(color);
/* 181 */     this.B = ColorHelper.getBlue(color);
/*     */     
/* 183 */     this.fR = this.R / 255.0F;
/* 184 */     this.fG = this.G / 255.0F;
/* 185 */     this.fB = this.B / 255.0F;
/*     */   }
/*     */   
/*     */   public void updateRgbValues() {
/* 189 */     int rgb = java.awt.Color.HSBtoRGB(this.fH, this.fS, this.fV);
/* 190 */     setRgb(rgb & 0xFFFFFF);
/*     */   }
/*     */   
/*     */   public void updateHsvValues() {
/* 194 */     float[] hsv = java.awt.Color.RGBtoHSB(this.R, this.G, this.B, null);
/*     */     
/* 196 */     this.fH = hsv[0];
/* 197 */     this.fS = hsv[1];
/* 198 */     this.fV = hsv[2];
/*     */     
/* 200 */     this.H = (int)(this.fH * 360.0F);
/* 201 */     this.S = (int)(this.fS * 255.0F);
/* 202 */     this.V = (int)(this.fV * 255.0F);
/*     */   }
/*     */   
/*     */   public int getRgb() {
/* 206 */     return ColorHelper.build(this.A, this.R, this.G, this.B);
/*     */   }
/*     */   
/*     */   public Color applyModifiers(List<String> modifiers) {
/* 210 */     Map<Character, BiFunction<Integer, Integer, Integer>> modifierFuncs = Map.of(
/* 211 */         Character.valueOf('+'), (v, a) -> Integer.valueOf(v.intValue() + a.intValue()), 
/* 212 */         Character.valueOf('-'), (v, a) -> Integer.valueOf(v.intValue() - a.intValue()), 
/* 213 */         Character.valueOf('='), (v, a) -> a);
/*     */ 
/*     */ 
/*     */     
/* 217 */     for (String modifier : modifiers) {
/*     */       int amount; BiFunction<Integer, Integer, Integer> mod;
/* 219 */       if (modifier.length() < 3) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 224 */       char type = modifier.toLowerCase().charAt(1);
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 229 */         amount = Integer.parseInt(modifier.substring(2));
/* 230 */         mod = modifierFuncs.get(Character.valueOf(modifier.charAt(0)));
/*     */       }
/* 232 */       catch (Exception e) {
/*     */         continue;
/*     */       } 
/*     */ 
/*     */       
/* 237 */       if (mod == null) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 242 */       switch (type) {
/*     */         
/*     */         case 'h':
/* 245 */           this.H = (((Integer)mod.apply(Integer.valueOf(this.H), Integer.valueOf(amount))).intValue() + 360) % 360;
/* 246 */           this.fH = this.H / 360.0F;
/* 247 */           updateRgbValues();
/*     */         
/*     */         case 's':
/* 250 */           this.S = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.S), Integer.valueOf(amount))).intValue(), 0, 255);
/* 251 */           this.fS = this.S / 255.0F;
/* 252 */           updateRgbValues();
/*     */         
/*     */         case 'v':
/* 255 */           this.V = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.V), Integer.valueOf(amount))).intValue(), 0, 255);
/* 256 */           this.fV = this.V / 255.0F;
/* 257 */           updateRgbValues();
/*     */         
/*     */         case 'r':
/* 260 */           this.R = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.R), Integer.valueOf(amount))).intValue(), 0, 255);
/* 261 */           this.fR = this.R / 255.0F;
/* 262 */           updateHsvValues();
/*     */         
/*     */         case 'g':
/* 265 */           this.G = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.G), Integer.valueOf(amount))).intValue(), 0, 255);
/* 266 */           this.fG = this.G / 255.0F;
/* 267 */           updateHsvValues();
/*     */         
/*     */         case 'b':
/* 270 */           this.B = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.B), Integer.valueOf(amount))).intValue(), 0, 255);
/* 271 */           this.fB = this.B / 255.0F;
/* 272 */           updateHsvValues();
/*     */         
/*     */         case 'a':
/* 275 */           this.A = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.A), Integer.valueOf(amount))).intValue(), 0, 255);
/* 276 */           this.fA = this.A / 255.0F;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 283 */     return this;
/*     */   }
/*     */   
/*     */   public static Color of(class_5251 color) {
/* 287 */     return new Color(color.method_27716());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 292 */     return "Color[R:" + this.R + ",G:" + this.G + ",B:" + this.B + ",A:" + this.A + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\ColorHelper$Color.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */