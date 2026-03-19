package com.github.shap_po.essencelib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum CollectorIntuitionGlowMode {
    PLAYER_THROUGH_WALLS_CUSTOM("player_through_walls_custom"),
    PLAYER_NOT_THROUGH_WALLS_CUSTOM("player_not_through_walls_custom"),
    EVERYONE_NOT_THROUGH_WALLS_CUSTOM("everyone_not_through_walls_custom"),
    DEFAULT_MINECRAFT_GLOW("default_minecraft_glow");

    private final String id;

    CollectorIntuitionGlowMode(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static CollectorIntuitionGlowMode current() {
        String raw = ClientAbilityHookConfig.get().collectorIntuitionGlowMode();
        for (CollectorIntuitionGlowMode mode : values()) {
            if (mode.id.equals(raw)) return mode;
        }
        return PLAYER_THROUGH_WALLS_CUSTOM;
    }
}
