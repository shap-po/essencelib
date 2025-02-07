package com.github.shap_po.essencelib.attribute;

import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;

public enum ManaAttributeType {
    MAX_MANA(ManaAttributeRegistry.getMaxMana()),
    CURRENT_MANA(ManaAttributeRegistry.getCurrentMana()),
    MANA_REGEN(ManaAttributeRegistry.getManaRegen());

    private final EntityAttribute attribute;

    ManaAttributeType(EntityAttribute attribute) {
        this.attribute = attribute;
    }

    public EntityAttribute getAttribute() {
        return attribute;
    }

    public static void initialize() {
        // Registration handled by static initializers and enum construction
    }
}
