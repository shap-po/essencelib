package com.github.shap_po.essencelib.item;

import dev.emi.trinkets.api.TrinketItem;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.EnumSet;

public class MobEssenceTrinketItem extends TrinketItem {
    public MobEssenceTrinketItem() {
        super(
            new Settings()
                .maxCount(1)
                .rarity(Rarity.RARE)

        );
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
                    addPowerToItem(stack);
                }

    private void addPowerToItem(ItemStack stack) {
        PowerReference powerReference = new PowerReference(Identifier.tryParse("origins:water_breathing")); // Replace with your power ID
        ItemPowersComponent itemPowers = stack.getOrDefault(ApoliDataComponentTypes.POWERS, ItemPowersComponent.DEFAULT);
        stack.set(ApoliDataComponentTypes.POWERS, ItemPowersComponent.builder(itemPowers)
                .add(EnumSet.of(AttributeModifierSlot.OFFHAND), powerReference.getId(), false, false)
                .build());
    }
}
