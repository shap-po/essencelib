package com.github.shap_po.essencelib.hud;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads HUD-tracked power ids from power_hud_styles.json.
 * Any id key in that file can become a synced HUD bar.
 */
public final class PowerHudSyncRegistry {
    private static final Gson GSON = new Gson();
    private static final Identifier DEFAULT_COLLECTOR =
        Identifier.of("esspack", "allay_essence/hoarder_lifestyle_collection_charge");
    private static final Identifier DEFAULT_RESOLVE =
        Identifier.of("esspack", "armadillo_essence/threat_rhythm_lifestyle_resolve");
    private static final Identifier DEFAULT_REBOUND =
        Identifier.of("esspack", "armadillo_essence/scute_rebound_momentum");
    private static List<Identifier> cached;

    public static List<Identifier> getTrackedPowerIds() {
        if (cached != null) {
            return cached;
        }
        List<Identifier> ids = new ArrayList<>();
        try (InputStream stream = PowerHudSyncRegistry.class.getResourceAsStream("/data/essencelib/power_hud_styles.json")) {
            if (stream != null) {
                JsonObject root = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                if (root != null) {
                    for (var entry : root.entrySet()) {
                        Identifier id = Identifier.tryParse(entry.getKey());
                        if (id != null) {
                            ids.add(id);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        // Safety fallback for dev/runtime resource-loading edge cases.
        if (ids.isEmpty()) {
            ids.add(DEFAULT_COLLECTOR);
            ids.add(DEFAULT_RESOLVE);
            ids.add(DEFAULT_REBOUND);
        }
        cached = List.copyOf(ids);
        return cached;
    }

    private PowerHudSyncRegistry() {}
}
