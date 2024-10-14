package com.github.shap_po.essencelib.item;

import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.TrinketItem;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MobEssenceTrinketItem extends TrinketItem {
    public static final int INITIAL_DECAY_TIME = 60 * 20 * 30;

    public MobEssenceTrinketItem() {
        super(new Settings().maxDamage(1200).rarity(Rarity.RARE));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            Integer decayTime = stack.get(ModDataComponentTypes.DECAY_TIMER);
            if (decayTime == null) {
                initializeDecayTimer(stack, new ArrayList<>());
                decayTime = INITIAL_DECAY_TIME;
            } else {
                decayTime -= 1;
                stack.set(ModDataComponentTypes.DECAY_TIMER, decayTime);
            }
            if (decayTime <= 0) stack.decrement(1);
        }
    }

    public static void initializeDecayTimer(ItemStack stack, List<Text> tooltip) {
        stack.set(ModDataComponentTypes.DECAY_TIMER, INITIAL_DECAY_TIME);
        addPowerToItem(stack, "origins:throw_ender_pearl", tooltip);
        addPowerToItem(stack, "origins:water_breathing", tooltip);
    }

    private static void addPowerToItem(ItemStack stack, String powerId, List<Text> tooltip) {
        PowerReference powerReference = new PowerReference(Identifier.tryParse(powerId));
        ItemPowersComponent itemPowers = stack.getOrDefault(ApoliDataComponentTypes.POWERS, ItemPowersComponent.DEFAULT);
        itemPowers = ItemPowersComponent.builder(itemPowers)
            .add(EnumSet.of(AttributeModifierSlot.OFFHAND), powerReference.getId(), true, false)
            .build();
        stack.set(ApoliDataComponentTypes.POWERS, itemPowers);
    }
}
