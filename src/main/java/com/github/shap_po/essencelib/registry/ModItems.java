package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item MOB_ESSENCE_ITEM = new MobEssenceTrinketItem();
    public static final Item GUILLOTINE_ITEM = new BlockItem(ModBlocks.GUILLOTINE, new Item.Settings());
    public static final RegistryKey<ItemGroup> ESSENCES_GROUP_KEY =
        RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(EssenceLib.MOD_ID, "essences"));

    public static void register() {
        register("mob_essence", MOB_ESSENCE_ITEM, ItemGroups.COMBAT);
        register("guillotine", GUILLOTINE_ITEM, ItemGroups.FUNCTIONAL);
        registerCreativeEssenceGroup();
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

    private static void registerCreativeEssenceGroup() {
        Registry.register(
            Registries.ITEM_GROUP,
            ESSENCES_GROUP_KEY,
            FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.essencelib.essences"))
                .icon(() -> new ItemStack(MOB_ESSENCE_ITEM))
                .entries((context, entries) -> {
                    entries.add(new ItemStack(MOB_ESSENCE_ITEM));
                    EssenceManager.getAll().stream()
                        .sorted((a, b) -> a.getId().toString().compareTo(b.getId().toString()))
                        .forEach(essence -> {
                            // 1. Full Essence (All Powers)
                            ItemStack fullStack = essence.toItemStack();
                            fullStack.set(ModDataComponentTypes.IDENTIFIED, true);
                            fullStack.set(DataComponentTypes.CUSTOM_NAME, Text.of(essence.getName()));
                            entries.add(fullStack);

                            // 2. Slot Variants (Active, Passive, Lifestyle)
                            addSlotVariant(entries, essence, PowerSlotRegistry.SLOT_ACTIVE, "Active");
                            addSlotVariant(entries, essence, PowerSlotRegistry.SLOT_PASSIVE, "Passive");
                            addSlotVariant(entries, essence, PowerSlotRegistry.SLOT_LIFESTYLE, "Lifestyle");
                        });
                })
                .build()
        );
    }

    private static void addSlotVariant(ItemGroup.Entries entries, com.github.shap_po.essencelib.essence.Essence essence, String slotType, String suffix) {
        ItemStack stack = new ItemStack(MOB_ESSENCE_ITEM);
        // Apply only powers for this specific slot
        essence.applyToItemStack(stack, null, slotType);
        
        stack.set(ModDataComponentTypes.IDENTIFIED, true);
        
        MutableText name = Text.literal(essence.getName())
            .append(Text.literal(" (" + suffix + ")").formatted(Formatting.GRAY));
        stack.set(DataComponentTypes.CUSTOM_NAME, name);
        
        entries.add(stack);
    }
}
