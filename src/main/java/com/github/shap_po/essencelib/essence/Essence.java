package com.github.shap_po.essencelib.essence;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.registry.ModItems;
import com.github.shap_po.essencelib.tags.ModTags;
import dev.emi.trinkets.api.SlotAttributes;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
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
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Essence implements Validatable {
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

    public static final AttributeModifierSlot SLOT = AttributeModifierSlot.ANY ;

    private final Identifier id;
    private final String name;
    private final Rarity rarity;
    private final Set<Power> powers;
    private final List<PowerReference> powerReferences;
    private final List<AttributedEntityAttributeModifier> attributes;
    private final boolean replace;

    public Essence(
        Identifier id,
        String name,
        Rarity rarity,
        @Nullable List<PowerReference> powerReferences,
        @Nullable List<AttributedEntityAttributeModifier> attributes,
        boolean replace
    ) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.powers = new ObjectLinkedOpenHashSet<>();
        this.powerReferences = powerReferences == null ? new LinkedList<>() : new LinkedList<>(powerReferences);
        this.attributes = attributes == null ? new LinkedList<>() : new LinkedList<>(attributes);
        this.replace = replace;
    }

    public Identifier getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Set<Power> getPowers() {
        return powers;
    }

    public List<PowerReference> getPowerReferences() {
        return powerReferences;
    }

    public List<AttributedEntityAttributeModifier> getAttributes() {
        return attributes;
    }

    public boolean shouldReplace() {
        return replace;
    }

    public ComponentMap.Builder toComponent() {
        ComponentMap.Builder builder = ComponentMap.builder();

        builder.add(ModDataComponentTypes.ESSENCE_ID, id);
        builder.add(DataComponentTypes.ITEM_NAME, Text.of(name));
        builder.add(DataComponentTypes.RARITY, rarity);

        if (!powerReferences.isEmpty()) {
            ItemPowersComponent.Builder itemPowers = ItemPowersComponent.builder();
            for (PowerReference powerReference : powerReferences) {
                itemPowers.add(EnumSet.of(SLOT), powerReference.getId(), true, false);
            }
            builder.add(ApoliDataComponentTypes.POWERS, itemPowers.build());
        }

        if (!attributes.isEmpty()) {
            AttributeModifiersComponent.Builder attributeModifiers = AttributeModifiersComponent.builder();
            for (AttributedEntityAttributeModifier attribute : attributes) {
                attributeModifiers.add(attribute.attribute(), attribute.modifier(), SLOT);
            }
            builder.add(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers.build());
        }

        return builder;
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(ModItems.MOB_ESSENCE_ITEM);
        stack.applyComponentsFrom(toComponent().build());
        return stack;
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
    public String toString() {
        return "Essence{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", rarity=" + rarity +
            ", powers=" + powers +
            ", powerReferences=" + powerReferences +
            ", attributes=" + attributes +
            ", replace=" + replace +
            '}';
    }

    public Text toText() {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(id.toString()));
        return Text.literal(name)
            .styled(style -> style.withHoverEvent(hoverEvent));
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
