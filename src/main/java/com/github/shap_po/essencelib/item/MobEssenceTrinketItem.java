package com.github.shap_po.essencelib.item;

import dev.emi.trinkets.api.TrinketItem;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import java.util.EnumSet;
import java.util.List;

public class MobEssenceTrinketItem extends TrinketItem {
    public MobEssenceTrinketItem() {
        super(
                new Settings().maxDamage(1200).rarity(Rarity.RARE)
        );
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.getStackInHand(Hand.OFF_HAND) == stack) {
                stack.setDamage(0);
                if (stack.getDamage() >= stack.getMaxDamage())
                    stack.decrement(1);
            }
            stack.setDamage(stack.getDamage() + 1);
        }
        addPowerToItem(stack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return false;
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int remainingTicks = stack.getMaxDamage() - stack.getDamage();
        int remainingSeconds = remainingTicks / 20; // Convert ticks to seconds
        tooltip.add(Text.translatable("item.essencelib.timer", remainingSeconds));
    }
    private void addPowerToItem(ItemStack stack) {
        PowerReference powerReference = new PowerReference(Identifier.tryParse("origins:water_breathing")); // Replace with your power ID
        ItemPowersComponent itemPowers = stack.getOrDefault(ApoliDataComponentTypes.POWERS, ItemPowersComponent.DEFAULT);
        stack.set(ApoliDataComponentTypes.POWERS, ItemPowersComponent.builder(itemPowers)
                .add(EnumSet.of(AttributeModifierSlot.OFFHAND), powerReference.getId(), false, false)
                .build());
    }
}
