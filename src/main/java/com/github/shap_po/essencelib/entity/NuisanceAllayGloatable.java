package com.github.shap_po.essencelib.entity;

/**
 * Interface for Allay entities that can show a synced "gloating" state (e.g. Nuisance Allay showing stolen item).
 * Used for client animation sync; implementation is in AllayEntityMixin.
 */
public interface NuisanceAllayGloatable {

    void setNuisanceGloating(boolean gloating);

    boolean isNuisanceGloating();
}
