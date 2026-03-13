/*     */ package com.lootbeams.helpers;
/*     */ 
/*     */ import java.util.function.BiFunction;
/*     */ 
/*     */ public class ColorHelper {
/*     */   public static final class IntRGB extends Record { private final int R;
/*     */     private final int G;
/*     */     private final int B;
/*     */     
/*  10 */     public IntRGB(int R, int G, int B) { this.R = R; this.G = G; this.B = B; } public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/lootbeams/helpers/ColorHelper$IntRGB;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #10	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*  10 */       //   0	7	0	this	Lcom/lootbeams/helpers/ColorHelper$IntRGB; } public int R() { return this.R; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/lootbeams/helpers/ColorHelper$IntRGB;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #10	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/lootbeams/helpers/ColorHelper$IntRGB;
/*  10 */       //   0	8	1	o	Ljava/lang/Object; } public int G() { return this.G; } public int B() { return this.B; }
/*     */      public IntRGB lighter(int value) {
/*  12 */       return new IntRGB(
/*  13 */           Math.min(this.R + value, 255), 
/*  14 */           Math.min(this.G + value, 255), 
/*  15 */           Math.min(this.B + value, 255));
/*     */     }
/*     */ 
/*     */     
/*     */     public IntRGB darken(int value) {
/*  20 */       return new IntRGB(
/*  21 */           Math.max(this.R - value, 0), 
/*  22 */           Math.max(this.G - value, 0), 
/*  23 */           Math.max(this.B - value, 0));
/*     */     }
/*     */ 
/*     */     
/*     */     public int pack() {
/*  28 */       return this.R << 16 | this.G << 8 | this.B;
/*     */     }
/*     */     
/*     */     public static IntRGB of(ColorHelper.Color color) {
/*  32 */       return new IntRGB(color.R, color.G, color.B);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/*  41 */       return "RGB{R=" + this.R + ", G=" + this.G + ", B=" + this.B + "}";
/*     */     } }
/*     */   public static final class FloatRGB extends Record { private final float R; private final float G; private final float B;
/*     */     
/*  45 */     public FloatRGB(float R, float G, float B) { this.R = R; this.G = G; this.B = B; } public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/lootbeams/helpers/ColorHelper$FloatRGB;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #45	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/lootbeams/helpers/ColorHelper$FloatRGB; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/lootbeams/helpers/ColorHelper$FloatRGB;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #45	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/lootbeams/helpers/ColorHelper$FloatRGB;
/*  45 */       //   0	8	1	o	Ljava/lang/Object; } public float R() { return this.R; } public float G() { return this.G; } public float B() { return this.B; }
/*     */      public FloatRGB lighter(float value) {
/*  47 */       return new FloatRGB(
/*  48 */           Math.min(this.R + value, 1.0F), 
/*  49 */           Math.min(this.G + value, 1.0F), 
/*  50 */           Math.min(this.B + value, 1.0F));
/*     */     }
/*     */ 
/*     */     
/*     */     public FloatRGB darken(float value) {
/*  55 */       return new FloatRGB(
/*  56 */           Math.max(this.R - value, 0.0F), 
/*  57 */           Math.max(this.G - value, 0.0F), 
/*  58 */           Math.max(this.B - value, 0.0F));
/*     */     }
/*     */ 
/*     */     
/*     */     public int pack() {
/*  63 */       return (int)(this.R * 255.0F) << 16 | (int)(this.G * 255.0F) << 8 | (int)(this.B * 255.0F);
/*     */     }
/*     */     
/*     */     public static FloatRGB of(ColorHelper.Color color) {
/*  67 */       return new FloatRGB(color.fR, color.fG, color.fB);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/*  76 */       return "RGB{R=" + this.R + ", G=" + this.G + ", B=" + this.B + "}";
/*     */     } }
/*     */ 
/*     */   
/*     */   public static int parseColor(String color) {
/*  81 */     if (color.startsWith("#")) {
/*  82 */       color = color.substring(1);
/*     */     }
/*  84 */     if (color.length() == 3 || color.length() == 4) {
/*  85 */       color = color.replaceAll(".", "$0$0");
/*     */     }
/*  87 */     if (color.length() == 6) {
/*  88 */       color = "FF" + color;
/*     */     }
/*     */     
/*  91 */     int i = Integer.parseUnsignedInt(color, 16);
/*  92 */     if (Integer.compareUnsigned(i, 0) >= 0 && Integer.compareUnsigned(i, -1) <= 0)
/*     */     {
/*  94 */       return i;
/*     */     }
/*     */     
/*  97 */     return 0;
/*     */   }
/*     */   
/*     */   public static int darken(int color, float value) {
/* 101 */     int a = getAlpha(color);
/* 102 */     int r = (int)(getRed(color) * (1.0F - value));
/* 103 */     int g = (int)(getGreen(color) * (1.0F - value));
/* 104 */     int b = (int)(getBlue(color) * (1.0F - value));
/*     */     
/* 106 */     return build(a, r, g, b);
/*     */   }
/*     */   
/*     */   public static String darken(String color, float value) {
/* 110 */     int colorValue = parseColor(color);
/* 111 */     return toHexColor(darken(colorValue, value));
/*     */   }
/*     */   
/*     */   public static String toHexColor(int color) {
/* 115 */     return String.format("#%08X", new Object[] { Integer.valueOf(color) });
/*     */   }
/*     */   
/*     */   public static int getAlpha(int i) {
/* 119 */     if (i <= 16777215) {
/* 120 */       return 255;
/*     */     }
/* 122 */     return i >>> 24;
/*     */   }
/*     */   
/*     */   public static int getRed(int i) {
/* 126 */     return i >> 16 & 0xFF;
/*     */   }
/*     */   
/*     */   public static int getGreen(int i) {
/* 130 */     return i >> 8 & 0xFF;
/*     */   }
/*     */   
/*     */   public static int getBlue(int i) {
/* 134 */     return i & 0xFF;
/*     */   }
/*     */   
/*     */   public static IntRGB getIntRGB(int color) {
/* 138 */     return new IntRGB(getRed(color), getGreen(color), getBlue(color));
/*     */   }
/*     */   
/*     */   public static FloatRGB getFloatRGB(int color) {
/* 142 */     return new FloatRGB(
/* 143 */         getRed(color) / 255.0F, 
/* 144 */         getGreen(color) / 255.0F, 
/* 145 */         getBlue(color) / 255.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int build(int A, int R, int G, int B) {
/* 150 */     return A << 24 | R << 16 | G << 8 | B;
/*     */   }
/*     */   
/*     */   public static class Color {
/* 154 */     public int R = 255;
/* 155 */     public int G = 255;
/* 156 */     public int B = 255;
/* 157 */     public int A = 255;
/* 158 */     public float fR = 1.0F;
/* 159 */     public float fG = 1.0F;
/* 160 */     public float fB = 1.0F;
/* 161 */     public float fA = 1.0F;
/*     */     
/* 163 */     public int H = 0;
/* 164 */     public int S = 0;
/* 165 */     public int V = 255;
/* 166 */     public float fH = 0.0F;
/* 167 */     public float fS = 0.0F;
/* 168 */     public float fV = 1.0F;
/*     */     
/*     */     public Color(int color) {
/* 171 */       setRgb(color);
/* 172 */       updateHsvValues();
/*     */       
/* 174 */       this.A = ColorHelper.getAlpha(color);
/* 175 */       this.fA = this.A / 255.0F;
/*     */     }
/*     */     
/*     */     public void setRgb(int color) {
/* 179 */       this.R = ColorHelper.getRed(color);
/* 180 */       this.G = ColorHelper.getGreen(color);
/* 181 */       this.B = ColorHelper.getBlue(color);
/*     */       
/* 183 */       this.fR = this.R / 255.0F;
/* 184 */       this.fG = this.G / 255.0F;
/* 185 */       this.fB = this.B / 255.0F;
/*     */     }
/*     */     
/*     */     public void updateRgbValues() {
/* 189 */       int rgb = java.awt.Color.HSBtoRGB(this.fH, this.fS, this.fV);
/* 190 */       setRgb(rgb & 0xFFFFFF);
/*     */     }
/*     */     
/*     */     public void updateHsvValues() {
/* 194 */       float[] hsv = java.awt.Color.RGBtoHSB(this.R, this.G, this.B, null);
/*     */       
/* 196 */       this.fH = hsv[0];
/* 197 */       this.fS = hsv[1];
/* 198 */       this.fV = hsv[2];
/*     */       
/* 200 */       this.H = (int)(this.fH * 360.0F);
/* 201 */       this.S = (int)(this.fS * 255.0F);
/* 202 */       this.V = (int)(this.fV * 255.0F);
/*     */     }
/*     */     
/*     */     public int getRgb() {
/* 206 */       return ColorHelper.build(this.A, this.R, this.G, this.B);
/*     */     }
/*     */     
/*     */     public Color applyModifiers(List<String> modifiers) {
/* 210 */       Map<Character, BiFunction<Integer, Integer, Integer>> modifierFuncs = Map.of(
/* 211 */           Character.valueOf('+'), (v, a) -> Integer.valueOf(v.intValue() + a.intValue()), 
/* 212 */           Character.valueOf('-'), (v, a) -> Integer.valueOf(v.intValue() - a.intValue()), 
/* 213 */           Character.valueOf('='), (v, a) -> a);
/*     */ 
/*     */ 
/*     */       
/* 217 */       for (String modifier : modifiers) {
/*     */         int amount; BiFunction<Integer, Integer, Integer> mod;
/* 219 */         if (modifier.length() < 3) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */         
/* 224 */         char type = modifier.toLowerCase().charAt(1);
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 229 */           amount = Integer.parseInt(modifier.substring(2));
/* 230 */           mod = modifierFuncs.get(Character.valueOf(modifier.charAt(0)));
/*     */         }
/* 232 */         catch (Exception e) {
/*     */           continue;
/*     */         } 
/*     */ 
/*     */         
/* 237 */         if (mod == null) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */         
/* 242 */         switch (type) {
/*     */           
/*     */           case 'h':
/* 245 */             this.H = (((Integer)mod.apply(Integer.valueOf(this.H), Integer.valueOf(amount))).intValue() + 360) % 360;
/* 246 */             this.fH = this.H / 360.0F;
/* 247 */             updateRgbValues();
/*     */           
/*     */           case 's':
/* 250 */             this.S = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.S), Integer.valueOf(amount))).intValue(), 0, 255);
/* 251 */             this.fS = this.S / 255.0F;
/* 252 */             updateRgbValues();
/*     */           
/*     */           case 'v':
/* 255 */             this.V = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.V), Integer.valueOf(amount))).intValue(), 0, 255);
/* 256 */             this.fV = this.V / 255.0F;
/* 257 */             updateRgbValues();
/*     */           
/*     */           case 'r':
/* 260 */             this.R = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.R), Integer.valueOf(amount))).intValue(), 0, 255);
/* 261 */             this.fR = this.R / 255.0F;
/* 262 */             updateHsvValues();
/*     */           
/*     */           case 'g':
/* 265 */             this.G = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.G), Integer.valueOf(amount))).intValue(), 0, 255);
/* 266 */             this.fG = this.G / 255.0F;
/* 267 */             updateHsvValues();
/*     */           
/*     */           case 'b':
/* 270 */             this.B = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.B), Integer.valueOf(amount))).intValue(), 0, 255);
/* 271 */             this.fB = this.B / 255.0F;
/* 272 */             updateHsvValues();
/*     */           
/*     */           case 'a':
/* 275 */             this.A = Math.clamp(((Integer)mod.apply(Integer.valueOf(this.A), Integer.valueOf(amount))).intValue(), 0, 255);
/* 276 */             this.fA = this.A / 255.0F;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       } 
/* 283 */       return this;
/*     */     }
/*     */     
/*     */     public static Color of(class_5251 color) {
/* 287 */       return new Color(color.method_27716());
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 292 */       return "Color[R:" + this.R + ",G:" + this.G + ",B:" + this.B + ",A:" + this.A + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\ColorHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */