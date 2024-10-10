package com.github.shap_po.essencelib.component.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class MobEssenceItemComponent implements TooltipAppender {
    public static final MobEssenceItemComponent DEFAULT = new MobEssenceItemComponent(new ObjectLinkedOpenHashSet<>());

    private final ObjectLinkedOpenHashSet<Entry> entries;

    MobEssenceItemComponent(Collection<Entry> entries) {
        this.entries = new ObjectLinkedOpenHashSet<>(entries);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("component.item.essencelib.mob_essence"));
    }

    public static final Codec<MobEssenceItemComponent> CODEC = Entry.SET_CODEC.xmap(
        MobEssenceItemComponent::new,
        MobEssenceItemComponent::entries
    );

    public static final PacketCodec<PacketByteBuf, MobEssenceItemComponent> PACKET_CODEC = PacketCodecs.collection(ObjectLinkedOpenHashSet::new, Entry.PACKET_CODEC).xmap(
        MobEssenceItemComponent::new,
        MobEssenceItemComponent::entries
    );

    private ObjectLinkedOpenHashSet<Entry> entries() {
        return entries;
    }

    public record Entry(Identifier entity) {
        public static final MapCodec<Entry> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Identifier.CODEC.fieldOf("entity").forGetter(Entry::entity)
            ).apply(instance, Entry::new)
        );

        public static final PacketCodec<PacketByteBuf, Entry> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, Entry::entity,
            Entry::new
        );

        public static final Codec<Set<Entry>> SET_CODEC = MAP_CODEC.codec().listOf().xmap(
            ImmutableSet::copyOf,
            ImmutableList::copyOf
        );
    }
}
