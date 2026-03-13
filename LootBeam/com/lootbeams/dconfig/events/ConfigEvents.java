/*    */ package com.lootbeams.dconfig.events;
/*    */ 
/*    */ public interface ConfigEvents {
/*  4 */   public static final EventFactory.Event<ConfigSave> SAVE = EventFactory.createArrayBacked(ConfigSave.class, listeners -> ());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 10 */   public static final EventFactory.Event<ConfigChange> CHANGE = EventFactory.createArrayBacked(ConfigChange.class, listeners -> ());
/*    */   
/*    */   public static interface ConfigChange {
/*    */     void onConfigChange();
/*    */   }
/*    */   
/*    */   public static interface ConfigSave {
/*    */     void onConfigSave();
/*    */   }
/*    */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\dconfig\events\ConfigEvents.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */