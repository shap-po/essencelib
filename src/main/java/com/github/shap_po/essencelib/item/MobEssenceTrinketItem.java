package com.github.shap_po.essencelib.item;

import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.TrinketItem;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.PowerReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;


public class MobEssenceTrinketItem extends TrinketItem {
    private static final int INITIAL_DECAY_TIME = 60 * 20 * 30;

    public MobEssenceTrinketItem() {
        super(new Settings().maxDamage(1200).rarity(Rarity.RARE));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {


        Integer decayTime = stack.get(ModDataComponentTypes.DECAY_TIMER);
        if (decayTime == null) {
            initializeDecayTimer(stack, tooltip);
            decayTime = INITIAL_DECAY_TIME;
        }
        int minutes = (decayTime / 20) / 60;
        int seconds = (decayTime / 20) % 60;
        tooltip.add(Text.translatable("item.essencelib.timer", String.format("%d:%02d", minutes, seconds)));

        ItemPowersComponent itemPowers = stack.get(ApoliDataComponentTypes.POWERS);
        if (itemPowers != null) {
            if (Screen.hasShiftDown()) {
                addPowerDescriptionsToTooltip(itemPowers, tooltip);
            } else {
                tooltip.add(Text.literal("Hold SHIFT for more info").formatted(Formatting.GRAY));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void addPowerDescription(List<Text> tooltip, String label, Formatting labelColor, Identifier powerId) {
        PowerManager.getOptional(powerId).ifPresent(power -> {
            tooltip.add(Text.literal(label).formatted(labelColor)
                    .append(power.getName().formatted(Formatting.GOLD)));
            List<OrderedText> wrappedDescription = net.minecraft.client.MinecraftClient.getInstance().textRenderer.wrapLines(power.getDescription(), 150);
            for (OrderedText orderedText : wrappedDescription) {
                tooltip.add(Text.literal(orderedText.toString()));
            }
        });
    }



    private void addPowerToItem(ItemStack stack, String powerId, List<Text> tooltip) {
        PowerReference powerReference = new PowerReference(Identifier.tryParse(powerId));
        ItemPowersComponent itemPowers = stack.getOrDefault(ApoliDataComponentTypes.POWERS, ItemPowersComponent.DEFAULT);
        itemPowers = ItemPowersComponent.builder(itemPowers)
                .add(EnumSet.of(AttributeModifierSlot.OFFHAND), powerReference.getId(), true, false)
                .build();
        stack.set(ApoliDataComponentTypes.POWERS, itemPowers);
    }

    private void addPowerDescriptionsToTooltip(ItemPowersComponent itemPowers, List<Text> tooltip) {
        List<Identifier> powerIds = itemPowers.stream()
                .map(entry -> entry.powerId())
                .toList();

        if (!powerIds.isEmpty()) {
            addPowerDescription(tooltip, "Active: ", Formatting.RED, powerIds.getFirst());
        }

        if (powerIds.size() > 1) {
            addPowerDescription(tooltip, "Passive: ", Formatting.GREEN, powerIds.get(1));
        }
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

    private void initializeDecayTimer(ItemStack stack, List<Text> tooltip) {
        stack.set(ModDataComponentTypes.DECAY_TIMER, INITIAL_DECAY_TIME);
        addPowerToItem(stack, "origins:throw_ender_pearl", tooltip);
        addPowerToItem(stack, "origins:water_breathing", tooltip);
    }
}

