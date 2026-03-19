package com.github.shap_po.essencelib.tooltip;

import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Custom tooltip data for essence items. Enables two-column layout:
 * - Left: name + powers. Right: stats (hidden when Shift held).
 * - Shift: left shows power descriptions only, stats disappear.
 */
public record EssenceTooltipData(
    Text name,
    List<PowerEntry> powers,
    List<Text> stats,
    List<Text> footerHints
) implements TooltipData {

    public record PowerEntry(
        Text categoryLabel,
        Text powerName,
        String description
    ) {}
}
