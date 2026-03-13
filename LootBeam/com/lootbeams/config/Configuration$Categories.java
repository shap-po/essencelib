package com.lootbeams.config;

import com.lootbeams.dconfig.DynamicConfig.Category;

public class Categories {
  @Category(name = "Loot Beams", key = "LootBeams", root = true)
  public static final String MAIN = "main";
  
  @Category(name = "Ground glow", key = "GroundGlow")
  public static final String GROUND_GLOW = "groundGlow";
  
  @Category(name = "Particles", key = "Particles")
  public static final String PARTICLES = "particles";
  
  @Category(name = "Items", key = "Items")
  public static final String ITEMS = "items";
  
  @Category(name = "Nametags", key = "Nametags")
  public static final String NAMETAGS = "nametags";
  
  @Category(name = "Presets", key = "Presets", display = false)
  public static final String PRESETS = "presets";
}


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\config\Configuration$Categories.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */