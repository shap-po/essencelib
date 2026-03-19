package com.github.shap_po.essencelib.essence;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.registry.PowerSlotRegistry;
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
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class Essence implements Validatable {
    public static final CompoundSerializableDataType<Essence> DATA_TYPE = SerializableDataType.compound(
        new SerializableData()
            .add("id", SerializableDataTypes.IDENTIFIER)
            .add("name", SerializableDataTypes.STRING)
            .add("rarity", SerializableDataType.enumValue(Rarity.class), Rarity.COMMON)

            .add("powers", ApoliDataTypes.POWER_REFERENCE.list(), null)
            .add("attributes", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)

            .add("dropped_by", SerializableDataTypes.ENTITY_TYPE, null)
            .add("chance", SerializableDataTypes.DOUBLE, null)
            .add("color", SerializableDataTypes.INT, -1) // Default -1 if unspecified

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

            data.get("dropped_by"),
            data.get("chance"),
            data.getInt("color"),

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

            .set("color", essence.color)

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
    private final @Nullable EntityType<?> droppedBy;
    private final @Nullable Double chance;
    private final int color;
    private final boolean replace;
    private final boolean canUnequip;
    private final boolean autoEquip;

    public Essence(
        Identifier id,
        String name,
        Rarity rarity,
        @Nullable List<PowerReference> powerReferences,
        @Nullable List<AttributedEntityAttributeModifier> attributes,
        @Nullable EntityType<?> droppedBy,
        @Nullable Double chance,
        int color,
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

        this.droppedBy = droppedBy;
        this.chance = chance;
        this.color = color;

        this.replace = replace;
        this.canUnequip = canUnequip;
        this.autoEquip = autoEquip;
    }

    public static Essence merge(Essence oldEssence, Essence newEssence) {
        if (newEssence.shouldReplace()) {
            return newEssence;
        }
        oldEssence.powerReferences.addAll(newEssence.powerReferences);
        oldEssence.attributes.addAll(newEssence.attributes);
        return oldEssence;
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

    public @Nullable EntityType<?> getDroppedBy() {
        return droppedBy;
    }

    public @Nullable Double getChance() {
        return chance;
    }

    public int getColor() {
        return color;
    }

    public boolean hasDropRules() {
        return droppedBy != null || chance != null;
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

    public EssenceReference getReference() {
        return new EssenceReference(id);
    }

    public ComponentMap.Builder toComponent() {
        return toComponent(powerReferences);
    }

    private ComponentMap.Builder toComponent(List<PowerReference> refs) {
        ComponentMap.Builder builder = ComponentMap.builder();

        builder.add(ModDataComponentTypes.ESSENCE_ID, id);
        builder.add(DataComponentTypes.ITEM_NAME, Text.of(name).copy().withColor(color));
        builder.add(DataComponentTypes.RARITY, rarity);

        if (!refs.isEmpty()) {
            TrinketItemPowersComponent.Builder itemPowers = TrinketItemPowersComponent.builder();
            for (PowerReference powerReference : refs) {
                itemPowers.add(powerReference.id(), false, false, false);
            }
            builder.add(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS, itemPowers.build());
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
        builder.add(ModDataComponentTypes.IDENTIFIED, false);

        return builder;
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(ModItems.MOB_ESSENCE_ITEM);
        stack.applyComponentsFrom(toComponent().build());
        return stack;
    }

    public ItemStack applyToItemStack(ItemStack stack) {
        stack.applyComponentsFrom(toComponent().build());
        return stack;
    }

    /**
     * Selects which power references to put on the item.
     */
    private List<PowerReference> selectPowerRefs(@Nullable Random random) {
        if (random == null || powerReferences.isEmpty()) return powerReferences;
        
        List<String> availableSlots = new ArrayList<>();
        Map<String, List<PowerReference>> slotMap = new HashMap<>();

        for (PowerReference ref : powerReferences) {
            String s = PowerSlotRegistry.getSlot(ref.id());
            if (s != null) {
                if (!slotMap.containsKey(s)) {
                    availableSlots.add(s);
                    slotMap.put(s, new ArrayList<>());
                }
                slotMap.get(s).add(ref);
            }
        }

        if (availableSlots.isEmpty()) {
            return powerReferences; // Fallback if no slotted powers
        }

        // Pick one slot type at random (e.g. active, passive, or lifestyle)
        String pickedSlot = availableSlots.get(random.nextInt(availableSlots.size()));
        
        List<PowerReference> result = new ArrayList<>();
        if (slotMap.containsKey(pickedSlot)) {
            result.addAll(slotMap.get(pickedSlot));
        }
        appendSupplementalPowersForSlot(result, pickedSlot);
        
        return result;
    }

    /**
     * Selects power references for a specific slot (active/passive/lifestyle).
     * Returns empty list if slot is invalid or essence has no powers in that slot.
     */
    private List<PowerReference> selectPowerRefsBySlot(String slot) {
        if (slot == null || powerReferences.isEmpty()) return powerReferences;
        List<PowerReference> out = new ArrayList<>();
        boolean hasAnySlotted = false;
        for (PowerReference ref : powerReferences) {
            String s = PowerSlotRegistry.getSlot(ref.id());
            if (s != null) {
                hasAnySlotted = true;
            }
            if (slot.equals(s)) out.add(ref);
        }
        // Keep backward compatibility for packs that do not define any slot mappings.
        return !hasAnySlotted ? powerReferences : out;
    }

    /**
     * Appends unslotted helper powers for a selected slot.
     * - Lifestyle slot receives unslotted lifestyle auxiliaries (drains/warnings/buffs bundles).
     * - Active/Passive receive only explicit key helpers (e.g. *_key).
     */
    private void appendSupplementalPowersForSlot(List<PowerReference> refs, @Nullable String slot) {
        for (PowerReference ref : powerReferences) {
            if (PowerSlotRegistry.getSlot(ref.id()) != null || refs.contains(ref)) continue;
            boolean isKeyHelper = ref.id().getPath().endsWith("_key");
            if (PowerSlotRegistry.SLOT_LIFESTYLE.equals(slot) || isKeyHelper) {
                refs.add(ref);
            }
        }
    }

    /**
     * Applies this essence to the stack. When slotOverride is "active", "passive", or "lifestyle",
     * uses only that slot's powers. When slotOverride is null, picks one slot at random (equal chance).
     * When random is null and slotOverride is null, uses all powers.
     */
    public ItemStack applyToItemStack(ItemStack stack, @Nullable Random random, @Nullable String slotOverride) {
        List<PowerReference> refs;
        if (slotOverride != null && (PowerSlotRegistry.SLOT_ACTIVE.equals(slotOverride)
                || PowerSlotRegistry.SLOT_PASSIVE.equals(slotOverride)
                || PowerSlotRegistry.SLOT_LIFESTYLE.equals(slotOverride))) {
            refs = new ArrayList<>(selectPowerRefsBySlot(slotOverride));
            appendSupplementalPowersForSlot(refs, slotOverride);
        } else if (random != null) {
            refs = selectPowerRefs(random);
        } else {
            return applyToItemStack(stack);
        }
        stack.applyComponentsFrom(toComponent(refs).build());
        return stack;
    }

    /** @deprecated Use {@link #applyToItemStack(ItemStack, Random, String)} */
    @Deprecated
    public ItemStack applyToItemStack(ItemStack stack, @Nullable Random random) {
        return applyToItemStack(stack, random, null);
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
            ", droppedBy=" + droppedBy +
            ", chance=" + chance +
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
                powers.add(powerReference.getPower());
            } catch (Exception e) {
                EssenceLib.LOGGER.error("Essence \"{}\" contained unregistered power \"{}\"!", id, powerReference.id());
            }
        }
    }
}
