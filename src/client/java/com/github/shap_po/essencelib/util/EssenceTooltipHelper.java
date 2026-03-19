package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipData;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shared tooltip building for essence items. Used by both the inventory tooltip
 * (custom EssenceTooltipComponent) and the floating label (EssenceFloatingTooltipRenderer).
 */
@Environment(EnvType.CLIENT)
public final class EssenceTooltipHelper {

    private static final String SEPARATOR = "─────────────────────";
    private static final int TOOLTIP_WRAP_WIDTH = 36;

    /**
     * Builds EssenceTooltipData for the custom two-column tooltip.
     * Used by Item.getTooltipData and EssenceFloatingTooltipRenderer.
     */
    public static Optional<EssenceTooltipData> buildTooltipData(ItemStack stack) {
        TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
        if (powers == null) return Optional.empty();
        boolean showExact = shouldShowExactTooltip(stack);

        Text name = stack.getName().copy().formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
        List<EssenceTooltipData.PowerEntry> powerEntries = new ArrayList<>();
        List<TrinketItemPowersComponent.Entry> entries = new ArrayList<>(
            ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries());
        List<TrinketItemPowersComponent.Entry> visible = entries.stream()
            .filter(e -> !e.hidden() && PowerManager.getNullable(e.powerId()) != null
                && PowerTooltipSlotRegistry.hasSlot(e.powerId()))
            .toList();

        addPowerEntries(visible, PowerTooltipSlotRegistry.SLOT_ACTIVE, "✦ Active", Formatting.RED, powerEntries, showExact);
        addPowerEntries(visible, PowerTooltipSlotRegistry.SLOT_PASSIVE, "✦ Passive", Formatting.GREEN, powerEntries, showExact);
        addPowerEntries(visible, PowerTooltipSlotRegistry.SLOT_LIFESTYLE, "✦ Lifestyle", Formatting.YELLOW, powerEntries, showExact);

        List<Text> stats = new ArrayList<>();
        if (showExact) {
            stats.add(Text.literal("Stats").formatted(Formatting.AQUA, Formatting.BOLD));
            stats.addAll(EssenceStatHelper.buildStatLinesGrouped(stack));
        }

        List<Text> footerHints = new ArrayList<>();
        if (ActiveEssenceHelper.hasActivePower(stack)) {
            footerHints.add(Text.translatable("tooltip.essencelib.shift_click_keybind").formatted(Formatting.GRAY, Formatting.ITALIC));
        }
        footerHints.add(Text.translatable("tooltip.essencelib.auto_equip_warning").formatted(Formatting.GOLD, Formatting.ITALIC));
        if (!showExact) {
            footerHints.add(Text.translatable("tooltip.essencelib.mage_appraisal_hint").formatted(Formatting.DARK_AQUA, Formatting.ITALIC));
        }

        return Optional.of(new EssenceTooltipData(name, powerEntries, stats, footerHints));
    }

    private static void addPowerEntries(List<TrinketItemPowersComponent.Entry> entries, String slot,
                                        String categoryLabel, Formatting color,
                                        List<EssenceTooltipData.PowerEntry> out, boolean showExact) {
        List<TrinketItemPowersComponent.Entry> inSlot = entries.stream()
            .filter(e -> slot.equals(PowerTooltipSlotRegistry.getSlot(e.powerId())))
            .toList();
        Text catText = Text.literal(categoryLabel).formatted(color);
        for (TrinketItemPowersComponent.Entry entry : inSlot) {
            Power power = PowerManager.getNullable(entry.powerId());
            if (power == null) continue;
            String desc = showExact
                ? (power.getDescription() != null ? power.getDescription().getString() : "")
                : EssenceWhisperHelper.getPowerFeel(entry.powerId());
            Text name = power.getName();
            out.add(new EssenceTooltipData.PowerEntry(catText, name, desc));
        }
    }

    private static boolean shouldShowExactTooltip(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        var player = MinecraftClient.getInstance().player;
        if (player != null && player.isCreative()) return true;
        return stack.getOrDefault(ModDataComponentTypes.IDENTIFIED, false);
    }

    /**
     * Builds flat essence tooltip lines (legacy, for LootBeamsTooltipManagerMixin / fallbacks).
     */
    public static List<Text> buildTooltipLines(ItemStack stack, boolean expanded, Text nameLine) {
        List<Text> tooltip = new ArrayList<>();
        TrinketItemPowersComponent powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
        if (powers == null) return tooltip;
        boolean showExact = shouldShowExactTooltip(stack);

        Text firstLine = nameLine != null ? nameLine : stack.getName();
        tooltip.add(firstLine.copy().formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        tooltip.add(Text.literal(SEPARATOR).formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal(""));

        List<TrinketItemPowersComponent.Entry> entries = new ArrayList<>(
            ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries());
        List<TrinketItemPowersComponent.Entry> visible = entries.stream()
            .filter(e -> !e.hidden() && PowerManager.getNullable(e.powerId()) != null
                && PowerTooltipSlotRegistry.hasSlot(e.powerId()))
            .toList();
        addPowersInSlot(visible, PowerTooltipSlotRegistry.SLOT_ACTIVE, "✦ Active", Formatting.RED, tooltip, expanded, showExact);
        addPowersInSlot(visible, PowerTooltipSlotRegistry.SLOT_PASSIVE, "✦ Passive", Formatting.GREEN, tooltip, expanded, showExact);
        addPowersInSlot(visible, PowerTooltipSlotRegistry.SLOT_LIFESTYLE, "✦ Lifestyle", Formatting.YELLOW, tooltip, expanded, showExact);

        if (showExact) {
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal(SEPARATOR).formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.literal("Stats").formatted(Formatting.AQUA, Formatting.BOLD));
            List<Text> statLines = EssenceStatHelper.buildStatLinesGrouped(stack);
            tooltip.addAll(statLines);
            if (!statLines.isEmpty()) tooltip.add(Text.literal(""));
        }

        if (expanded) {
            tooltip.add(Text.literal(SEPARATOR).formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.literal("  ► Hold Shift to pickup from ground").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));
        }

        return tooltip;
    }

    private static void addPowersInSlot(List<TrinketItemPowersComponent.Entry> entries, String slot,
                                        String categoryLabel, Formatting color, List<Text> tooltip, boolean expanded,
                                        boolean showExact) {
        List<TrinketItemPowersComponent.Entry> inSlot = entries.stream()
            .filter(e -> slot.equals(PowerTooltipSlotRegistry.getSlot(e.powerId())))
            .toList();
        for (TrinketItemPowersComponent.Entry entry : inSlot) {
            Power power = PowerManager.getNullable(entry.powerId());
            if (power == null) continue;

            tooltip.add(Text.literal(categoryLabel)
                .formatted(color)
                .append(Text.literal(":").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(" ").append(power.getName()).formatted(Formatting.WHITE)));

            boolean shouldShowDescription = expanded || !showExact;
            if (shouldShowDescription) {
                List<Text> wrapped = wrapTooltipText(
                    showExact
                        ? (power.getDescription() != null ? power.getDescription().getString() : "")
                        : EssenceWhisperHelper.getPowerFeel(entry.powerId()),
                    "  › ", "    ",
                    showExact ? Formatting.GRAY : Formatting.WHITE, showExact ? Formatting.ITALIC : Formatting.RESET);
                tooltip.addAll(wrapped);
            }
        }
    }

    private static List<Text> wrapTooltipText(String text, String firstLinePrefix, String continuationPrefix,
                                               Formatting... formattings) {
        List<Text> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        int contentMaxLen = TOOLTIP_WRAP_WIDTH - Math.max(firstLinePrefix.length(), continuationPrefix.length());
        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder(firstLinePrefix);
        String prefix = firstLinePrefix;
        for (String word : words) {
            int contentLen = current.length() - prefix.length();
            int needLen = (contentLen > 0 ? 1 : 0) + word.length();
            if (contentLen > 0 && contentLen + needLen > contentMaxLen) {
                lines.add(Text.literal(current.toString()).formatted(formattings));
                current = new StringBuilder(continuationPrefix);
                prefix = continuationPrefix;
            }
            if (current.length() > prefix.length()) current.append(" ");
            current.append(word);
        }
        if (current.length() > 0) {
            lines.add(Text.literal(current.toString()).formatted(formattings));
        }
        return lines;
    }
}
