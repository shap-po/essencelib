/*    */ package com.lootbeams.utils;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.function.BiPredicate;
/*    */ import net.minecraft.class_2514;
/*    */ import net.minecraft.class_2520;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class null
/*    */   extends HashMap<String, BiPredicate<class_2520, String>>
/*    */ {
/*    */   null() {
/* 46 */     put("=", (tag, value) -> tag.method_10714().contentEquals(value));
/*    */ 
/*    */     
/* 49 */     put("!=", (tag, value) -> !tag.method_10714().contentEquals(value));
/*    */ 
/*    */     
/* 52 */     put(">", (tag, value) -> {
/*    */           try {
/*    */             double parsedValue = Double.valueOf(value).doubleValue();
/*    */ 
/*    */ 
/*    */ 
/*    */             
/*    */             return (tag instanceof class_2514) ? ((((class_2514)tag).method_10697() > parsedValue)) : false;
/* 60 */           } catch (Exception var4) {
/*    */             return false;
/*    */           } 
/*    */         });
/* 64 */     put("<", (tag, value) -> {
/*    */           try {
/*    */             double parsedValue = Double.valueOf(value).doubleValue();
/*    */ 
/*    */ 
/*    */ 
/*    */             
/*    */             return (tag instanceof class_2514) ? ((((class_2514)tag).method_10697() < parsedValue)) : false;
/* 72 */           } catch (Exception var4) {
/*    */             return false;
/*    */           } 
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeam\\utils\Selectors$2.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */