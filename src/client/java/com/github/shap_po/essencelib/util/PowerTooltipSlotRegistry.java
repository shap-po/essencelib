package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps power IDs to tooltip display slots. Powers with "active" or "passive" are
 * shown; others are hidden. Add essencelib:tooltip_slot to power JSON or use this
 * registry. Data from data/essencelib/essence_tooltip_slots.json.
 */
@Environment(EnvType.CLIENT)
public final class PowerTooltipSlotRegistry {

    /** active = shown under Active, passive = shown under Passive, lifestyle = Lifestyle (drawbacks), null = hidden */
    public static final String SLOT_ACTIVE = "active";
    public static final String SLOT_PASSIVE = "passive";
    public static final String SLOT_LIFESTYLE = "lifestyle";

    private static Map<String, String> SLOTS = null;

    public static String getSlot(Identifier powerId) {
        if (SLOTS == null) load();
        return powerId != null ? SLOTS.get(powerId.toString()) : null;
    }

    public static boolean hasSlot(Identifier powerId) {
        String slot = getSlot(powerId);
        return SLOT_ACTIVE.equals(slot) || SLOT_PASSIVE.equals(slot) || SLOT_LIFESTYLE.equals(slot);
    }

    private static synchronized void load() {
        if (SLOTS != null) return;
        Map<String, String> map = new HashMap<>();
        try (var stream = PowerTooltipSlotRegistry.class.getResourceAsStream(
            "/data/essencelib/essence_tooltip_slots.json")) {
            if (stream != null) {
                JsonObject root = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                if (root != null) {
                    for (var e : root.entrySet()) {
                        if (e.getValue().isJsonPrimitive()) {
                            String v = e.getValue().getAsString();
                            if (SLOT_ACTIVE.equals(v) || SLOT_PASSIVE.equals(v) || SLOT_LIFESTYLE.equals(v)) {
                                map.put(e.getKey(), v);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load essence tooltip slot mapping: {}", e.getMessage());
        }
        SLOTS = map;
    }
}
