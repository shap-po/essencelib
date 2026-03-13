package com.github.shap_po.essencelib.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders essence tooltip with two-column layout:
 * - Powers (left) | Stats (right). Stats disappear when Shift held.
 * - Shift: power descriptions only, wider wrap, stats hidden
 */
@Environment(EnvType.CLIENT)
public class EssenceTooltipComponent implements TooltipComponent {

    private static final int LINE_HEIGHT = 10;
    private static final int PADDING = 4;
    private static final int COLUMN_GAP = 16;
    private static final int DESC_WRAP_WIDTH = 26;
    private static final int DESC_WRAP_WIDTH_SHIFT = 42;

    private final EssenceTooltipData data;

    public EssenceTooltipComponent(EssenceTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        var textRenderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        boolean shift = Screen.hasShiftDown();
        List<Line> powersLines = computePowersLines(textRenderer, shift);
        List<Line> statsLines = shift ? List.of() : data.stats().stream()
            .map(t -> new Line(t, getRgb(t)))
            .toList();
        return PADDING * 2 + Math.max(powersLines.size(), statsLines.size()) * LINE_HEIGHT;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        boolean shift = Screen.hasShiftDown();
        List<Line> powersLines = computePowersLines(textRenderer, shift);
        int powersW = powersLines.stream().mapToInt(l -> textRenderer.getWidth(l.text)).max().orElse(0);
        int statsW = shift ? 0 : data.stats().stream()
            .mapToInt(t -> textRenderer.getWidth(t))
            .max().orElse(0);
        return PADDING * 2 + powersW + (statsW > 0 ? COLUMN_GAP + statsW : 0);
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        boolean shift = Screen.hasShiftDown();
        List<Line> powersLines = computePowersLines(textRenderer, shift);
        List<Line> statsLines = shift ? List.of() : data.stats().stream()
            .map(t -> new Line(t, getRgb(t)))
            .toList();

        int powersW = powersLines.stream().mapToInt(l -> textRenderer.getWidth(l.text)).max().orElse(0);
        int statsW = statsLines.stream().mapToInt(l -> textRenderer.getWidth(l.text)).max().orElse(0);
        int rightColX = x + PADDING + powersW + COLUMN_GAP;

        int yy = y + PADDING;
        int maxRows = Math.max(powersLines.size(), statsLines.size());
        for (int i = 0; i < maxRows; i++) {
            if (i < powersLines.size()) {
                Line line = powersLines.get(i);
                drawLine(textRenderer, line.text, x + PADDING, yy, line.color, matrix, vertexConsumers);
            }
            if (i < statsLines.size()) {
                Line line = statsLines.get(i);
                int lineW = textRenderer.getWidth(line.text);
                drawLine(textRenderer, line.text, rightColX + statsW - lineW, yy, line.color, matrix, vertexConsumers);
            }
            yy += LINE_HEIGHT;
        }
    }

    private List<Line> computePowersLines(TextRenderer textRenderer, boolean shift) {
        List<Line> out = new ArrayList<>();
        out.add(new Line(data.name().copy().formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), 0xFFDD55FF));

        int wrapWidth = shift ? DESC_WRAP_WIDTH_SHIFT : DESC_WRAP_WIDTH;
        if (shift) {
            for (EssenceTooltipData.PowerEntry p : data.powers()) {
                out.add(new Line(Text.literal("").append(p.categoryLabel()).append(Text.literal(": ").formatted(Formatting.DARK_GRAY)).append(p.powerName()), 0xFFFFFF));
                List<Text> wrapped = wrap(p.description(), "  › ", "    ", wrapWidth);
                for (Text t : wrapped) {
                    out.add(new Line(t.copy().formatted(Formatting.GRAY, Formatting.ITALIC), 0xAAAAAA));
                }
            }
            out.add(new Line(Text.literal("  ► Hold Shift to pickup from ground").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC), 0xDD55FF));
        } else {
            for (EssenceTooltipData.PowerEntry p : data.powers()) {
                out.add(new Line(Text.literal("").append(p.categoryLabel()).append(Text.literal(": ").formatted(Formatting.DARK_GRAY)).append(p.powerName()), 0xFFFFFF));
            }
        }
        return out;
    }

    private void drawLine(TextRenderer textRenderer, Text text, int x, int y, int color, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        textRenderer.draw(text, (float) x, (float) y, color, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    private int getRgb(Text text) {
        return text.getStyle().getColor() != null ? text.getStyle().getColor().getRgb() : 0xFFFFFF;
    }

    private static List<Text> wrap(String text, String firstPrefix, String contPrefix, int contentWidth) {
        List<Text> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        int maxContent = contentWidth - Math.max(firstPrefix.length(), contPrefix.length());
        String[] words = text.split("\\s+");
        StringBuilder cur = new StringBuilder(firstPrefix);
        String pref = firstPrefix;
        for (String w : words) {
            int contentLen = cur.length() - pref.length();
            int need = (contentLen > 0 ? 1 : 0) + w.length();
            if (contentLen > 0 && contentLen + need > maxContent) {
                lines.add(Text.literal(cur.toString()));
                cur = new StringBuilder(contPrefix);
                pref = contPrefix;
            }
            if (cur.length() > pref.length()) cur.append(" ");
            cur.append(w);
        }
        if (!cur.isEmpty()) lines.add(Text.literal(cur.toString()));
        return lines;
    }

    private record Line(Text text, int color) {}
}
