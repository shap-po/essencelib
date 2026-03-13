/*     */ package com.lootbeams.utils;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.function.BiPredicate;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1792;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_1814;
/*     */ import net.minecraft.class_1836;
/*     */ import net.minecraft.class_2487;
/*     */ import net.minecraft.class_2499;
/*     */ import net.minecraft.class_2514;
/*     */ import net.minecraft.class_2520;
/*     */ import net.minecraft.class_2522;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_5224;
/*     */ import net.minecraft.class_5251;
/*     */ import net.minecraft.class_6862;
/*     */ import net.minecraft.class_7225;
/*     */ import net.minecraft.class_7923;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Selectors
/*     */ {
/*  36 */   private static Map<String, class_1814> rarities = new HashMap<String, class_1814>()
/*     */     {
/*     */     
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static Map<String, BiPredicate<class_2520, String>> nbtComparators = new HashMap<String, BiPredicate<class_2520, String>>()
/*     */     {
/*     */     
/*     */     };
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
/*     */   public static boolean validateSelector(String value) {
/*  83 */     if (value.contains("+")) {
/*  84 */       String[] var1 = value.split("\\+");
/*  85 */       int var2 = var1.length;
/*     */       
/*  87 */       for (int var3 = 0; var3 < var2; var3++) {
/*  88 */         String selector = var1[var3];
/*  89 */         if (!validateSelector(selector)) {
/*  90 */           return false;
/*     */         }
/*     */       } 
/*     */       
/*  94 */       return true;
/*  95 */     }  if (value.startsWith("~"))
/*  96 */       return validateSelector(value.substring(1)); 
/*  97 */     if (value.contentEquals("*"))
/*  98 */       return true; 
/*  99 */     if (value.startsWith("$"))
/* 100 */       return (class_2960.method_12829(value.substring(1)) != null); 
/* 101 */     if (value.startsWith("@"))
/* 102 */       return value.substring(1).matches("^[a-z][a-z0-9_-]{1,63}$"); 
/* 103 */     if (value.startsWith("!"))
/* 104 */       return rarities.keySet().contains(value.substring(1).toLowerCase()); 
/* 105 */     if (value.startsWith("#"))
/* 106 */       return (class_5251.method_27719(value).result().orElse(null) != null); 
/* 107 */     if (!value.startsWith("%") && !value.startsWith("^")) {
/* 108 */       if (value.startsWith("&")) {
/* 109 */         return true;
/*     */       }
/* 111 */       return (value == null || value == "" || class_2960.method_12829(value) != null);
/*     */     } 
/*     */     
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean itemMatches(class_1799 item, String selector, class_7225.class_7874 provider) {
/* 119 */     if (item.method_7960()) {
/* 120 */       return false;
/*     */     }
/*     */     
/* 123 */     if (selector.contains("+")) {
/* 124 */       String[] var11 = selector.split("\\+");
/* 125 */       int var16 = var11.length;
/*     */       
/* 127 */       for (int var18 = 0; var18 < var16; var18++) {
/* 128 */         String tooltipText = var11[var18];
/* 129 */         if (!itemMatches(item, tooltipText, provider)) {
/* 130 */           return false;
/*     */         }
/*     */       } 
/*     */       
/* 134 */       return true;
/* 135 */     }  if (selector.startsWith("~"))
/* 136 */       return !itemMatches(item, selector.substring(1), provider); 
/* 137 */     if (selector.contentEquals("*")) {
/* 138 */       return true;
/*     */     }
/* 140 */     String itemResourceLocation = class_7923.field_41178.method_10221(item.method_7909()).toString();
/* 141 */     if (!selector.equals(itemResourceLocation) && !selector.equals(itemResourceLocation.replace("minecraft:", ""))) {
/* 142 */       if (selector.startsWith("@")) {
/* 143 */         if (itemResourceLocation.startsWith(selector.substring(1) + ":")) {
/* 144 */           return true;
/*     */         }
/* 146 */       } else if (selector.startsWith("#")) {
/* 147 */         class_5251 entryColor = class_5251.method_27719(selector).result().orElse(null);
/* 148 */         if (entryColor != null && entryColor.equals(getColorForItem(item, class_5251.method_27717(16777215)))) {
/* 149 */           return true;
/*     */         }
/* 151 */       } else if (selector.startsWith("!")) {
/* 152 */         if (item.method_7932() == rarities.get(selector.substring(1))) {
/* 153 */           return true;
/*     */         }
/* 155 */       } else if (selector.startsWith("$")) {
/*     */ 
/*     */         
/* 158 */         Optional<class_6862<class_1792>> matchingTag = class_7923.field_41178.method_40273().filter(tagKey -> tagKey.comp_327().equals(class_2960.method_60654(selector.substring(1)))).findFirst();
/* 159 */         if (matchingTag.isPresent() && item.method_31573(matchingTag.get())) {
/* 160 */           return true;
/*     */         }
/* 162 */       } else if (selector.startsWith("%")) {
/* 163 */         if (item.method_7954().getString().contains(selector.substring(1))) {
/* 164 */           return true;
/*     */         }
/* 166 */       } else if (selector.startsWith("^")) {
/* 167 */         class_310 mc = class_310.method_1551();
/* 168 */         List<class_2561> lines = item.method_7950(class_1792.class_9635.field_51353, (class_1657)mc.field_1724, (class_1836)class_1836.class_1837.field_41071);
/* 169 */         String tooltipText = "";
/*     */         
/* 171 */         for (int n = 1; n < lines.size(); n++) {
/* 172 */           tooltipText = tooltipText + tooltipText + "\n";
/*     */         }
/*     */         
/* 175 */         if (tooltipText.contains(selector.substring(1))) {
/* 176 */           return true;
/*     */         }
/* 178 */       } else if (selector.startsWith("&")) {
/* 179 */         String name = selector.substring(1);
/* 180 */         String value = null;
/* 181 */         BiPredicate<class_2520, String> valueChecker = null;
/* 182 */         Iterator<String> var19 = nbtComparators.keySet().iterator();
/*     */ 
/*     */         
/* 185 */         while (var19.hasNext()) {
/* 186 */           String comparator = var19.next();
/* 187 */           if (name.contains(comparator)) {
/* 188 */             valueChecker = nbtComparators.get(comparator);
/* 189 */             String[] components = name.split(comparator);
/* 190 */             name = components[0];
/* 191 */             if (components.length > 1) {
/* 192 */               value = components[1];
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 198 */         class_2520 itemTag = item.method_57358(provider);
/* 199 */         boolean result = findMatchingSubtag(itemTag, name, value, valueChecker);
/* 200 */         if (!result) {
/* 201 */           if (!name.contains(":")) {
/* 202 */             name = "minecraft:" + name;
/*     */           }
/*     */           
/* 205 */           if (value != null) {
/* 206 */             if (!value.contains(":") && value.matches("^[a-z]+$")) {
/* 207 */               value = "minecraft:" + value;
/* 208 */             } else if (value.contains("\"")) {
/* 209 */               String[] components = value.split("\"");
/*     */               
/* 211 */               for (int i = 0; i < components.length; i++) {
/* 212 */                 if (i % 2 == 1 && !components[i].contains(":")) {
/* 213 */                   components[i] = "minecraft:" + components[i];
/*     */                 }
/*     */               } 
/*     */               
/* 217 */               value = String.join("\"", (CharSequence[])components);
/*     */             } 
/*     */           }
/*     */           
/* 221 */           result = findMatchingSubtag(itemTag, name, value, valueChecker);
/*     */         } 
/*     */         
/* 224 */         return result;
/*     */       } 
/*     */       
/* 227 */       return false;
/*     */     } 
/* 229 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean findMatchingSubtag(class_2520 tag, String key, String value, BiPredicate<class_2520, String> valueChecker)
/*     */   {
/*     */     class_2487 class_2487;
/* 236 */     if (tag == null) {
/* 237 */       return false;
/*     */     }
/* 239 */     if (tag.method_10711() == 8) {
/*     */       try {
/* 241 */         class_2487 = class_2522.method_10718(tag.method_10714());
/* 242 */       } catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 247 */     if (class_2487.method_10711() == 10) {
/* 248 */       class_2487 compoundTag = class_2487;
/* 249 */       if (compoundTag.method_10545(key)) {
/* 250 */         return (value == null && valueChecker == null) ? true : valueChecker.test(compoundTag.method_10580(key), value);
/*     */       }
/* 252 */       Iterator<String> var5 = compoundTag.method_10541().iterator();
/*     */ 
/*     */       
/* 255 */       while (var5.hasNext()) {
/* 256 */         String innerKey = var5.next();
/* 257 */         if (compoundTag.method_10540(innerKey) != 9 && compoundTag.method_10540(innerKey) != 10) {
/* 258 */           if (compoundTag.method_10540(innerKey) == 8)
/*     */             try {
/* 260 */               class_2487 = class_2522.method_10718(class_2487.method_10714());
/* 261 */               if (findMatchingSubtag(compoundTag.method_10580(innerKey), key, value, valueChecker)) {
/* 262 */                 return true;
/*     */               }
/* 264 */             } catch (Exception exception) {} 
/*     */           continue;
/*     */         } 
/* 267 */         if (findMatchingSubtag(compoundTag.method_10580(innerKey), key, value, valueChecker)) {
/* 268 */           return true;
/*     */         }
/*     */       } 
/*     */       
/* 272 */       return false;
/*     */     } 
/*     */     
/* 275 */     if (class_2487.method_10711() == 9) {
/* 276 */       class_2520 innerTag; class_2499 listTag = (class_2499)class_2487;
/* 277 */       Iterator<class_2520> var5 = listTag.iterator();
/*     */ 
/*     */ 
/*     */       
/*     */       do {
/* 282 */         if (!var5.hasNext()) {
/* 283 */           return false;
/*     */         }
/*     */         
/* 286 */         innerTag = var5.next();
/* 287 */       } while ((innerTag.method_10711() != 9 && innerTag.method_10711() != 10) || 
/* 288 */         !findMatchingSubtag(innerTag, key, value, valueChecker));
/*     */       
/* 290 */       return true;
/*     */     } 
/* 292 */     return false;
/*     */   }
/*     */   
/*     */   public static final class SelectorDocumentation extends Record { private final String name;
/*     */     private final String description;
/*     */     private final List<String> examples;
/*     */     
/* 299 */     public SelectorDocumentation(String name, String description, String... examples) { this(name, description, Arrays.asList(examples)); }
/*     */     
/*     */     public final String toString() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/lootbeams/utils/Selectors$SelectorDocumentation;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #297	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/lootbeams/utils/Selectors$SelectorDocumentation;
/* 303 */     } public SelectorDocumentation(String name, String description, List<String> examples) { this.name = name;
/* 304 */       this.description = description;
/* 305 */       this.examples = examples; }
/*     */     public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/lootbeams/utils/Selectors$SelectorDocumentation;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #297	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/lootbeams/utils/Selectors$SelectorDocumentation; } public final boolean equals(Object o) {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/lootbeams/utils/Selectors$SelectorDocumentation;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #297	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/lootbeams/utils/Selectors$SelectorDocumentation;
/*     */       //   0	8	1	o	Ljava/lang/Object;
/*     */     } public String name() {
/* 309 */       return this.name;
/*     */     }
/*     */     
/*     */     public String description() {
/* 313 */       return this.description;
/*     */     }
/*     */     
/*     */     public List<String> examples() {
/* 317 */       return this.examples;
/*     */     } }
/*     */ 
/*     */   
/*     */   private static class ColorCollector implements class_5224 {
/* 322 */     private class_5251 color = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean accept(int index, class_2583 style, int codePoint) {
/* 328 */       if (style.method_10973() != null) {
/* 329 */         this.color = style.method_10973();
/* 330 */         return false;
/*     */       } 
/* 332 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public class_5251 getColor() {
/* 337 */       return this.color;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class_5251 findFirstColorCode(class_2561 textComponent) {
/* 342 */     String rawTitle = textComponent.getString();
/*     */     
/* 344 */     for (int i = 0; i < rawTitle.length(); i += 2) {
/* 345 */       if (rawTitle.charAt(i) != '§') {
/* 346 */         return null;
/*     */       }
/*     */       
/*     */       try {
/* 350 */         class_124 format = class_124.method_544(rawTitle.charAt(i + 1));
/* 351 */         if (format != null && format.method_543()) {
/* 352 */           return class_5251.method_27718(format);
/*     */         }
/* 354 */       } catch (StringIndexOutOfBoundsException var4) {
/* 355 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/* 359 */     return null;
/*     */   }
/*     */   
/*     */   public static class_5251 getColorForItem(class_1799 item, class_5251 defaultColor) {
/* 363 */     class_5251 result = null;
/* 364 */     result = item.method_7954().method_10866().method_10973();
/* 365 */     if (item.method_7909() != null && item.method_7909().method_7864(item) != null && item.method_7909().method_7864(item).method_10866() != null && item.method_7909().method_7864(item).method_10866().method_10973() != null) {
/* 366 */       result = item.method_7909().method_7864(item).method_10866().method_10973();
/*     */     }
/*     */     
/* 369 */     if (!item.method_7964().method_10866().method_10967() && item.method_7964().method_10866().method_10973() != null) {
/* 370 */       result = item.method_7964().method_10866().method_10973();
/*     */     }
/*     */     
/* 373 */     class_5251 formattingColor = findFirstColorCode(item.method_7964());
/* 374 */     if (formattingColor != null) {
/* 375 */       result = formattingColor;
/*     */     }
/*     */     
/* 378 */     ColorCollector colorCollector = new ColorCollector();
/* 379 */     item.method_7964().method_30937().accept(colorCollector);
/* 380 */     if (colorCollector.getColor() != null) {
/* 381 */       result = colorCollector.getColor();
/*     */     }
/*     */     
/* 384 */     if (result == null || result.equals(item.method_7954().method_10866().method_10973())) {
/* 385 */       class_310 mc = class_310.method_1551();
/* 386 */       List<class_2561> lines = null;
/*     */       
/*     */       try {
/* 389 */         lines = item.method_7950(class_1792.class_9635.field_51353, (class_1657)mc.field_1724, (class_1836)class_1836.class_1837.field_41071);
/* 390 */       } catch (Exception exception) {}
/*     */ 
/*     */       
/* 393 */       if (lines != null && !lines.isEmpty()) {
/* 394 */         result = ((class_2561)lines.get(0)).method_10866().method_10973();
/*     */       }
/*     */     } 
/*     */     
/* 398 */     if (result == null) {
/* 399 */       result = defaultColor;
/*     */     }
/*     */     
/* 402 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeam\\utils\Selectors.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */