package com.github.shap_po.essencelib.networking.s2c;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record LevelUpToastS2CPacket(int level) implements CustomPayload {
    public static final CustomPayload.Id<LevelUpToastS2CPacket> ID = new CustomPayload.Id<>(EssenceLib.identifier("level_up_toast"));
    public static final PacketCodec<RegistryByteBuf, LevelUpToastS2CPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.VAR_INT, LevelUpToastS2CPacket::level,
        LevelUpToastS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
