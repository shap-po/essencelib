package com.github.shap_po.essencelib.networking;

import com.github.shap_po.essencelib.networking.s2c.ManaUpdateS2CPacket;
import com.github.shap_po.essencelib.networking.s2c.SyncEssencesS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPackets {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(SyncEssencesS2CPacket.PACKET_ID, SyncEssencesS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ManaUpdateS2CPacket.ID, ManaUpdateS2CPacket.CODEC);
    }
}
