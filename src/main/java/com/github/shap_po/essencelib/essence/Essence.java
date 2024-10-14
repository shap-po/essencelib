package com.github.shap_po.essencelib.essence;

import com.github.shap_po.essencelib.EssenceLib;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerReference;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.CompoundSerializableDataType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.util.Validatable;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class Essence implements Validatable {
    public static final CompoundSerializableDataType<Essence> DATA_TYPE = SerializableDataType.compound(
        new SerializableData()
            .add("id", SerializableDataTypes.IDENTIFIER)
            .add("name", SerializableDataTypes.STRING)
            .add("rarity", SerializableDataType.enumValue(Rarity.class), Rarity.COMMON)
            .add("powerReferences", ApoliDataTypes.POWER_REFERENCE.list(), null)
            .add("attributes", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
            .add("replace", SerializableDataTypes.BOOLEAN, false)
        ,
        data -> new Essence(
            data.getId("id"),
            data.getString("name"),
            data.get("rarity"),
            data.get("powerReferences"),
            data.get("attributes"),
            data.getBoolean("replace")
        ),
        (essence, serializableData) -> serializableData.instance()
            .set("id", essence.id)
            .set("name", essence.name)
            .set("rarity", essence.rarity)
            .set("powerReferences", essence.powers)
            .set("attributes", essence.attributes)
            .set("replace", essence.replace)
    );

    private final Identifier id;
    private final String name;
    private final Rarity rarity;
    private final Set<Power> powers;
    private final Set<PowerReference> powerReferences;
    private final Set<AttributedEntityAttributeModifier> attributes;
    private final boolean replace;

    public Essence(
        Identifier id,
        String name,
        Rarity rarity,
        @Nullable Set<PowerReference> powerReferences,
        @Nullable Set<AttributedEntityAttributeModifier> attributes,
        boolean replace
    ) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.powers = new ObjectLinkedOpenHashSet<>();
        this.powerReferences = powerReferences == null ? new ObjectLinkedOpenHashSet<>() : new ObjectLinkedOpenHashSet<>(powerReferences);
        this.attributes = attributes == null ? new ObjectLinkedOpenHashSet<>() : new ObjectLinkedOpenHashSet<>(attributes);
        this.replace = replace;
    }

    public boolean shouldReplace() {
        return replace;
    }

    public Identifier getId() {
        return id;
    }

    public static Essence merge(Essence oldEssence, Essence newEssence) {
        if (newEssence.shouldReplace()) {
            return newEssence;
        }
        oldEssence.powerReferences.addAll(newEssence.powerReferences);
        oldEssence.attributes.addAll(newEssence.attributes);
        return oldEssence;
    }

    @Override
    public void validate() {
        this.powers.clear();
        for (PowerReference powerReference : powerReferences) {
            try {
                powers.add(powerReference.getStrictReference());
            } catch (Exception e) {
                EssenceLib.LOGGER.error("Essence \"{}\" contained unregistered power \"{}\"!", id, powerReference.getId());
            }
        }
    }
}
