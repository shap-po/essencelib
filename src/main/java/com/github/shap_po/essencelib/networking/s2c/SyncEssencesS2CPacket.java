package com.github.shap_po.essencelib.networking.s2c;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.essence.Essence;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SyncEssencesS2CPacket(Map<Identifier, Essence> essenceById) implements CustomPayload {
    public static final Id<SyncEssencesS2CPacket> PACKET_ID = new Id<>(EssenceLib.identifier("s2c/sync_essence_registry"));
    public static final PacketCodec<RegistryByteBuf, SyncEssencesS2CPacket> PACKET_CODEC = PacketCodec.of(SyncEssencesS2CPacket::write, SyncEssencesS2CPacket::read);

    public static SyncEssencesS2CPacket read(RegistryByteBuf buf) {
        try {
            Collection<Essence> essences = new ObjectArrayList<>();
            int essenceCount = buf.readVarInt();

            for (int i = 0; i < essenceCount; i++) {
                essences.add(Essence.DATA_TYPE.receive(buf));
            }

            return new SyncEssencesS2CPacket(essences
                .stream()
                .collect(Collectors.toMap(Essence::getId, Function.identity(), (oldEssence, newEssence) -> newEssence, Object2ObjectOpenHashMap::new)));

        } catch (Exception e) {
            EssenceLib.LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public void write(RegistryByteBuf buf) {
        Collection<Essence> essences = this.essenceById().values();
        buf.writeVarInt(essences.size());

        essences.forEach(essence -> Essence.DATA_TYPE.send(buf, essence));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
