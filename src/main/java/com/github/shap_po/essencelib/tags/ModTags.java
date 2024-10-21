package com.github.shap_po.essencelib.tags;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Item> Soul_Essence = TagKey.of(RegistryKeys.ITEM, Identifier.of("trinkets", "soul/essence"));
}
