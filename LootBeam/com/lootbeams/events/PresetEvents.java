/*   */ package com.lootbeams.events;
/*   */ 
/*   */ import net.fabricmc.fabric.api.event.Event;
/*   */ import net.fabricmc.fabric.api.event.EventFactory;
/*   */ 
/*   */ public interface PresetEvents {
/* 7 */   public static final Event<ApplyPreset> APPLY_PRESET = EventFactory.createArrayBacked(ApplyPreset.class, listeners -> ());
/*   */   
/*   */   public static interface ApplyPreset {
/*   */     void onApplyPreset(String param1String);
/*   */   }
/*   */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\events\PresetEvents.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */