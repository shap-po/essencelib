/*     */ package com.lootbeams.compat.modmenu;
/*     */ import com.lootbeams.LootBeams;
/*     */ import com.lootbeams.dconfig.DynamicConfig;
/*     */ import com.lootbeams.features.BeamOpacityOnApproach;
/*     */ import com.lootbeams.features.BeamSizeOnApproach;
/*     */ import com.lootbeams.managers.GlowEffectManager;
/*     */ import com.lootbeams.managers.ParticleManager;
/*     */ import com.lootbeams.shaders.LootBeamShaders;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
/*     */ import me.shedaniel.clothconfig2.api.ConfigBuilder;
/*     */ import me.shedaniel.clothconfig2.api.ConfigCategory;
/*     */ import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_437;
/*     */ 
/*     */ public class LootBeamsConfigScreen {
/*     */   public static Map<Class<?>, TriConsumer<ConfigBuilder, ConfigCategory, DynamicConfig.Control.Field>> FIELD_BUILDERS;
/*     */   
/*     */   static {
/*  23 */     FIELD_BUILDERS = Map.of(boolean.class, (builder, group, field) -> { boolean defaultValue = ((Boolean)field.defaultValue).booleanValue(); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle((class_2561)class_2561.method_43471(field.name), ((Boolean)LootBeams.configManager.getFieldValue(field.key)).booleanValue()).setDefaultValue(defaultValue).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).setSaveConsumer(()).build()); }int.class, (builder, group, field) -> { int defaultValue = ((Integer)field.defaultValue).intValue(); int minValue = ((Integer)field.minValue).intValue(); int maxValue = ((Integer)field.maxValue).intValue(); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((class_2561)class_2561.method_43471(field.name), ((Integer)LootBeams.configManager.getFieldValue(field.key)).intValue()).setDefaultValue(defaultValue).setMin(minValue).setMax(maxValue).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).setSaveConsumer(()).build()); }float.class, (builder, group, field) -> { float defaultValue = ((Float)field.defaultValue).floatValue(); float minValue = ((Float)field.minValue).floatValue(); float maxValue = ((Float)field.maxValue).floatValue(); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startFloatField((class_2561)class_2561.method_43471(field.name), ((Float)LootBeams.configManager.getFieldValue(field.key)).floatValue()).setDefaultValue(defaultValue).setMin(minValue).setMax(maxValue).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).setSaveConsumer(()).build()); }List.class, (builder, group, field) -> { List<String> defaultValue = (List<String>)field.defaultValue; group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startStrList((class_2561)class_2561.method_43471(field.name), (List)LootBeams.configManager.getFieldValue(field.key)).setDefaultValue(defaultValue).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).setSaveConsumer(()).build()); }ParticleManager.ParticleTexture.class, (builder, group, field) -> { ParticleManager.ParticleTexture defaultValue = (ParticleManager.ParticleTexture)field.defaultValue; ParticleManager.ParticleTexture currentValue = (ParticleManager.ParticleTexture)LootBeams.configManager.getFieldValue(field.key); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startDropdownMenu((class_2561)class_2561.method_43471(field.name), AnimatedTextureCellCreator.topParticleCell(currentValue), AnimatedTextureCellCreator.selectionParticleCell(20, 112, 6)).setDefaultValue(defaultValue).setSelections(ParticleManager.getTextures()).setSuggestionMode(false).setSaveConsumer(()).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).build()); }GlowEffectManager.GlowEffectTexture.class, (builder, group, field) -> { GlowEffectManager.GlowEffectTexture defaultValue = (GlowEffectManager.GlowEffectTexture)field.defaultValue; GlowEffectManager.GlowEffectTexture currentValue = (GlowEffectManager.GlowEffectTexture)LootBeams.configManager.getFieldValue(field.key); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startDropdownMenu((class_2561)class_2561.method_43471(field.name), AnimatedTextureCellCreator.topGlowEffectCell(currentValue), AnimatedTextureCellCreator.selectionGlowEffectCell(20, 112, 6)).setDefaultValue(defaultValue).setSelections(GlowEffectManager.getTextures()).setSuggestionMode(false).setSaveConsumer(()).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).build()); }LootBeamShaders.Shader.class, (builder, group, field) -> { LootBeamShaders.Shader defaultValue = (LootBeamShaders.Shader)field.defaultValue; LootBeamShaders.Shader currentValue = (LootBeamShaders.Shader)LootBeams.configManager.getFieldValue(field.key); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startDropdownMenu((class_2561)class_2561.method_43471(field.name), DropdownMenuBuilder.TopCellElementBuilder.of(currentValue.name().toLowerCase(), (), class_2561::method_43470)).setDefaultValue(defaultValue.name().toLowerCase()).setSelections(Arrays.<LootBeamShaders.Shader>stream(LootBeamShaders.Shader.values()).map(()).toList()).setSuggestionMode(false).setSaveConsumer(()).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).build()); }LootBeamShaders.CustomShader.class, (builder, group, field) -> { LootBeamShaders.CustomShader defaultValue = (LootBeamShaders.CustomShader)field.defaultValue; LootBeamShaders.CustomShader currentValue = (LootBeamShaders.CustomShader)LootBeams.configManager.getFieldValue(field.key); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startDropdownMenu((class_2561)class_2561.method_43471(field.name), DropdownMenuBuilder.TopCellElementBuilder.of(currentValue.name().toLowerCase(), (), class_2561::method_43470)).setDefaultValue(defaultValue.name().toLowerCase()).setSelections(LootBeamShaders.CustomShader.values().stream().map(()).toList()).setSuggestionMode(false).setSaveConsumer(()).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).build()); }BeamOpacityOnApproach.class, (builder, group, field) -> { BeamOpacityOnApproach defaultValue = (BeamOpacityOnApproach)field.defaultValue; BeamOpacityOnApproach currentValue = (BeamOpacityOnApproach)LootBeams.configManager.getFieldValue(field.key); group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startDropdownMenu((class_2561)class_2561.method_43471(field.name), DropdownMenuBuilder.TopCellElementBuilder.of(currentValue.name().toLowerCase(), (), class_2561::method_43470)).setDefaultValue(defaultValue.name().toLowerCase()).setSelections(Arrays.<BeamOpacityOnApproach>stream(BeamOpacityOnApproach.values()).map(()).toList()).setSuggestionMode(false).setSaveConsumer(()).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).build()); }BeamSizeOnApproach.class, (builder, group, field) -> {
/*     */           BeamSizeOnApproach defaultValue = (BeamSizeOnApproach)field.defaultValue;
/*     */           BeamSizeOnApproach currentValue = (BeamSizeOnApproach)LootBeams.configManager.getFieldValue(field.key);
/*     */           group.addEntry((AbstractConfigListEntry)builder.entryBuilder().startDropdownMenu((class_2561)class_2561.method_43471(field.name), DropdownMenuBuilder.TopCellElementBuilder.of(currentValue.name().toLowerCase(), (), class_2561::method_43470)).setDefaultValue(defaultValue.name().toLowerCase()).setSelections(Arrays.<BeamSizeOnApproach>stream(BeamSizeOnApproach.values()).map(()).toList()).setSuggestionMode(false).setSaveConsumer(()).setTooltip(new class_2561[] { (class_2561)class_2561.method_43471(field.description) }).build());
/*     */         });
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class_437 getConfigScreen(class_437 parent) {
/* 209 */     ConfigBuilder builder = ConfigBuilder.create();
/* 210 */     if (parent != null) builder.setParentScreen(parent); 
/* 211 */     builder.setTitle((class_2561)class_2561.method_43471("lootbeams.title"));
/*     */     
/* 213 */     Map<String, ConfigCategory> groups = new HashMap<>();
/*     */     
/* 215 */     for (DynamicConfig.Control.Field field : LootBeams.configManager.getFields()) {
/* 216 */       if (!field.displayOnConfigScreen) {
/*     */         continue;
/*     */       }
/* 219 */       if (!field.category.displayOnConfigScreen) {
/*     */         continue;
/*     */       }
/* 222 */       if (!groups.containsKey(field.category.key)) {
/* 223 */         groups.put(field.category.key, builder.getOrCreateCategory((class_2561)class_2561.method_43471("lootbeams.config." + field.category.key)));
/*     */       }
/* 225 */       ConfigCategory group = groups.get(field.category.key);
/* 226 */       if (FIELD_BUILDERS.containsKey(field.type)) {
/* 227 */         ((TriConsumer)FIELD_BUILDERS.get(field.type)).accept(builder, group, field);
/*     */       }
/*     */     } 
/*     */     
/* 231 */     builder.setSavingRunnable(() -> LootBeams.configManager.save());
/*     */ 
/*     */     
/* 234 */     return builder.build();
/*     */   }
/*     */ }


/* Location:              C:\Users\SdataG\Downloads\lootbeams-3.6.1-mc1.21-.1.jar!\com\lootbeams\compat\modmenu\LootBeamsConfigScreen.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */