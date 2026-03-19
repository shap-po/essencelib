package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-side registry mapping power IDs to tooltip slots (active / passive / lifestyle).
 * Used to pick one random slot when applying essence to an item. Loaded from data packs.
 */
public final class PowerSlotRegistry {

    public static final String SLOT_ACTIVE = "active";
    public static final String SLOT_PASSIVE = "passive";
    public static final String SLOT_LIFESTYLE = "lifestyle";

    private static final Map<String, String> SLOTS = new HashMap<>();
    private static final Gson GSON = new Gson();

    /**
     * Load slot mappings from resource manager. Call from EssenceManager or a reload listener.
     */
    public static void load(ResourceManager manager) {
        SLOTS.clear();
        Identifier path = Identifier.of("essencelib", "essence_tooltip_slots.json");
        try {
            List<net.minecraft.resource.Resource> resources = manager.getAllResources(path);
            for (net.minecraft.resource.Resource resource : resources) {
                try (var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject root = GSON.fromJson(reader, JsonObject.class);
                    if (root != null) {
                        for (var e : root.entrySet()) {
                            if (e.getValue().isJsonPrimitive()) {
                                String v = e.getValue().getAsString();
                                if (SLOT_ACTIVE.equals(v) || SLOT_PASSIVE.equals(v) || SLOT_LIFESTYLE.equals(v)) {
                                    SLOTS.put(e.getKey(), v);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load essence_tooltip_slots.json: {}", e.getMessage());
        }
    }

    public static String getSlot(Identifier powerId) {
        return powerId != null ? SLOTS.getOrDefault(powerId.toString(), null) : null;
    }

    public static boolean hasSlot(Identifier powerId) {
        String slot = getSlot(powerId);
        return SLOT_ACTIVE.equals(slot) || SLOT_PASSIVE.equals(slot) || SLOT_LIFESTYLE.equals(slot);
    }
}
