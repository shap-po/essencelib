package com.github.shap_po.essencelib.attribute;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ManaAttributes {
    public static final EntityAttribute MAX_MANA = Registry.register(
        Registries.ATTRIBUTE,
        EssenceLib.identifier("max_mana"),
        new ClampedEntityAttribute("attribute.essencelib.max_mana", 100.0, 0.0, 1000.0).setTracked(true)
    );

    public static final EntityAttribute MANA_REGEN = Registry.register(
        Registries.ATTRIBUTE,
        EssenceLib.identifier("mana_regen"),
        new ClampedEntityAttribute("attribute.essencelib.mana_regen", 1.0, 0.0, 50.0).setTracked(true)
    );

    public static void register() {
        // Registration handled by static initializers
    }
} 