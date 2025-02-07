package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class ManaAttributeRegistry {
    private static EntityAttribute maxMana;
    private static EntityAttribute currentMana;
    private static EntityAttribute manaRegen;

    public static final RegistryKey<EntityAttribute> MAX_MANA = RegistryKey.of(RegistryKeys.ATTRIBUTE, EssenceLib.identifier("max_mana"));
    public static final RegistryKey<EntityAttribute> CURRENT_MANA = RegistryKey.of(RegistryKeys.ATTRIBUTE, EssenceLib.identifier("current_mana"));
    public static final RegistryKey<EntityAttribute> MANA_REGEN = RegistryKey.of(RegistryKeys.ATTRIBUTE, EssenceLib.identifier("mana_regen"));

    public static void register() {
        maxMana = Registry.register(Registries.ATTRIBUTE, 
            EssenceLib.identifier("max_mana"), 
            new ClampedEntityAttribute(
                "attribute.essencelib.max_mana",
                100.0, // default value
                0.0,   // minimum value
                1000.0 // maximum value
            ).setTracked(true));
        currentMana = Registry.register(Registries.ATTRIBUTE, 
            EssenceLib.identifier("current_mana"), 
            new ClampedEntityAttribute(
                "attribute.name.generic.current_mana", 
                100.0, 
                0.0, 
                1000.0)
                .setTracked(true));
        manaRegen = Registry.register(Registries.ATTRIBUTE, 
            EssenceLib.identifier("mana_regen"), 
            new ClampedEntityAttribute(

                "attribute.name.generic.mana_regen",
                1.0,
                0.0, 
                100.0)
                .setTracked(true));
    }


    public static EntityAttribute getMaxMana() { return maxMana; }
    public static EntityAttribute getCurrentMana() { return currentMana; }
    public static EntityAttribute getManaRegen() { return manaRegen; }

    public static RegistryEntry<EntityAttribute> getMaxManaEntry() {
        if (maxMana == null) {
            register();
        }
        return Registries.ATTRIBUTE.entryOf(MAX_MANA);
    }

    public static RegistryEntry<EntityAttribute> getCurrentManaEntry() {
        if (currentMana == null) {
            register();
        }
        return Registries.ATTRIBUTE.entryOf(CURRENT_MANA);
    }

    public static RegistryEntry<EntityAttribute> getManaRegenEntry() {
        if (manaRegen == null) {
            register();
        }
        return Registries.ATTRIBUTE.entryOf(MANA_REGEN);
    }

    public static void initialize() {
        // Registration happens through static initializers
    }
} 