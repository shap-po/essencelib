package com.github.shap_po.essencelib.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders essence tooltip with two-column layout:
 * - Left: name, then each power with its description underneath.
 * - Right: stats. No shift-to-expand; descriptions always visible.
 */
@Environment(EnvType.CLIENT)
public class EssenceTooltipComponent implements TooltipComponent {

    private static final int LINE_HEIGHT = 10;
    private static final int PADDING = 4;
    private static final int COLUMN_GAP = 4;
    private static final int DESC_WRAP_WIDTH = 42;
    private static final int FOOTER_GAP = 4;
    private static final int FOOTER_LINE_HEIGHT = 6;
    private static final float FOOTER_SCALE = 0.75f;

    private final EssenceTooltipData data;

    public EssenceTooltipComponent(EssenceTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        var textRenderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        List<Line> powersLines = computePowersLines(textRenderer);
        List<Line> statsLines = data.stats().stream()
            .map(t -> new Line(t, getRgb(t)))
            .toList();
        int mainHeight = Math.max(powersLines.size(), statsLines.size()) * LINE_HEIGHT;
        int footerHeight = data.footerHints().isEmpty() ? 0 : FOOTER_GAP + data.footerHints().size() * FOOTER_LINE_HEIGHT;
        return PADDING * 2 + mainHeight + footerHeight;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        List<Line> powersLines = computePowersLines(textRenderer);
        int powersW = powersLines.stream().mapToInt(l -> textRenderer.getWidth(l.text)).max().orElse(0);
        int statsW = data.stats().stream()
            .mapToInt(t -> textRenderer.getWidth(t))
            .max().orElse(0);
        int footerW = data.footerHints().stream()
            .mapToInt(textRenderer::getWidth)
            .max().orElse(0);
        int footerWScaled = (int) (footerW * FOOTER_SCALE);
        int mainW = powersW + (statsW > 0 ? COLUMN_GAP + statsW : 0);
        return PADDING * 2 + Math.max(mainW, footerWScaled);
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        List<Line> powersLines = computePowersLines(textRenderer);
        List<Line> statsLines = data.stats().stream()
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

        if (!data.footerHints().isEmpty()) {
            yy += FOOTER_GAP;
            int fx = x + PADDING;
            for (Text hint : data.footerHints()) {
                int rgb = hint.getStyle().getColor() != null ? hint.getStyle().getColor().getRgb() : 0xAAAAAA;
                drawLineScaled(textRenderer, hint, fx, yy, rgb, matrix, vertexConsumers);
                yy += FOOTER_LINE_HEIGHT;
            }
        }
    }

    private List<Line> computePowersLines(TextRenderer textRenderer) {
        List<Line> out = new ArrayList<>();
        out.add(new Line(data.name().copy().formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), 0xFFDD55FF));

        for (EssenceTooltipData.PowerEntry p : data.powers()) {
            out.add(new Line(Text.literal("").append(p.categoryLabel()).append(Text.literal(": ").formatted(Formatting.DARK_GRAY)).append(p.powerName()), 0xFFFFFF));
            List<Text> wrapped = wrap(p.description(), "  › ", "    ", DESC_WRAP_WIDTH);
            for (Text t : wrapped) {
                out.add(new Line(t.copy().formatted(Formatting.GRAY, Formatting.ITALIC), 0xAAAAAA));
            }
        }
        return out;
    }

    private void drawLine(TextRenderer textRenderer, Text text, int x, int y, int color, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        textRenderer.draw(text, (float) x, (float) y, color, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    /** Draws footer text at 75% scale (fine print). */
    private void drawLineScaled(TextRenderer textRenderer, Text text, int x, int y, int color, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        Matrix4f scaled = new Matrix4f(matrix).translate(x, y, 0).scale(FOOTER_SCALE).translate(-x, -y, 0);
        textRenderer.draw(text, (float) x, (float) y, color, false, scaled, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
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
