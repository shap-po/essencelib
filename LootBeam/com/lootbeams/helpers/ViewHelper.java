/*    */ package com.lootbeams.helpers;
/*    */ 
/*    */ import com.lootbeams.LootBeams;
/*    */ import net.minecraft.class_1041;
/*    */ import net.minecraft.class_1657;
/*    */ import net.minecraft.class_1799;
/*    */ import net.minecraft.class_243;
/*    */ import net.minecraft.class_310;
/*    */ import net.minecraft.class_3532;
/*    */ import net.minecraft.class_4184;
/*    */ import net.minecraft.class_5498;
/*    */ import net.minecraft.class_7833;
/*    */ import org.joml.Quaternionf;
/*    */ import org.joml.Vector3f;
/*    */ 
/*    */ public class ViewHelper {
/*    */   public static boolean shouldRenderOnItem(class_1799 itemStack) {
/* 18 */     boolean shouldRender = false;
/* 19 */     if (LootBeams.config.allItems) {
/* 20 */       shouldRender = true;
/*    */     } else {
/* 22 */       if (LootBeams.config.onlyEquipment) {
/* 23 */         shouldRender = ItemHelper.isEquipmentItem(itemStack);
/*    */       }
/*    */       
/* 26 */       if (LootBeams.config.onlyRare) {
/* 27 */         shouldRender = RarityHelper.rarityCheck(itemStack, shouldRender);
/*    */       }
/*    */       
/* 30 */       if (ItemHelper.isItemInRegistryList(LootBeams.config.whitelist, itemStack.method_7909())) {
/* 31 */         shouldRender = true;
/*    */       }
/*    */     } 
/*    */     
/* 35 */     if (ItemHelper.isItemInRegistryList(LootBeams.config.blacklist, itemStack.method_7909())) {
/* 36 */       shouldRender = false;
/*    */     }
/*    */     
/* 39 */     return shouldRender;
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean shouldRenderCrosshair(class_310 client) {
/* 44 */     if (LootBeams.config.renderTooltipsInThirdPersonView) {
/* 45 */       return ((client.field_1690
/* 46 */         .method_31044() == class_5498.field_26666 || client.field_1690
/* 47 */         .method_31044() == class_5498.field_26665 || client.field_1690
/* 48 */         .method_31044().method_31034()) && !client.field_1690.field_1842);
/*    */     }
/*    */     
/* 51 */     return (client.field_1690
/* 52 */       .method_31044().method_31034() && !client.field_1690.field_1842);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void cancelBobView(Vector3f position, float partialTicks) {
/* 58 */     class_310 mc = class_310.method_1551();
/* 59 */     if (((Boolean)mc.field_1690.method_42448().method_41753()).booleanValue() && mc.method_1560() instanceof class_1657) {
/* 60 */       class_1657 player = (class_1657)mc.method_1560();
/* 61 */       float playerStep = player.field_5973 - player.field_6039;
/* 62 */       float stepSize = -(player.field_5973 + playerStep * partialTicks);
/* 63 */       float viewBob = class_3532.method_16439(partialTicks, player.field_7505, player.field_7483);
/*    */       
/* 65 */       Quaternionf bobXRotation = class_7833.field_40714.rotationDegrees(Math.abs(class_3532.method_15362(stepSize * 3.1415927F - 0.2F) * viewBob) * 5.0F);
/* 66 */       Quaternionf bobZRotation = class_7833.field_40718.rotationDegrees(class_3532.method_15374(stepSize * 3.1415927F) * viewBob * 3.0F);
/* 67 */       bobXRotation.conjugate();
/* 68 */       bobZRotation.conjugate();
/* 69 */       bobXRotation.transform(position);
/* 70 */       bobZRotation.transform(position);
/* 71 */       position.add(-class_3532.method_15374(stepSize * 3.1415927F) * viewBob * 0.5F, Math.abs(class_3532.method_15362(stepSize * 3.1415927F) * viewBob), 0.0F);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Vector3f worldToScreenSpace(class_243 pos, float partialTicks) {
/* 76 */     class_310 mc = class_310.method_1551();
/* 77 */     class_4184 camera = mc.field_1773.method_19418();
/* 78 */     class_243 cameraPosition = camera.method_19326();
/*    */     
/* 80 */     Vector3f position = new Vector3f((float)(cameraPosition.field_1352 - pos.field_1352), (float)(cameraPosition.field_1351 - pos.field_1351), (float)(cameraPosition.field_1350 - pos.field_1350));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 85 */     Quaternionf cameraRotation = camera.method_23767();
/* 86 */     cameraRotation.conjugate();
/*    */     
/* 88 */     cameraRotation.transform(position);
/*    */ 
/*    */     
/* 91 */     cancelBobView(position, partialTicks);
/*    */     
/* 93 */     class_1041 window = mc.method_22683();
/* 94 */     float screenSize = window.method_4502() / 2.0F / position.z() / (float)Math.tan(Math.toRadians(mc.field_1773.method_3196(camera, partialTicks, true) / 2.0D));
/* 95 */     position.mul(-screenSize, screenSize, 1.0F);
/* 96 */     position.add(window.method_4486() / 2.0F, window.method_4502() / 2.0F, 0.0F);
/*    */     
/* 98 */     return position;
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\ViewHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */