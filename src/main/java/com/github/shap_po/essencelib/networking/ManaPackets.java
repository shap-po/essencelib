package com.github.shap_po.essencelib.networking;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.networking.s2c.ManaUpdateS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ManaPackets {
    public static final Identifier MANA_SYNC = EssenceLib.identifier("mana_sync");

    public static void sendManaUpdate(ServerPlayerEntity player, float current, float max) {
        if (player.networkHandler != null && player.getWorld() != null && !player.isDisconnected()) {
            ServerPlayNetworking.send(player, new ManaUpdateS2CPacket(current, max));
        }
    }
} 
