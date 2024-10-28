package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
    public static final TagKey<EntityType<?>> ENTITY_IGNORELIST = TagKey.of(RegistryKeys.ENTITY_TYPE, EssenceLib.identifier("entity_ignorelist"));
}
