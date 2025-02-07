package com.github.shap_po.essencelib.networking;

import com.github.shap_po.essencelib.client.ClientManaData;
import com.github.shap_po.essencelib.networking.s2c.ManaUpdateS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModPacketsClient {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ManaUpdateS2CPacket.ID,
            (packet, context) -> ClientManaData.set(packet.current(), packet.max()));
    }
} 
