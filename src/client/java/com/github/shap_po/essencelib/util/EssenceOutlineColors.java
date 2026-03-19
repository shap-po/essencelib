package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Loads outline/glow colors for Collector's Intuition from essence_outline_colors.json. */
@Environment(EnvType.CLIENT)
public final class EssenceOutlineColors {

    private static int uncollectedR = 255, uncollectedG = 200, uncollectedB = 100;
    private static int collectedR = 255, collectedG = 255, collectedB = 255;

    public static void load() {
        try (var stream = EssenceOutlineColors.class.getResourceAsStream(
            "/data/essencelib/essence_outline_colors.json")) {
            if (stream == null) return;
            JsonObject root = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) return;
            if (root.has("uncollected")) {
                JsonObject o = root.getAsJsonObject("uncollected");
                uncollectedR = get(o, "r", 255);
                uncollectedG = get(o, "g", 200);
                uncollectedB = get(o, "b", 100);
            }
            if (root.has("collected")) {
                JsonObject o = root.getAsJsonObject("collected");
                collectedR = get(o, "r", 255);
                collectedG = get(o, "g", 255);
                collectedB = get(o, "b", 255);
            }
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load essence outline colors: {}", e.getMessage());
        }
    }

    private static int get(JsonObject o, String key, int def) {
        if (!o.has(key)) return def;
        return Math.max(0, Math.min(255, o.get(key).getAsInt()));
    }

    /** Default color when only Collector's Intuition (no collected/uncollected distinction). */
    public static int getDefaultArgb() {
        return 0xFF << 24 | (uncollectedR << 16) | (uncollectedG << 8) | uncollectedB;
    }

    /** Uncollected (new) items - warm yellow-orange. Only when player has both abilities. */
    public static int getUncollectedArgb() {
        return 0x60 << 24 | (uncollectedR << 16) | (uncollectedG << 8) | uncollectedB;
    }

    /** Collected items - white. Only when player has both abilities. */
    public static int getCollectedArgb() {
        return 0x40 << 24 | (collectedR << 16) | (collectedG << 8) | collectedB;
    }

    public static int getUncollectedR() { return uncollectedR; }
    public static int getUncollectedG() { return uncollectedG; }
    public static int getUncollectedB() { return uncollectedB; }
    public static int getCollectedR() { return collectedR; }
    public static int getCollectedG() { return collectedG; }
    public static int getCollectedB() { return collectedB; }
}
