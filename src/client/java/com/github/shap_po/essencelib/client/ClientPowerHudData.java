package com.github.shap_po.essencelib.client;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ClientPowerHudData {
    private static final Map<Identifier, Integer> VALUES = new HashMap<>();

    public static void setFromPayload(String payload) {
        VALUES.clear();
        if (payload == null || payload.isBlank()) {
            return;
        }
        String[] entries = payload.split(";");
        for (String entry : entries) {
            int sep = entry.indexOf('=');
            if (sep <= 0 || sep >= entry.length() - 1) {
                continue;
            }
            Identifier id = Identifier.tryParse(entry.substring(0, sep));
            if (id == null) {
                continue;
            }
            try {
                int value = Integer.parseInt(entry.substring(sep + 1));
                VALUES.put(id, Math.max(0, value));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public static @Nullable Integer get(Identifier id) {
        return VALUES.get(id);
    }

    public static void clear() {
        VALUES.clear();
    }

    private ClientPowerHudData() {}
}
