package com.github.shap_po.essencelib.networking.s2c;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PowerHudSyncS2CPacket(String payload) implements CustomPayload {
    public static final Id<PowerHudSyncS2CPacket> ID = new Id<>(EssenceLib.identifier("power_hud_sync"));
    public static final PacketCodec<RegistryByteBuf, PowerHudSyncS2CPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, PowerHudSyncS2CPacket::payload,
        PowerHudSyncS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
