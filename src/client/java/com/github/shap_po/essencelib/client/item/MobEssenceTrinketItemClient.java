package com.github.shap_po.essencelib.client.item;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
import io.github.apace100.apoli.power.PowerManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MobEssenceTrinketItemClient {

    public static void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip) {
        Integer decayTime = stack.get(ModDataComponentTypes.DECAY_TIMER);
        if (decayTime == null) {
            MobEssenceTrinketItem.initializeDecayTimer(stack, tooltip);
            decayTime = MobEssenceTrinketItem.INITIAL_DECAY_TIME;
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

    private static void addPowerDescription(List<Text> tooltip, String label, Formatting labelColor, Identifier powerId) {
        PowerManager.getOptional(powerId).ifPresent(power -> {
            tooltip.add(Text.literal(label).formatted(labelColor)
                    .append(power.getName().formatted(Formatting.GOLD)));
            List<OrderedText> wrappedDescription = MinecraftClient.getInstance().textRenderer.wrapLines(power.getDescription(), 150);
            for (OrderedText orderedText : wrappedDescription) {
                tooltip.add(Text.literal(orderedText.toString()));
            }
        });
    }

    private static void addPowerDescriptionsToTooltip(ItemPowersComponent itemPowers, List<Text> tooltip) {
        List<Identifier> powerIds = itemPowers.stream()
                .map(entry -> entry.powerId())
                .toList();

        if (!powerIds.isEmpty()) {
            addPowerDescription(tooltip, "Active: ", Formatting.RED, powerIds.get(0));
        }

        if (powerIds.size() > 1) {
            addPowerDescription(tooltip, "Passive: ", Formatting.GREEN, powerIds.get(1));
        }
    }
}