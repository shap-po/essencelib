package com.github.shap_po.essencelib.item;

import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import io.github.apace100.apoli.component.item.ApoliDataComponentTypes;
import io.github.apace100.apoli.component.item.ItemPowersComponent;
import io.github.apace100.apoli.power.PowerManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MobEssenceTooltips {

    private static final int MAX_LINE_LENGTH = 30;

    public static void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip) {
        tooltip.clear(); // Clear the current tooltip

        ItemPowersComponent itemPowers = stack.get(ApoliDataComponentTypes.POWERS);
        if (itemPowers != null) {
            addPowerDescriptionsToTooltip(itemPowers, tooltip);
        }
    }

    private static void addPowerDescription(List<Text> tooltip, String label, Formatting labelColor, Identifier powerId) {
        PowerManager.getOptional(powerId).ifPresent(power -> {
            tooltip.add(Text.literal(label).formatted(labelColor)
                    .append(power.getName().formatted(Formatting.GOLD)));
            List<String> wrappedDescription = wrapText(power.getDescription().getString(), MAX_LINE_LENGTH);
            for (String line : wrappedDescription) {
                tooltip.add(Text.literal(line).formatted(Formatting.GRAY));
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
        if (powerIds.size() > 2) {
            addPowerDescription(tooltip, "LifeStyle: ", Formatting.YELLOW, powerIds.get(2));
        }
    }

    private static List<String> wrapText(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }
}
