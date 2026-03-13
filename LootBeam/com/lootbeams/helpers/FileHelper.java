/*    */ package com.lootbeams.helpers;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ 
/*    */ public class FileHelper {
/*    */   public static File createPathIfNotExists(String filePath) {
/* 11 */     Path path = Paths.get(filePath, new String[0]);
/*    */     
/* 13 */     if (Files.notExists(path.getParent(), new java.nio.file.LinkOption[0])) {
/*    */       try {
/* 15 */         Files.createDirectories(path.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
/* 16 */       } catch (IOException e) {
/* 17 */         e.printStackTrace();
/*    */       } 
/*    */     }
/*    */     
/* 21 */     if (Files.notExists(path, new java.nio.file.LinkOption[0])) {
/*    */       try {
/* 23 */         Files.createFile(path, (FileAttribute<?>[])new FileAttribute[0]);
/* 24 */       } catch (IOException e) {
/* 25 */         e.printStackTrace();
/*    */       } 
/*    */     }
/*    */     
/* 29 */     return path.toFile();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\helpers\FileHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */