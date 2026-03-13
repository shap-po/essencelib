package com.github.shap_po.essencelib.client;

/**
 * Holds mana values received from server.
 * Display values lerp toward actual values for smooth HUD animation.
 */
public class ClientManaData {
    private static float currentMana;
    private static float maxMana;

    /** Smoothed display value - lerps toward currentMana each frame */
    private static float displayMana = -1;
    /** Smoothed display max - lerps toward maxMana each frame */
    private static float displayMax = -1;

    private static final float LERP_SPEED = 0.2f;

    public static void set(float current, float max) {
        currentMana = current;
        maxMana = max;
        if (displayMana < 0) displayMana = current;
        if (displayMax < 0) displayMax = max;
    }

    /** Call each frame to update smoothed display values */
    public static void tick() {
        if (displayMana < 0) displayMana = currentMana;
        if (displayMax < 0) displayMax = maxMana;
        displayMana += (currentMana - displayMana) * LERP_SPEED;
        displayMax += (maxMana - displayMax) * LERP_SPEED;
    }

    /** Returns smoothed mana for HUD (smooth movement) */
    public static float getCurrentMana() {
        return displayMana >= 0 ? displayMana : currentMana;
    }

    /** Returns smoothed max for HUD */
    public static float getMaxMana() {
        return displayMax >= 0 ? displayMax : maxMana;
    }
} 