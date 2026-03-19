package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import net.minecraft.item.SpawnEggItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.awt.Color;

/**
 * Resolves a stable RGB color for an essence for rendering (orb, particles).
 * Uses a deterministic hue from the essence id so each mob essence has a distinct,
 * mysterious glow. Spawn-egg-based colors can be added later via data or accessor.
 */
@Environment(EnvType.CLIENT)
public final class EssenceColorHelper {

    /** Default tint when essence is unknown: warm otherworldly glow. */
    private static final int DEFAULT_R = 255, DEFAULT_G = 140, DEFAULT_B = 180;

    /**
     * Returns 0xRRGGBB (opaque) for the given essence id.
     * Safe to call with null; returns default color.
     */
    public static int getRgb(Identifier essenceId) {
        if (essenceId == null) return (DEFAULT_R << 16) | (DEFAULT_G << 8) | DEFAULT_B;
        
        Essence essence = EssenceManager.getNullable(essenceId);
        if (essence != null && essence.getColor() != -1) {
            return essence.getColor();
        }

        // Prefer the mob's spawn-egg primary tint when essence JSON does not define color.
        if (essence != null && essence.getDroppedBy() != null) {
            SpawnEggItem egg = SpawnEggItem.forEntity(essence.getDroppedBy());
            if (egg != null) {
                return egg.getColor(0) & 0xFFFFFF;
            }
        }
        
        return idToRgb(essenceId);
    }

    /**
     * Returns two RGB entries [primary, secondary] suitable for animated gradients.
     * Prefers spawn egg colors when available; otherwise derives a stable pair from the base RGB.
     */
    public static int[] getEggGradientRgb(Identifier essenceId) {
        if (essenceId != null) {
            Essence essence = EssenceManager.getNullable(essenceId);
            if (essence != null && essence.getDroppedBy() != null) {
                SpawnEggItem egg = SpawnEggItem.forEntity(essence.getDroppedBy());
                if (egg != null) {
                    int primary = egg.getColor(0) & 0xFFFFFF;
                    int secondary = egg.getColor(1) & 0xFFFFFF;
                    if (primary == secondary) {
                        secondary = tintTowardsWhite(primary, 0.24f);
                    }
                    return new int[] { primary, secondary };
                }
            }
        }

        int base = getRgb(essenceId);
        return new int[] { base, tintTowardsWhite(base, 0.24f) };
    }

    /** Unpacks R from 0xRRGGBB. */
    public static int getR(int rgb) { return (rgb >> 16) & 0xFF; }
    /** Unpacks G from 0xRRGGBB. */
    public static int getG(int rgb) { return (rgb >> 8) & 0xFF; }
    /** Unpacks B from 0xRRGGBB. */
    public static int getB(int rgb) { return rgb & 0xFF; }

    /** Stable color from essence id: broad hue fallback when no explicit or egg tint is available. */
    private static int idToRgb(Identifier id) {
        int hash = id.hashCode();
        float hue = ((hash & 0xFFFF) / 65535.0f);
        float sat = 0.65f + ((hash >> 16) & 0xFF) / 255.0f * 0.25f;
        float bri = 0.82f + ((hash >> 24) & 0xFF) / 255.0f * 0.16f;
        return Color.HSBtoRGB(hue % 1.0f, Math.min(1f, sat), Math.min(1f, bri)) & 0xFFFFFF;
    }

    private static int tintTowardsWhite(int rgb, float amount) {
        amount = Math.max(0.0f, Math.min(1.0f, amount));
        int r = getR(rgb);
        int g = getG(rgb);
        int b = getB(rgb);
        int nr = Math.min(255, (int) (r + (255 - r) * amount));
        int ng = Math.min(255, (int) (g + (255 - g) * amount));
        int nb = Math.min(255, (int) (b + (255 - b) * amount));
        return (nr << 16) | (ng << 8) | nb;
    }

    private EssenceColorHelper() {}
}
