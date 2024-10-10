package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModItems {
    public static final Item MOB_ESSENCE_ITEM = new MobEssenceTrinketItem();

    public static void register() {
        register("mob_essence", MOB_ESSENCE_ITEM, ItemGroups.COMBAT);
    }

    @SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, EssenceLib.identifier(id), item);
    }

    @SuppressWarnings({"SameParameterValue"})
    private static <I extends Item> void register(String name, I item, RegistryKey<ItemGroup> group) {
        register(name, item);
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> content.add(item));
    }
}
