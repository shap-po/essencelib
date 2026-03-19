package com.github.shap_po.essencelib.config;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Server/common hook config for coded ability behaviors.
 *
 * Resource path: data/essencelib/ability_hooks.json
 */
public final class ServerAbilityHookConfig {

    private static final String CONFIG_PATH = "/data/essencelib/ability_hooks.json";

    private static final Identifier DEFAULT_COLLECTOR_RUSH_CHARGE_POWER =
        Identifier.of("esspack", "allay_essence/hoarder_lifestyle_collection_charge");
    private static final double DEFAULT_COLLECTOR_RUSH_MANA_ON_NEW_ITEM = 15.0;
    private static final Identifier DEFAULT_COLLECTOR_MEMORY_STORAGE =
        Identifier.of("esspack", "memory");
    private static final String DEFAULT_COLLECTOR_MEMORY_LIST_KEY = "RecentItems";

    private static final Identifier DEFAULT_ARMADILLO_BALL_STATE_POWER =
        Identifier.of("esspack", "armadillo_essence/armadillo_ball_up_ball_state");
    private static final Identifier DEFAULT_THREAT_RESOLVE_POWER =
        Identifier.of("esspack", "armadillo_essence/threat_rhythm_lifestyle_resolve");
    private static final Identifier DEFAULT_THREAT_AGGRO_TIMER_POWER =
        Identifier.of("esspack", "armadillo_essence/threat_rhythm_lifestyle_aggro_timer");
    private static final Identifier DEFAULT_THREAT_CHASED_STATE_POWER =
        Identifier.of("esspack", "armadillo_essence/threat_rhythm_lifestyle_chased_state");

    private static final int DEFAULT_THREAT_TRACK_TICKS = 100;
    private static final double DEFAULT_AWAY_DISTANCE_SQ_STEP = 0.09;
    private static final double DEFAULT_MIN_PLAYER_MOVE_SQ = 0.0025;
    private static final int DEFAULT_AWAY_PENALTY_PER_STEP = 14;
    private static final int DEFAULT_FRONTLINE_GAIN_INTERVAL_TICKS = 8;
    private static final int DEFAULT_FRONTLINE_LOSS_INTERVAL_TICKS = 15;
    private static final int DEFAULT_RETREAT_DRAIN_INTERVAL_TICKS = 5;
    private static final int DEFAULT_RETREAT_DRAIN_PER_STEP = 4;
    private static final int DEFAULT_DAMAGE_FRONTLINE_RESOLVE_GAIN = 8;
    private static final int DEFAULT_DAMAGE_RETREAT_RESOLVE_PENALTY = 6;
    private static final double DEFAULT_FACING_TARGET_DOT_MIN = 0.25;
    private static final double DEFAULT_HARD_KITE_FACING_DOT_MAX = 0.05;

    private static Config config;

    public static Config get() {
        ensureLoaded();
        return config;
    }

    private static synchronized void ensureLoaded() {
        if (config != null) return;
        config = defaults();
        try (var stream = ServerAbilityHookConfig.class.getResourceAsStream(CONFIG_PATH)) {
            if (stream == null) return;
            JsonObject root = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) return;
            config = new Config(
                parseIdentifier(root.get("collector_rush_charge_power"), DEFAULT_COLLECTOR_RUSH_CHARGE_POWER),
                parseDouble(root.get("collector_rush_mana_on_new_item"), DEFAULT_COLLECTOR_RUSH_MANA_ON_NEW_ITEM),
                parseIdentifier(root.get("collector_memory_storage"), DEFAULT_COLLECTOR_MEMORY_STORAGE),
                parseString(root.get("collector_memory_list_key"), DEFAULT_COLLECTOR_MEMORY_LIST_KEY),
                parseIdentifier(root.get("armadillo_ball_state_power"), DEFAULT_ARMADILLO_BALL_STATE_POWER),
                parseIdentifier(root.get("threat_rhythm_resolve_power"), DEFAULT_THREAT_RESOLVE_POWER),
                parseIdentifier(root.get("threat_rhythm_aggro_timer_power"), DEFAULT_THREAT_AGGRO_TIMER_POWER),
                parseIdentifier(root.get("threat_rhythm_chased_state_power"), DEFAULT_THREAT_CHASED_STATE_POWER),
                parseInt(root.get("threat_track_ticks"), DEFAULT_THREAT_TRACK_TICKS),
                parseDouble(root.get("away_distance_sq_step"), DEFAULT_AWAY_DISTANCE_SQ_STEP),
                parseDouble(root.get("min_player_move_sq"), DEFAULT_MIN_PLAYER_MOVE_SQ),
                parseInt(root.get("away_penalty_per_step"), DEFAULT_AWAY_PENALTY_PER_STEP),
                parseInt(root.get("frontline_gain_interval_ticks"), DEFAULT_FRONTLINE_GAIN_INTERVAL_TICKS),
                parseInt(root.get("frontline_loss_interval_ticks"), DEFAULT_FRONTLINE_LOSS_INTERVAL_TICKS),
                parseInt(root.get("retreat_drain_interval_ticks"), DEFAULT_RETREAT_DRAIN_INTERVAL_TICKS),
                parseInt(root.get("retreat_drain_per_step"), DEFAULT_RETREAT_DRAIN_PER_STEP),
                parseInt(root.get("damage_frontline_resolve_gain"), DEFAULT_DAMAGE_FRONTLINE_RESOLVE_GAIN),
                parseInt(root.get("damage_retreat_resolve_penalty"), DEFAULT_DAMAGE_RETREAT_RESOLVE_PENALTY),
                parseDouble(root.get("facing_target_dot_min"), DEFAULT_FACING_TARGET_DOT_MIN),
                parseDouble(root.get("hard_kite_facing_dot_max"), DEFAULT_HARD_KITE_FACING_DOT_MAX)
            );
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load server ability hook config: {}", e.getMessage());
        }
    }

    private static Config defaults() {
        return new Config(
            DEFAULT_COLLECTOR_RUSH_CHARGE_POWER,
            DEFAULT_COLLECTOR_RUSH_MANA_ON_NEW_ITEM,
            DEFAULT_COLLECTOR_MEMORY_STORAGE,
            DEFAULT_COLLECTOR_MEMORY_LIST_KEY,
            DEFAULT_ARMADILLO_BALL_STATE_POWER,
            DEFAULT_THREAT_RESOLVE_POWER,
            DEFAULT_THREAT_AGGRO_TIMER_POWER,
            DEFAULT_THREAT_CHASED_STATE_POWER,
            DEFAULT_THREAT_TRACK_TICKS,
            DEFAULT_AWAY_DISTANCE_SQ_STEP,
            DEFAULT_MIN_PLAYER_MOVE_SQ,
            DEFAULT_AWAY_PENALTY_PER_STEP,
            DEFAULT_FRONTLINE_GAIN_INTERVAL_TICKS,
            DEFAULT_FRONTLINE_LOSS_INTERVAL_TICKS,
            DEFAULT_RETREAT_DRAIN_INTERVAL_TICKS,
            DEFAULT_RETREAT_DRAIN_PER_STEP,
            DEFAULT_DAMAGE_FRONTLINE_RESOLVE_GAIN,
            DEFAULT_DAMAGE_RETREAT_RESOLVE_PENALTY,
            DEFAULT_FACING_TARGET_DOT_MIN,
            DEFAULT_HARD_KITE_FACING_DOT_MAX
        );
    }

    private static Identifier parseIdentifier(JsonElement element, Identifier def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        Identifier parsed = Identifier.tryParse(element.getAsString());
        return parsed != null ? parsed : def;
    }

    private static String parseString(JsonElement element, String def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        String value = element.getAsString();
        return value != null && !value.isBlank() ? value : def;
    }

    private static int parseInt(JsonElement element, int def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        try {
            return element.getAsInt();
        } catch (Exception ignored) {
            return def;
        }
    }

    private static double parseDouble(JsonElement element, double def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        try {
            return element.getAsDouble();
        } catch (Exception ignored) {
            return def;
        }
    }

    public record Config(
        Identifier collectorRushChargePower,
        double collectorRushManaOnNewItem,
        Identifier collectorMemoryStorage,
        String collectorMemoryListKey,
        Identifier armadilloBallStatePower,
        Identifier threatRhythmResolvePower,
        Identifier threatRhythmAggroTimerPower,
        Identifier threatRhythmChasedStatePower,
        int threatTrackTicks,
        double awayDistanceSqStep,
        double minPlayerMoveSq,
        int awayPenaltyPerStep,
        int frontlineGainIntervalTicks,
        int frontlineLossIntervalTicks,
        int retreatDrainIntervalTicks,
        int retreatDrainPerStep,
        int damageFrontlineResolveGain,
        int damageRetreatResolvePenalty,
        double facingTargetDotMin,
        double hardKiteFacingDotMax
    ) {}

    private ServerAbilityHookConfig() {}
}
