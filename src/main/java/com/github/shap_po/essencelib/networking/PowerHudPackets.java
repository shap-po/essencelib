package com.github.shap_po.essencelib.networking;

import com.github.shap_po.essencelib.networking.s2c.PowerHudSyncS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class PowerHudPackets {
    public static void send(ServerPlayerEntity player, Map<Identifier, Integer> values) {
        if (player.networkHandler != null && player.getWorld() != null && !player.isDisconnected()) {
            ServerPlayNetworking.send(player, new PowerHudSyncS2CPacket(serialize(values)));
        }
    }

    private static String serialize(Map<Identifier, Integer> values) {
        if (values.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (var entry : values.entrySet()) {
            if (!out.isEmpty()) {
                out.append(';');
            }
            out.append(entry.getKey());
            out.append('=');
            out.append(Math.max(0, entry.getValue()));
        }
        return out.toString();
    }

    private PowerHudPackets() {}
}
