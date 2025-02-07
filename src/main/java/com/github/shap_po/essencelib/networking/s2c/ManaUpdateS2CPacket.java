package com.github.shap_po.essencelib.networking.s2c;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ManaUpdateS2CPacket(float current, float max) implements CustomPayload {
    public static final Id<ManaUpdateS2CPacket> ID = new Id<>(EssenceLib.identifier("mana_update"));
    public static final PacketCodec<PacketByteBuf, ManaUpdateS2CPacket> CODEC = PacketCodec.of(ManaUpdateS2CPacket::write, ManaUpdateS2CPacket::read);

    public static ManaUpdateS2CPacket read(PacketByteBuf buf) {
        return new ManaUpdateS2CPacket(buf.readFloat(), buf.readFloat());
    }

    public void write(PacketByteBuf buf) {
        buf.writeFloat(current());
        buf.writeFloat(max());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
} 