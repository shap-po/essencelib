/*     */ package com.lootbeams.screens.widgets;
/*     */ 
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.helpers.RenderHelper;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import net.fabricmc.api.EnvType;
/*     */ import net.fabricmc.api.Environment;
/*     */ import net.minecraft.class_1144;
/*     */ import net.minecraft.class_156;
/*     */ import net.minecraft.class_1921;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_2960;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_327;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_339;
/*     */ import net.minecraft.class_342;
/*     */ import net.minecraft.class_3532;
/*     */ import net.minecraft.class_3544;
/*     */ import net.minecraft.class_4068;
/*     */ import net.minecraft.class_437;
/*     */ import net.minecraft.class_5250;
/*     */ import net.minecraft.class_5481;
/*     */ import net.minecraft.class_6381;
/*     */ import net.minecraft.class_6382;
/*     */ import net.minecraft.class_8666;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ @Environment(EnvType.CLIENT)
/*     */ public class PresetTextFieldWidget
/*     */   extends class_339
/*     */   implements class_4068
/*     */ {
/*  39 */   private static final class_8666 TEXTURES = new class_8666(
/*  40 */       LootBeams.id("textures/gui/text_field/normal.png"), 
/*  41 */       LootBeams.id("textures/gui/text_field/highlighted.png"));
/*     */   
/*  43 */   private int verticalPadding = 4;
/*  44 */   private int horizontalPadding = 8;
/*     */   private final class_327 textRenderer;
/*  46 */   private String text = "";
/*  47 */   private int maxLength = 32;
/*     */   
/*     */   private boolean drawsBackground = true;
/*     */   
/*     */   private boolean focusUnlocked = true;
/*     */   
/*     */   private boolean editable = true;
/*     */   private int firstCharacterIndex;
/*     */   private int selectionStart;
/*     */   private int selectionEnd;
/*  57 */   private int editableColor = 14737632;
/*  58 */   private int uneditableColor = 7368816;
/*     */   @Nullable
/*     */   private String suggestion;
/*     */   @Nullable
/*     */   private Consumer<String> changedListener;
/*  63 */   private Predicate<String> textPredicate = Objects::nonNull;
/*     */   
/*     */   private BiFunction<String, Integer, class_5481> renderTextProvider;
/*     */   
/*     */   @Nullable
/*     */   private class_2561 placeholder;
/*     */   private long lastSwitchFocusTime;
/*     */   
/*     */   public PresetTextFieldWidget(class_327 textRenderer, int width, int height, class_2561 text) {
/*  72 */     this(textRenderer, 0, 0, width, height, text);
/*     */   }
/*     */   
/*     */   public PresetTextFieldWidget(class_327 textRenderer, int x, int y, int width, int height, class_2561 text) {
/*  76 */     this(textRenderer, x, y, width, height, (class_342)null, text);
/*     */   }
/*     */   
/*     */   public PresetTextFieldWidget(class_327 textRenderer, int x, int y, int width, int height, @Nullable class_342 copyFrom, class_2561 text) {
/*  80 */     super(x, y, width, height, text); this.renderTextProvider = ((string, firstCharacterIndex) -> class_5481.method_30747(string, class_2583.field_24360)); this.lastSwitchFocusTime = class_156.method_658();
/*  81 */     this.textRenderer = textRenderer;
/*  82 */     if (copyFrom != null) {
/*  83 */       setText(copyFrom.method_1882());
/*     */     }
/*     */   }
/*     */   
/*     */   public void setChangedListener(Consumer<String> changedListener) {
/*  88 */     this.changedListener = changedListener;
/*     */   }
/*     */   
/*     */   public void setRenderTextProvider(BiFunction<String, Integer, class_5481> renderTextProvider) {
/*  92 */     this.renderTextProvider = renderTextProvider;
/*     */   }
/*     */ 
/*     */   
/*     */   protected class_5250 method_25360() {
/*  97 */     class_2561 text = method_25369();
/*  98 */     return class_2561.method_43469("gui.narrate.editBox", new Object[] { text, this.text });
/*     */   }
/*     */   
/*     */   public void setText(String text) {
/* 102 */     if (this.textPredicate.test(text)) {
/* 103 */       if (text.length() > this.maxLength) {
/* 104 */         this.text = text.substring(0, this.maxLength);
/*     */       } else {
/* 106 */         this.text = text;
/*     */       } 
/*     */       
/* 109 */       setCursorToEnd(false);
/* 110 */       setSelectionEnd(this.selectionStart);
/* 111 */       onChanged(text);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getText() {
/* 116 */     return this.text;
/*     */   }
/*     */   
/*     */   public String getSelectedText() {
/* 120 */     int i = Math.min(this.selectionStart, this.selectionEnd);
/* 121 */     int j = Math.max(this.selectionStart, this.selectionEnd);
/* 122 */     return this.text.substring(i, j);
/*     */   }
/*     */   
/*     */   public void setTextPredicate(Predicate<String> textPredicate) {
/* 126 */     this.textPredicate = textPredicate;
/*     */   }
/*     */   
/*     */   public void write(String text) {
/* 130 */     int i = Math.min(this.selectionStart, this.selectionEnd);
/* 131 */     int j = Math.max(this.selectionStart, this.selectionEnd);
/* 132 */     int k = this.maxLength - this.text.length() - i - j;
/* 133 */     if (k > 0) {
/* 134 */       String string = class_3544.method_57180(text);
/* 135 */       int l = string.length();
/* 136 */       if (k < l) {
/* 137 */         if (Character.isHighSurrogate(string.charAt(k - 1))) {
/* 138 */           k--;
/*     */         }
/*     */         
/* 141 */         string = string.substring(0, k);
/* 142 */         l = k;
/*     */       } 
/*     */       
/* 145 */       String string2 = (new StringBuilder(this.text)).replace(i, j, string).toString();
/* 146 */       if (this.textPredicate.test(string2)) {
/* 147 */         this.text = string2;
/* 148 */         setSelectionStart(i + l);
/* 149 */         setSelectionEnd(this.selectionStart);
/* 150 */         onChanged(this.text);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void onChanged(String newText) {
/* 156 */     if (this.changedListener != null) {
/* 157 */       this.changedListener.accept(newText);
/*     */     }
/*     */   }
/*     */   
/*     */   private void erase(int offset) {
/* 162 */     if (class_437.method_25441()) {
/* 163 */       eraseWords(offset);
/*     */     } else {
/* 165 */       eraseCharacters(offset);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void eraseWords(int wordOffset) {
/* 170 */     if (!this.text.isEmpty()) {
/* 171 */       if (this.selectionEnd != this.selectionStart) {
/* 172 */         write("");
/*     */       } else {
/* 174 */         eraseCharactersTo(getWordSkipPosition(wordOffset));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void eraseCharacters(int characterOffset) {
/* 180 */     eraseCharactersTo(getCursorPosWithOffset(characterOffset));
/*     */   }
/*     */   
/*     */   public void eraseCharactersTo(int position) {
/* 184 */     if (!this.text.isEmpty()) {
/* 185 */       if (this.selectionEnd != this.selectionStart) {
/* 186 */         write("");
/*     */       } else {
/* 188 */         int i = Math.min(position, this.selectionStart);
/* 189 */         int j = Math.max(position, this.selectionStart);
/* 190 */         if (i != j) {
/* 191 */           String string = (new StringBuilder(this.text)).delete(i, j).toString();
/* 192 */           if (this.textPredicate.test(string)) {
/* 193 */             this.text = string;
/* 194 */             setCursor(i, false);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public int getWordSkipPosition(int wordOffset) {
/* 202 */     return getWordSkipPosition(wordOffset, getCursor());
/*     */   }
/*     */   
/*     */   private int getWordSkipPosition(int wordOffset, int cursorPosition) {
/* 206 */     return getWordSkipPosition(wordOffset, cursorPosition, true);
/*     */   }
/*     */   
/*     */   private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
/* 210 */     int i = cursorPosition;
/* 211 */     boolean bl = (wordOffset < 0);
/* 212 */     int j = Math.abs(wordOffset);
/*     */     
/* 214 */     for (int k = 0; k < j; k++) {
/* 215 */       if (!bl) {
/* 216 */         int l = this.text.length();
/* 217 */         i = this.text.indexOf(' ', i);
/* 218 */         if (i == -1) {
/* 219 */           i = l;
/*     */         } else {
/* 221 */           while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
/* 222 */             i++;
/*     */           }
/*     */         } 
/*     */       } else {
/* 226 */         while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
/* 227 */           i--;
/*     */         }
/*     */         
/* 230 */         while (i > 0 && this.text.charAt(i - 1) != ' ') {
/* 231 */           i--;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 236 */     return i;
/*     */   }
/*     */   
/*     */   public void moveCursor(int offset, boolean shiftKeyPressed) {
/* 240 */     setCursor(getCursorPosWithOffset(offset), shiftKeyPressed);
/*     */   }
/*     */   
/*     */   private int getCursorPosWithOffset(int offset) {
/* 244 */     return class_156.method_27761(this.text, this.selectionStart, offset);
/*     */   }
/*     */   
/*     */   public void setCursor(int cursor, boolean shiftKeyPressed) {
/* 248 */     setSelectionStart(cursor);
/* 249 */     if (!shiftKeyPressed) {
/* 250 */       setSelectionEnd(this.selectionStart);
/*     */     }
/*     */     
/* 253 */     onChanged(this.text);
/*     */   }
/*     */   
/*     */   public void setSelectionStart(int cursor) {
/* 257 */     this.selectionStart = class_3532.method_15340(cursor, 0, this.text.length());
/* 258 */     updateFirstCharacterIndex(this.selectionStart);
/*     */   }
/*     */   
/*     */   public void setCursorToStart(boolean shiftKeyPressed) {
/* 262 */     setCursor(0, shiftKeyPressed);
/*     */   }
/*     */   
/*     */   public void setCursorToEnd(boolean shiftKeyPressed) {
/* 266 */     setCursor(this.text.length(), shiftKeyPressed);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
/* 271 */     if (method_37303() && method_25370()) {
/* 272 */       switch (keyCode)
/*     */       { case 259:
/* 274 */           if (this.editable) {
/* 275 */             erase(-1);
/*     */           }
/*     */           
/* 278 */           return true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         default:
/* 285 */           if (class_437.method_25439(keyCode)) {
/* 286 */             setCursorToEnd(false);
/* 287 */             setSelectionEnd(0);
/* 288 */             return true;
/* 289 */           }  if (class_437.method_25438(keyCode)) {
/* 290 */             (class_310.method_1551()).field_1774.method_1455(getSelectedText());
/* 291 */             return true;
/* 292 */           }  if (class_437.method_25437(keyCode)) {
/* 293 */             if (isEditable()) {
/* 294 */               write((class_310.method_1551()).field_1774.method_1460());
/*     */             }
/*     */             
/* 297 */             return true;
/*     */           } 
/* 299 */           if (class_437.method_25436(keyCode)) {
/* 300 */             (class_310.method_1551()).field_1774.method_1455(getSelectedText());
/* 301 */             if (isEditable()) {
/* 302 */               write("");
/*     */             }
/*     */             
/* 305 */             return true;
/*     */           } 
/*     */           
/* 308 */           return false;
/*     */         
/*     */         case 261:
/* 311 */           if (this.editable) {
/* 312 */             erase(1);
/*     */           }
/*     */           
/* 315 */           return true;
/*     */         case 262:
/* 317 */           if (class_437.method_25441()) {
/* 318 */             setCursor(getWordSkipPosition(1), class_437.method_25442());
/*     */           } else {
/* 320 */             moveCursor(1, class_437.method_25442());
/*     */           } 
/*     */           
/* 323 */           return true;
/*     */         case 263:
/* 325 */           if (class_437.method_25441()) {
/* 326 */             setCursor(getWordSkipPosition(-1), class_437.method_25442());
/*     */           } else {
/* 328 */             moveCursor(-1, class_437.method_25442());
/*     */           } 
/*     */           
/* 331 */           return true;
/*     */         case 268:
/* 333 */           setCursorToStart(class_437.method_25442());
/* 334 */           return true;
/*     */         case 269:
/* 336 */           break; }  setCursorToEnd(class_437.method_25442());
/* 337 */       return true;
/*     */     } 
/*     */     
/* 340 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 345 */     return (method_37303() && method_25370() && isEditable());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean method_25400(char chr, int modifiers) {
/* 350 */     if (!isActive())
/* 351 */       return false; 
/* 352 */     if (class_3544.method_57175(chr)) {
/* 353 */       if (this.editable) {
/* 354 */         write(Character.toString(chr));
/*     */       }
/*     */       
/* 357 */       return true;
/*     */     } 
/* 359 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void method_25348(double mouseX, double mouseY) {
/* 365 */     int i = class_3532.method_15357(mouseX) - method_46426();
/* 366 */     if (this.drawsBackground) {
/* 367 */       i -= this.horizontalPadding;
/*     */     }
/*     */     
/* 370 */     String string = this.textRenderer.method_27523(this.text.substring(this.firstCharacterIndex), getInnerWidth());
/* 371 */     setCursor(this.textRenderer.method_27523(string, i).length() + this.firstCharacterIndex, class_437.method_25442());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void method_25354(class_1144 soundManager) {}
/*     */ 
/*     */   
/*     */   public void method_48579(class_332 context, int mouseX, int mouseY, float delta) {
/* 380 */     if (isVisible()) {
/* 381 */       if (drawsBackground()) {
/* 382 */         class_2960 texture = TEXTURES.method_52729(method_37303(), method_25370());
/* 383 */         RenderSystem.setShaderTexture(0, texture);
/* 384 */         RenderHelper.blit(context, texture, method_46426(), method_46427(), method_25368(), method_25364(), 0, 0);
/*     */       } 
/*     */       
/* 387 */       int i = this.editable ? this.editableColor : this.uneditableColor;
/* 388 */       int j = this.selectionStart - this.firstCharacterIndex;
/* 389 */       String string = this.textRenderer.method_27523(this.text.substring(this.firstCharacterIndex), getInnerWidth());
/* 390 */       boolean bl = (j >= 0 && j <= string.length());
/* 391 */       boolean bl2 = (method_25370() && (class_156.method_658() - this.lastSwitchFocusTime) / 300L % 2L == 0L && bl);
/* 392 */       int k = this.drawsBackground ? (method_46426() + this.horizontalPadding) : method_46426();
/* 393 */       int l = this.drawsBackground ? (method_46427() + (this.field_22759 - this.verticalPadding * 2) / 2) : method_46427();
/* 394 */       int m = k;
/* 395 */       int n = class_3532.method_15340(this.selectionEnd - this.firstCharacterIndex, 0, string.length());
/* 396 */       if (!string.isEmpty()) {
/* 397 */         String string2 = bl ? string.substring(0, j) : string;
/* 398 */         m = context.method_35720(this.textRenderer, this.renderTextProvider.apply(string2, Integer.valueOf(this.firstCharacterIndex)), k, l, i);
/*     */       } 
/*     */       
/* 401 */       boolean bl3 = (this.selectionStart < this.text.length() || this.text.length() >= getMaxLength());
/* 402 */       int o = m;
/* 403 */       if (!bl) {
/* 404 */         o = (j > 0) ? (k + this.field_22758) : k;
/* 405 */       } else if (bl3) {
/* 406 */         o = m - 1;
/* 407 */         m--;
/*     */       } 
/*     */       
/* 410 */       if (!string.isEmpty() && bl && j < string.length()) {
/* 411 */         context.method_35720(this.textRenderer, this.renderTextProvider.apply(string.substring(j), Integer.valueOf(this.selectionStart)), m, l, i);
/*     */       }
/*     */       
/* 414 */       if (this.placeholder != null && string.isEmpty() && !method_25370()) {
/* 415 */         context.method_27535(this.textRenderer, this.placeholder, m, l, i);
/*     */       }
/*     */       
/* 418 */       if (!bl3 && this.suggestion != null) {
/* 419 */         context.method_25303(this.textRenderer, this.suggestion, o - 1, l, -8355712);
/*     */       }
/*     */       
/* 422 */       if (bl2) {
/* 423 */         if (bl3) {
/* 424 */           context.method_51739(class_1921.method_51785(), o, l - 1, o + 1, l + 1 + 9, -3092272);
/*     */         } else {
/* 426 */           context.method_25303(this.textRenderer, "_", o, l, i);
/*     */         } 
/*     */       }
/*     */       
/* 430 */       if (n != j) {
/* 431 */         int p = k + this.textRenderer.method_1727(string.substring(0, n));
/* 432 */         drawSelectionHighlight(context, o, l - 1, p - 1, l + 1 + 9);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void drawSelectionHighlight(class_332 context, int x1, int y1, int x2, int y2) {
/* 438 */     if (x1 < x2) {
/* 439 */       int i = x1;
/* 440 */       x1 = x2;
/* 441 */       x2 = i;
/*     */     } 
/*     */     
/* 444 */     if (y1 < y2) {
/* 445 */       int i = y1;
/* 446 */       y1 = y2;
/* 447 */       y2 = i;
/*     */     } 
/*     */     
/* 450 */     if (x2 > method_46426() + this.field_22758) {
/* 451 */       x2 = this.drawsBackground ? (method_46426() + this.field_22758 - this.horizontalPadding) : (method_46426() + this.field_22758);
/*     */     }
/*     */     
/* 454 */     if (x1 > method_46426() + this.field_22758) {
/* 455 */       x1 = this.drawsBackground ? (method_46426() + this.field_22758 - this.horizontalPadding) : (method_46426() + this.field_22758);
/*     */     }
/*     */     
/* 458 */     context.method_51739(class_1921.method_51786(), x1, y1, x2, y2, -16776961);
/*     */   }
/*     */   
/*     */   public void setMaxLength(int maxLength) {
/* 462 */     this.maxLength = maxLength;
/* 463 */     if (this.text.length() > maxLength) {
/* 464 */       this.text = this.text.substring(0, maxLength);
/* 465 */       onChanged(this.text);
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getMaxLength() {
/* 470 */     return this.maxLength;
/*     */   }
/*     */   
/*     */   public int getCursor() {
/* 474 */     return this.selectionStart;
/*     */   }
/*     */   
/*     */   public boolean drawsBackground() {
/* 478 */     return this.drawsBackground;
/*     */   }
/*     */   
/*     */   public void setDrawsBackground(boolean drawsBackground) {
/* 482 */     this.drawsBackground = drawsBackground;
/*     */   }
/*     */   
/*     */   public void setEditableColor(int editableColor) {
/* 486 */     this.editableColor = editableColor;
/*     */   }
/*     */   
/*     */   public void setUneditableColor(int uneditableColor) {
/* 490 */     this.uneditableColor = uneditableColor;
/*     */   }
/*     */   
/*     */   public void setVerticalPadding(int padding) {
/* 494 */     this.verticalPadding = padding;
/*     */   }
/*     */   
/*     */   public void setHorizontalPadding(int padding) {
/* 498 */     this.horizontalPadding = padding;
/*     */   }
/*     */   
/*     */   public void setPadding(int vertical, int horizontal) {
/* 502 */     setVerticalPadding(vertical);
/* 503 */     setHorizontalPadding(horizontal);
/*     */   }
/*     */   
/*     */   public void setPadding(int padding) {
/* 507 */     setVerticalPadding(padding);
/* 508 */     setHorizontalPadding(padding);
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_25365(boolean focused) {
/* 513 */     if (this.focusUnlocked || focused) {
/* 514 */       super.method_25365(focused);
/* 515 */       if (focused) {
/* 516 */         this.lastSwitchFocusTime = class_156.method_658();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isEditable() {
/* 522 */     return this.editable;
/*     */   }
/*     */   
/*     */   public void setEditable(boolean editable) {
/* 526 */     this.editable = editable;
/*     */   }
/*     */   
/*     */   public int getInnerWidth() {
/* 530 */     return drawsBackground() ? (this.field_22758 - this.horizontalPadding * 2) : this.field_22758;
/*     */   }
/*     */   
/*     */   public void setSelectionEnd(int index) {
/* 534 */     this.selectionEnd = class_3532.method_15340(index, 0, this.text.length());
/* 535 */     updateFirstCharacterIndex(this.selectionEnd);
/*     */   }
/*     */   
/*     */   private void updateFirstCharacterIndex(int cursor) {
/* 539 */     if (this.textRenderer != null) {
/* 540 */       this.firstCharacterIndex = Math.min(this.firstCharacterIndex, this.text.length());
/* 541 */       int i = getInnerWidth();
/* 542 */       String string = this.textRenderer.method_27523(this.text.substring(this.firstCharacterIndex), i);
/* 543 */       int j = string.length() + this.firstCharacterIndex;
/* 544 */       if (cursor == this.firstCharacterIndex) {
/* 545 */         this.firstCharacterIndex -= this.textRenderer.method_27524(this.text, i, true).length();
/*     */       }
/*     */       
/* 548 */       if (cursor > j) {
/* 549 */         this.firstCharacterIndex += cursor - j;
/* 550 */       } else if (cursor <= this.firstCharacterIndex) {
/* 551 */         this.firstCharacterIndex -= this.firstCharacterIndex - cursor;
/*     */       } 
/*     */       
/* 554 */       this.firstCharacterIndex = class_3532.method_15340(this.firstCharacterIndex, 0, this.text.length());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setFocusUnlocked(boolean focusUnlocked) {
/* 559 */     this.focusUnlocked = focusUnlocked;
/*     */   }
/*     */   
/*     */   public boolean isVisible() {
/* 563 */     return this.field_22764;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible) {
/* 567 */     this.field_22764 = visible;
/*     */   }
/*     */   
/*     */   public void setSuggestion(@Nullable String suggestion) {
/* 571 */     this.suggestion = suggestion;
/*     */   }
/*     */   
/*     */   public int getCharacterX(int index) {
/* 575 */     return (index > this.text.length()) ? method_46426() : (method_46426() + this.textRenderer.method_1727(this.text.substring(0, index)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void method_47399(class_6382 builder) {
/* 580 */     builder.method_37034(class_6381.field_33788, (class_2561)method_25360());
/*     */   }
/*     */   
/*     */   public void setPlaceholder(class_2561 placeholder) {
/* 584 */     this.placeholder = placeholder;
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\screens\widgets\PresetTextFieldWidget.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */