package com.github.shap_po.essencelib.networking;

import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.networking.s2c.SyncEssencesS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ModPacketsS2C {
    public static void register() {
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(SyncEssencesS2CPacket.PACKET_ID, (packet, context) -> EssenceManager.receive(packet));
        }));
    }
}
