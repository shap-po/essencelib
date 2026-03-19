package com.github.shap_po.essencelib.networking.s2c;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UniqueKillToastS2CPacket(Identifier entityId) implements CustomPayload {
    public static final CustomPayload.Id<UniqueKillToastS2CPacket> ID = new CustomPayload.Id<>(EssenceLib.identifier("unique_kill_toast"));
    public static final PacketCodec<RegistryByteBuf, UniqueKillToastS2CPacket> CODEC = PacketCodec.tuple(
        Identifier.PACKET_CODEC, UniqueKillToastS2CPacket::entityId,
        UniqueKillToastS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
