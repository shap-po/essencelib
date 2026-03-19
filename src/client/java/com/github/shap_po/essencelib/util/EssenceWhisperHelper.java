package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data-driven whispers for unidentified essences.
 * Content is generated once (Gemini) and loaded from resource JSON.
 */
@Environment(EnvType.CLIENT)
public final class EssenceWhisperHelper {
    private static final String DEFAULT_LINE = "Its nature is veiled, and only a faint impression lingers.";
    private static final String DEFAULT_POWER_FEEL = "You feel a strange pull from this power, but its full nature stays hidden.";
    private static Map<String, String> POWER_FEELS = null;
    private static Map<String, List<String>> ESSENCE_POWER_ORDER = null;

    public static String getPowerFeel(Identifier powerId) {
        ensureLoaded();
        if (powerId == null) return DEFAULT_POWER_FEEL;
        String value = POWER_FEELS.get(powerId.toString());
        return (value == null || value.isBlank()) ? DEFAULT_POWER_FEEL : value;
    }

    public static List<String> buildEssenceFlavorLines(ItemStack stack) {
        ensureLoaded();
        Identifier essenceId = stack.get(ModDataComponentTypes.ESSENCE_ID);
        if (essenceId == null) {
            return List.of(DEFAULT_LINE);
        }

        List<String> lines = new ArrayList<>();
        List<String> orderedPowers = ESSENCE_POWER_ORDER.get(essenceId.toString());
        if (orderedPowers != null) {
            for (String powerId : orderedPowers) {
                String line = POWER_FEELS.get(powerId);
                if (line != null && !line.isBlank()) {
                    lines.add(line);
                }
            }
        }

        // Fallback to whatever powers are actually on this stack, in slot order.
        if (lines.isEmpty()) {
            TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
            if (powers != null) {
                List<String> active = new ArrayList<>();
                List<String> passive = new ArrayList<>();
                List<String> lifestyle = new ArrayList<>();
                var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
                for (var entry : entries) {
                    if (entry.hidden()) continue;
                    String line = POWER_FEELS.get(entry.powerId().toString());
                    if (line == null || line.isBlank()) continue;
                    String slot = PowerTooltipSlotRegistry.getSlot(entry.powerId());
                    if (PowerTooltipSlotRegistry.SLOT_ACTIVE.equals(slot)) active.add(line);
                    else if (PowerTooltipSlotRegistry.SLOT_PASSIVE.equals(slot)) passive.add(line);
                    else if (PowerTooltipSlotRegistry.SLOT_LIFESTYLE.equals(slot)) lifestyle.add(line);
                }
                lines.addAll(active);
                lines.addAll(passive);
                lines.addAll(lifestyle);
            }
        }

        return lines.isEmpty() ? List.of(DEFAULT_LINE) : lines;
    }

    private static synchronized void ensureLoaded() {
        if (POWER_FEELS != null && ESSENCE_POWER_ORDER != null) return;
        POWER_FEELS = new HashMap<>();
        ESSENCE_POWER_ORDER = new HashMap<>();
        try (var stream = EssenceWhisperHelper.class.getResourceAsStream("/data/essencelib/essence_whispers.json")) {
            if (stream == null) return;
            JsonObject root = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) return;
            copyStringMap(root.getAsJsonObject("power_feels"), POWER_FEELS);
            copyArrayMap(root.getAsJsonObject("essence_power_order"), ESSENCE_POWER_ORDER);
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load essence whispers data: {}", e.getMessage());
        }
    }

    private static void copyStringMap(JsonObject source, Map<String, String> target) {
        if (source == null) return;
        for (var e : source.entrySet()) {
            if (e.getValue().isJsonPrimitive()) {
                target.put(e.getKey(), e.getValue().getAsString());
            }
        }
    }

    private static void copyArrayMap(JsonObject source, Map<String, List<String>> target) {
        if (source == null) return;
        for (var e : source.entrySet()) {
            if (!e.getValue().isJsonArray()) continue;
            JsonArray arr = e.getValue().getAsJsonArray();
            List<String> out = new ArrayList<>();
            for (JsonElement el : arr) {
                if (el.isJsonPrimitive()) out.add(el.getAsString());
            }
            target.put(e.getKey(), out);
        }
    }

    private EssenceWhisperHelper() {}
}
