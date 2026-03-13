/*     */ package com.lootbeams.config;
/*     */ 
/*     */ import com.lootbeams.dconfig.DynamicConfig.Category;
/*     */ import com.lootbeams.dconfig.DynamicConfig.Field;
/*     */ import com.lootbeams.features.BeamOpacityOnApproach;
/*     */ import com.lootbeams.features.BeamSizeOnApproach;
/*     */ import com.lootbeams.managers.GlowEffectManager;
/*     */ import com.lootbeams.managers.ParticleManager;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Configuration
/*     */ {
/*     */   public static class Categories
/*     */   {
/*     */     @Category(name = "Loot Beams", key = "LootBeams", root = true)
/*     */     public static final String MAIN = "main";
/*     */     @Category(name = "Ground glow", key = "GroundGlow")
/*     */     public static final String GROUND_GLOW = "groundGlow";
/*     */     @Category(name = "Particles", key = "Particles")
/*     */     public static final String PARTICLES = "particles";
/*     */     @Category(name = "Items", key = "Items")
/*     */     public static final String ITEMS = "items";
/*     */     @Category(name = "Nametags", key = "Nametags")
/*     */     public static final String NAMETAGS = "nametags";
/*     */     @Category(name = "Presets", key = "Presets", display = false)
/*     */     public static final String PRESETS = "presets";
/*     */   }
/*     */   
/*     */   public static class Groups
/*     */   {
/*     */     public static final String VISUAL = "visual";
/*     */   }
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean renderNameColor = true;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean renderRarityColor = true;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean renderBeam = true;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean renderDroplightBeam = false;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean animateDroplightBeam = false;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 5.0D)
/*  49 */   public float droplightBeamAnimationSpeed = 1.0F;
/*     */   
/*     */   @Field(category = "main", group = "visual")
/*  52 */   public List<String> beamGradientModifiers = List.of("-h50", "+s35", "-v50");
/*     */   
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 100.0D)
/*  55 */   public float smoothDuration = 3.0F; @Field(category = "main", group = "visual")
/*     */   public boolean smoothBeamSize = true; @Field(category = "main", group = "visual", min = 0.0D, max = 5.0D)
/*  57 */   public float beamRadius = 0.55F;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 5.0D)
/*  59 */   public float minBeamRadius = 0.0F;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 24.0D)
/*  61 */   public float beamHeight = 3.0F;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 24.0D)
/*  63 */   public float minBeamHeight = 0.0F;
/*     */   @Field(category = "main", group = "visual", min = -30.0D, max = 30.0D)
/*  65 */   public float beamYOffset = 0.0F;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean commonShorterBeam = true;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 1.0D)
/*  69 */   public float beamAlpha = 0.75F;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 1.0D)
/*  71 */   public float minBeamAlpha = 0.0F;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean solidBeam = true;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean whiteCenter = true;
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean glowingBeam = true;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 128.0D)
/*  79 */   public float renderDistance = 24.0F;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 128.0D)
/*  81 */   public float changeDistance = 16.0F;
/*     */   @Field(category = "main", group = "visual", min = 0.0D, max = 128.0D)
/*  83 */   public float changeOffset = 2.0F;
/*     */   @Field(category = "main", group = "visual")
/*  85 */   public BeamOpacityOnApproach beamOpacityOnApproach = BeamOpacityOnApproach.FADE_IN;
/*     */   @Field(category = "main", group = "visual")
/*  87 */   public BeamSizeOnApproach beamSizeOnApproach = BeamSizeOnApproach.DISABLED;
/*     */   
/*     */   @Field(category = "main", group = "visual")
/*     */   public boolean requireOnGround = true;
/*     */   
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean glowEffect = true;
/*     */   @Field(category = "groundGlow", group = "visual")
/*  95 */   public LootBeamShaders.CustomShader glowCustomShader = LootBeamShaders.CustomShader.NONE;
/*     */   
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean useGlowGradient = false;
/*     */   @Field(category = "groundGlow", group = "visual")
/* 100 */   public List<String> glowGradientModifiers = List.of("-h50", "+s35", "-v50"); @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 100.0D)
/* 101 */   public float glowGradientStart = 0.0F;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 100.0D)
/* 103 */   public float glowGradientEnd = 100.0F;
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean smoothGlowEffectRadius = false;
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean smoothGlowEffectAlpha = true;
/*     */   @Field(category = "groundGlow", group = "visual")
/* 109 */   public GlowEffectManager.GlowEffectTexture glowEffectTexture = GlowEffectManager.GLOW_TEXTURE;
/*     */   @Field(category = "groundGlow", group = "visual", min = 1.0E-5D, max = 3.0D)
/* 111 */   public float glowEffectRadius = 0.5F;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 1.0D)
/* 113 */   public float glowEffectAlpha = 0.8F;
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean pulseGlow = true;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 10.0D)
/* 117 */   public float pulseGlowSpeed = 1.0F;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 1.0D)
/* 119 */   public float pulseGlowMinAlpha = 0.8F;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 1.0D)
/* 121 */   public float pulseGlowMaxAlpha = 1.0F;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 3.0D)
/* 123 */   public float pulseGlowMinRadius = 0.5F;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 3.0D)
/* 125 */   public float pulseGlowMaxRadius = 0.625F;
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean rotateGlow = false;
/*     */   @Field(category = "groundGlow", group = "visual")
/*     */   public boolean glowRotateClockwise = true;
/*     */   @Field(category = "groundGlow", group = "visual", min = 0.0D, max = 50.0D)
/* 131 */   public float glowRotationSpeed = 1.0F;
/*     */   
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean particles = true;
/*     */   
/*     */   @Field(category = "particles", group = "visual")
/* 137 */   public ParticleManager.ParticleTexture particleTexture = ParticleManager.GLOW_TEXTURE;
/*     */   @Field(category = "particles", group = "visual")
/* 139 */   public LootBeamShaders.Shader particleColorMode = LootBeamShaders.Shader.PARTICLE_OVERLAY;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean particleInheritsColor = true;
/*     */   @Field(category = "particles", group = "visual", min = 0.0D, max = 20.0D)
/* 143 */   public int tickPerParticleSpriteUpdate = 1;
/*     */   @Field(category = "particles", group = "visual", min = 1.0D, max = 20.0D)
/* 145 */   public float particleCount = 5.0F;
/*     */   @Field(category = "particles", group = "visual", min = 1.0D, max = 100.0D)
/* 147 */   public int particleLifetime = 15;
/*     */   @Field(category = "particles", group = "visual", min = 1.0E-5D, max = 10.0D)
/* 149 */   public float particleSize = 0.25F;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean particleRandomSize = true;
/*     */   @Field(category = "particles", group = "visual", min = 1.0E-5D, max = 10.0D)
/* 153 */   public float particleRadius = 0.1F;
/*     */   @Field(category = "particles", group = "visual", min = -35.0D, max = 35.0D)
/* 155 */   public float particleYOffset = 0.0F;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean particleRandomY = true;
/*     */   @Field(category = "particles", group = "visual", min = 1.0E-5D, max = 10.0D)
/* 159 */   public float particleSpeed = 0.2F;
/*     */   @Field(category = "particles", group = "visual", min = 0.0D, max = 10.0D)
/* 161 */   public float particleSpeedX = 0.2F;
/*     */   @Field(category = "particles", group = "visual", min = 0.0D, max = 10.0D)
/* 163 */   public float particleSpeedY = 0.01F;
/*     */   @Field(category = "particles", group = "visual", min = 0.0D, max = 10.0D)
/* 165 */   public float particleSpeedZ = 0.2F;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean particleUseConstantVerticalSpeed = false;
/*     */   @Field(category = "particles", group = "visual", min = 0.0D, max = 1.0D)
/* 169 */   public float randomnessIntensity = 0.05F;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean particleRareOnly = true;
/*     */   @Field(category = "particles", min = 0.0D, max = 1.0D)
/* 173 */   public float particleDirectionX = 0.0F;
/*     */   @Field(category = "particles", min = 0.0D, max = 1.0D)
/* 175 */   public float particleDirectionY = 1.0F;
/*     */   @Field(category = "particles", min = 0.0D, max = 1.0D)
/* 177 */   public float particleDirectionZ = 0.0F;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean spinAroundBeam = true;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean trails = true;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean trailParticlesInvisible = true;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean trailUseScale = true;
/*     */   @Field(category = "particles", group = "visual")
/*     */   public boolean trailScaleHeightEqualsBeamHeight = true;
/*     */   @Field(category = "particles", group = "visual", min = 1.0E-5D, max = 24.0D)
/* 189 */   public float trailScaleHeight = 3.0F;
/*     */   @Field(category = "particles", group = "visual", min = 0.0D, max = 1.0D)
/* 191 */   public float trailChance = 0.4F;
/*     */   @Field(category = "particles", group = "visual", min = 1.0E-5D, max = 10.0D)
/* 193 */   public float trailWidth = 0.2F;
/*     */   @Field(category = "particles", group = "visual", min = 1.0D, max = 200.0D)
/* 195 */   public int trailLength = 30;
/*     */   @Field(category = "particles", group = "visual", min = 1.0D, max = 200.0D)
/* 197 */   public int trailFrequency = 1;
/*     */   
/*     */   @Field(category = "items", group = "visual")
/*     */   public boolean itemsGlow = false;
/*     */   
/*     */   @Field(category = "items")
/*     */   public boolean allItems = false;
/*     */   @Field(category = "items")
/*     */   public boolean onlyEquipment = true;
/*     */   @Field(category = "items")
/*     */   public boolean onlyRare = true;
/*     */   @Field(category = "items")
/* 209 */   public List<String> whitelist = new ArrayList<>();
/*     */   @Field(category = "items")
/* 211 */   public List<String> blacklist = new ArrayList<>();
/*     */   @Field(category = "items")
/* 213 */   public List<String> colorOverrides = new ArrayList<>();
/*     */   
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean advancedTooltips = true;
/*     */   
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean worldspaceTooltips = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean itemFrameTooltips = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean renderTooltipsInThirdPersonView = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean borders = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean renderNametags = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean renderNametagsOnlook = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean renderItemRarity = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean renderItemRarityInTooltip = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean renderStackcount = true;
/*     */   @Field(category = "nametags", min = 0.0D, max = 5.0D)
/* 237 */   public float nametagLookSensitivity = 0.018F;
/*     */   @Field(category = "nametags", group = "visual", min = 0.0D, max = 1.0D)
/* 239 */   public float nametagTextAlpha = 1.0F;
/*     */   @Field(category = "nametags", group = "visual", min = 0.0D, max = 1.0D)
/* 241 */   public float nametagBackgroundAlpha = 0.5F;
/*     */   @Field(category = "nametags", group = "visual", min = -10.0D, max = 10.0D)
/* 243 */   public float nametagScale = 1.0F;
/*     */   @Field(category = "nametags", group = "visual", min = -30.0D, max = 30.0D)
/* 245 */   public float nametagYOffset = 0.75F;
/*     */   @Field(category = "nametags")
/*     */   public boolean whiteRarities = false;
/*     */   @Field(category = "nametags")
/*     */   public boolean vanillaRarities = false;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean screenTooltipsRequireCrouch = true;
/*     */   @Field(category = "nametags", group = "visual")
/*     */   public boolean combineNameAndRarity = false;
/*     */   @Field(category = "nametags")
/* 255 */   public List<String> customRarities = new ArrayList<>();
/*     */   
/*     */   @Field(category = "nametags")
/* 258 */   public List<String> alwaysDrawRaritiesOn = List.of(new String[] { "#minecraft:music_discs" });
/*     */   
/*     */   @Field(category = "presets")
/* 261 */   public String selectedPreset = "";
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\config\Configuration.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */