package com.github.shap_po.essencelib.essence;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.registry.ModItems;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import dev.emi.trinkets.api.TrinketsAttributeModifiersComponent;
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
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

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
            .add("can_unequip", SerializableDataTypes.BOOLEAN, false)
            .add("auto_equip", SerializableDataTypes.BOOLEAN, true)
        ,
        data -> new Essence(
            data.getId("id"),
            data.getString("name"),
            data.get("rarity"),
            data.get("powers"),
            data.get("attributes"),
            data.getBoolean("replace"),
            data.getBoolean("can_unequip"),
            data.getBoolean("auto_equip")
        ),
        (essence, serializableData) -> serializableData.instance()
            .set("id", essence.id)
            .set("name", essence.name)
            .set("rarity", essence.rarity)
            .set("powers", essence.powers)
            .set("attributes", essence.attributes)
            .set("replace", essence.replace)
            .set("can_unequip", essence.canUnequip)
            .set("auto_equip", essence.autoEquip)
    );

    private final Identifier id;
    private final String name;
    private final Rarity rarity;
    private final Set<Power> powers;
    private final List<PowerReference> powerReferences;
    private final List<AttributedEntityAttributeModifier> attributes;
    private final boolean replace;
    private final boolean canUnequip;
    private final boolean autoEquip;

    public Essence(
        Identifier id,
        String name,
        Rarity rarity,
        @Nullable List<PowerReference> powerReferences,
        @Nullable List<AttributedEntityAttributeModifier> attributes,
        boolean replace,
        boolean canUnequip,
        boolean autoEquip
    ) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.powers = new ObjectLinkedOpenHashSet<>();
        this.powerReferences = powerReferences == null ? new LinkedList<>() : new LinkedList<>(powerReferences);
        this.attributes = attributes == null ? new LinkedList<>() : new LinkedList<>(attributes);
        this.replace = replace;
        this.canUnequip = canUnequip;
        this.autoEquip = autoEquip;
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

    public boolean canUnequip() {
        return canUnequip;
    }

    public boolean autoEquip() {
        return autoEquip;
    }

    public ComponentMap.Builder toComponent() {
        ComponentMap.Builder builder = ComponentMap.builder();

        builder.add(ModDataComponentTypes.ESSENCE_ID, id);
        builder.add(DataComponentTypes.ITEM_NAME, Text.of(name));
        builder.add(DataComponentTypes.RARITY, rarity);

        if (!powerReferences.isEmpty()) {
            TrinketItemPowersComponent.Builder itemPowers = TrinketItemPowersComponent.builder();
            for (PowerReference powerReference : powerReferences) {
                itemPowers.add(powerReference.getId(), false, false, false);
            }
            builder.add(ShappoliTrinketsDataComponentTypes.POWERS, itemPowers.build());
        }

        if (!attributes.isEmpty()) {
            TrinketsAttributeModifiersComponent.Builder attributeModifiers = TrinketsAttributeModifiersComponent.builder();
            for (AttributedEntityAttributeModifier attribute : attributes) {
                attributeModifiers.add(attribute.attribute(), attribute.modifier());
            }
            builder.add(TrinketsAttributeModifiersComponent.TYPE, attributeModifiers.build());
        }

        builder.add(ModDataComponentTypes.CAN_UNEQUIP, canUnequip);
        builder.add(ModDataComponentTypes.AUTO_EQUIP, autoEquip);

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
            ", canUnequip=" + canUnequip +
            ", autoEquip=" + autoEquip +
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
