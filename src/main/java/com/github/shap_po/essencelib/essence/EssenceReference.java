package com.github.shap_po.essencelib.essence;

import net.minecraft.util.Identifier;

import java.util.Optional;

public record EssenceReference(Identifier id) {
    public Optional<Essence> getOptionalEssence() {
        return EssenceManager.getOptional(id);
    }
}
