package com.github.shap_po.essencelib.networking;

import com.github.shap_po.essencelib.client.ClientManaData;
import com.github.shap_po.essencelib.client.ClientPowerHudData;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.networking.s2c.LevelUpToastS2CPacket;
import com.github.shap_po.essencelib.networking.s2c.ManaUpdateS2CPacket;
import com.github.shap_po.essencelib.networking.s2c.PowerHudSyncS2CPacket;
import com.github.shap_po.essencelib.networking.s2c.SyncEssencesS2CPacket;
import com.github.shap_po.essencelib.networking.s2c.UniqueKillToastS2CPacket;
import com.github.shap_po.essencelib.toast.LevelUpToast;
import com.github.shap_po.essencelib.toast.UniqueKillToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ModPacketsS2C {
    public static void register() {
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPowerHudData.clear();
            ClientPlayNetworking.registerReceiver(SyncEssencesS2CPacket.PACKET_ID, (packet, context) -> EssenceManager.receive(packet));
            ClientPlayNetworking.registerReceiver(ManaUpdateS2CPacket.ID, (payload, context) -> {
                ClientManaData.set(payload.current(), payload.max());
            });
            ClientPlayNetworking.registerReceiver(PowerHudSyncS2CPacket.ID, (payload, context) -> {
                ClientPowerHudData.setFromPayload(payload.payload());
            });
            ClientPlayNetworking.registerReceiver(UniqueKillToastS2CPacket.ID, (payload, context) -> {
                minecraftClient.getToastManager().add(new UniqueKillToast(payload.entityId()));
            });
            ClientPlayNetworking.registerReceiver(LevelUpToastS2CPacket.ID, (payload, context) -> {
                minecraftClient.getToastManager().add(new LevelUpToast(payload.level()));
            });
        }));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientPowerHudData.clear();
        });
    }
}
