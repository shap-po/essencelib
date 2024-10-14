package com.github.shap_po.essencelib.essence;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.CompoundSerializableDataType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Essence {
    public static final CompoundSerializableDataType<Essence> DATA_TYPE = SerializableDataType.compound(
        new SerializableData()
            .add("id", SerializableDataTypes.IDENTIFIER)
            .add("name", SerializableDataTypes.STRING)
            .add("rarity", SerializableDataType.enumValue(Rarity.class), Rarity.COMMON)
            .add("powers", ApoliDataTypes.POWER_REFERENCE.list(), null)
            .add("attributes", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
            .add("replace", SerializableDataTypes.BOOLEAN, false)
        ,
        data -> new Essence(
            data.getId("id"),
            data.getString("name"),
            data.get("rarity"),
            data.get("powers"),
            data.get("attributes"),
            data.getBoolean("replace")
        ),
        (essence, serializableData) -> serializableData.instance()
            .set("id", essence.id)
            .set("name", essence.name)
            .set("rarity", essence.rarity)
            .set("powers", essence.powers)
            .set("attributes", essence.attributes)
            .set("replace", essence.replace)
    );

    private final Identifier id;
    private final String name;
    private final Rarity rarity;
    private final List<Power> powers;
    private final List<AttributedEntityAttributeModifier> attributes;
    private final boolean replace;

    public Essence(
        Identifier id,
        String name,
        Rarity rarity,
        @Nullable List<Power> powers,
        @Nullable List<AttributedEntityAttributeModifier> attributes,
        boolean replace
    ) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.powers = powers == null ? new ArrayList<>() : powers;
        this.attributes = attributes == null ? new ArrayList<>() : attributes;
        this.replace = replace;
    }

    public boolean shouldReplace() {
        return replace;
    }
}
