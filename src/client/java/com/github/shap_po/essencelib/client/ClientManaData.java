package com.github.shap_po.essencelib.client;

public class ClientManaData {
    private static float currentMana;
    private static float maxMana;

    public static void set(float current, float max) {
        currentMana = current;
        maxMana = max;
    }

    public static float getCurrentMana() {
        return currentMana;
    }

    public static float getMaxMana() {
        return maxMana;
    }
} 