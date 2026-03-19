package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Client-side hook config for data-driven ability wiring.
 *
 * Resource path: data/essencelib/ability_hooks.json
 */
@Environment(EnvType.CLIENT)
public final class ClientAbilityHookConfig {

    private static final String CONFIG_PATH = "/data/essencelib/ability_hooks.json";

    private static final Identifier DEFAULT_COLLECTOR_INTUITION_POWER =
        Identifier.of("esspack", "allay_essence/item_whisperer");
    private static final Identifier DEFAULT_COLLECTOR_INTUITION_DETECTION_PROFILE =
        Identifier.of("essencelib", "collector_intuition_item_outline");
    private static final Identifier DEFAULT_COLLECTORS_RUSH_POWER =
        Identifier.of("esspack", "allay_essence/hoarder_lifestyle_collection_charge");
    private static final Identifier DEFAULT_COLLECTION_RANGE_ATTRIBUTE =
        Identifier.of("additionalentityattributes", "player.collection_range");
    private static final double DEFAULT_BASE_RANGE = 30.0;
    private static final double DEFAULT_RANGE_PER_COLLECTION = 4.0;
    private static final double DEFAULT_MIN_RANGE = 12.0;
    private static final double DEFAULT_MAX_RANGE = 64.0;
    private static final Identifier DEFAULT_UNCOLLECTED_HINT_POWER = DEFAULT_COLLECTORS_RUSH_POWER;
    private static final double DEFAULT_UNCOLLECTED_HINT_RANGE = 10.0;
    private static final int DEFAULT_UNCOLLECTED_HINT_INTERVAL = 30;
    private static final Identifier DEFAULT_UNCOLLECTED_HINT_PARTICLE =
        Identifier.of("minecraft", "happy_villager");
    private static final String DEFAULT_COLLECTOR_INTUITION_GLOW_MODE = "player_through_walls_custom";

    private static Config config;

    public static Config get() {
        ensureLoaded();
        return config;
    }

    private static synchronized void ensureLoaded() {
        if (config != null) return;
        config = defaults();

        try (var stream = ClientAbilityHookConfig.class.getResourceAsStream(CONFIG_PATH)) {
            if (stream == null) return;
            JsonObject root = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) return;

            config = new Config(
                parseIdentifier(root.get("collector_intuition_power"), DEFAULT_COLLECTOR_INTUITION_POWER),
                parseIdentifier(root.get("collector_intuition_detection_profile"), DEFAULT_COLLECTOR_INTUITION_DETECTION_PROFILE),
                parseIdentifier(root.get("collectors_rush_power"), DEFAULT_COLLECTORS_RUSH_POWER),
                parseIdentifier(root.get("collection_range_attribute"), DEFAULT_COLLECTION_RANGE_ATTRIBUTE),
                parseDouble(root.get("collector_intuition_base_range"), DEFAULT_BASE_RANGE),
                parseDouble(root.get("collector_intuition_range_per_collection"), DEFAULT_RANGE_PER_COLLECTION),
                parseDouble(root.get("collector_intuition_min_range"), DEFAULT_MIN_RANGE),
                parseDouble(root.get("collector_intuition_max_range"), DEFAULT_MAX_RANGE),
                parseIdentifier(root.get("uncollected_hint_required_power"), DEFAULT_UNCOLLECTED_HINT_POWER),
                parseDouble(root.get("uncollected_hint_range"), DEFAULT_UNCOLLECTED_HINT_RANGE),
                parseInt(root.get("uncollected_hint_interval"), DEFAULT_UNCOLLECTED_HINT_INTERVAL),
                parseIdentifier(root.get("uncollected_hint_particle"), DEFAULT_UNCOLLECTED_HINT_PARTICLE),
                parseString(root.get("collector_intuition_glow_mode"), DEFAULT_COLLECTOR_INTUITION_GLOW_MODE)
            );
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load client ability hook config: {}", e.getMessage());
        }
    }

    private static Config defaults() {
        return new Config(
            DEFAULT_COLLECTOR_INTUITION_POWER,
            DEFAULT_COLLECTOR_INTUITION_DETECTION_PROFILE,
            DEFAULT_COLLECTORS_RUSH_POWER,
            DEFAULT_COLLECTION_RANGE_ATTRIBUTE,
            DEFAULT_BASE_RANGE,
            DEFAULT_RANGE_PER_COLLECTION,
            DEFAULT_MIN_RANGE,
            DEFAULT_MAX_RANGE,
            DEFAULT_UNCOLLECTED_HINT_POWER,
            DEFAULT_UNCOLLECTED_HINT_RANGE,
            DEFAULT_UNCOLLECTED_HINT_INTERVAL,
            DEFAULT_UNCOLLECTED_HINT_PARTICLE,
            DEFAULT_COLLECTOR_INTUITION_GLOW_MODE
        );
    }

    private static Identifier parseIdentifier(JsonElement element, Identifier def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        Identifier parsed = Identifier.tryParse(element.getAsString());
        return parsed != null ? parsed : def;
    }

    private static double parseDouble(JsonElement element, double def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        try {
            return element.getAsDouble();
        } catch (Exception ignored) {
            return def;
        }
    }

    private static int parseInt(JsonElement element, int def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        try {
            return element.getAsInt();
        } catch (Exception ignored) {
            return def;
        }
    }

    private static String parseString(JsonElement element, String def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        try {
            String value = element.getAsString();
            return (value == null || value.isBlank()) ? def : value;
        } catch (Exception ignored) {
            return def;
        }
    }

    public record Config(
        Identifier collectorIntuitionPower,
        Identifier collectorIntuitionDetectionProfile,
        Identifier collectorsRushPower,
        Identifier collectionRangeAttribute,
        double collectorIntuitionBaseRange,
        double collectorIntuitionRangePerCollection,
        double collectorIntuitionMinRange,
        double collectorIntuitionMaxRange,
        Identifier uncollectedHintRequiredPower,
        double uncollectedHintRange,
        int uncollectedHintInterval,
        Identifier uncollectedHintParticle,
        String collectorIntuitionGlowMode
    ) {}

    private ClientAbilityHookConfig() {}
}
