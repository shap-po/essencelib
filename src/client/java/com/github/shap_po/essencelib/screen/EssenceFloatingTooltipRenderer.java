package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.tooltip.EssenceTooltipData;
import com.github.shap_po.essencelib.util.EssenceTooltipHelper;
import com.lootbeams.helpers.TargetHelper;
import com.lootbeams.helpers.ViewHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Renders a custom floating tooltip above essence items when the player looks at them.
 * Two-column layout: Name + Powers (left) | Stats (right). Shift: power descriptions only, stats hidden.
 */
@Environment(EnvType.CLIENT)
public class EssenceFloatingTooltipRenderer implements HudRenderCallback {

    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 11;
    private static final int PANEL_GAP = 8;
    private static final int POWER_SECTION_GAP = 2;
    /** Approx. chars per line when stats visible. */
    private static final int DESC_WRAP_CHARS = 26;
    private static final int POWERS_PANEL_BG = 0xF0181220;
    private static final int POWERS_PANEL_BORDER = 0xFF9B4DCF;
    private static final int POWERS_PANEL_ACCENT = 0x80483060;
    private static final int STATS_PANEL_BG = 0xF0140E18;
    private static final int STATS_PANEL_BORDER = 0xFF6B3D8A;

    @Override
    public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (!ViewHelper.shouldRenderCrosshair(client)) return;

        HitResult hit = TargetHelper.getEntityItem(client.player);
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;
        if (!(entityHit.getEntity() instanceof ItemEntity itemEntity)) return;

        ItemStack stack = itemEntity.getStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof MobEssenceTrinketItem)) return;

        Optional<EssenceTooltipData> dataOpt = EssenceTooltipHelper.buildTooltipData(stack);
        if (dataOpt.isEmpty()) return;

        boolean shift = client.options.sneakKey.isPressed();
        EssenceTooltipData data = dataOpt.get();

        float tickDelta = tickCounter.getTickDelta(false);
        Vec3d itemPos = itemEntity.getPos().add(0, 0.5, 0);
        Vector3f screenPos = ViewHelper.worldToScreenSpace(itemPos, tickDelta);

        int centerX = client.getWindow().getScaledWidth() / 2;
        int centerY = client.getWindow().getScaledHeight() / 2;
        int tooltipX = screenPos.z > 0 ? (int) screenPos.x : centerX;
        int tooltipY = screenPos.z > 0 ? (int) screenPos.y : centerY - 40;

        renderTooltipPanel(context, data, shift, tooltipX, tooltipY);
    }

    private void renderTooltipPanel(DrawContext context, EssenceTooltipData data, boolean shift, int centerX, int centerY) {
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        int statsW = data.stats().stream().mapToInt(t -> textRenderer.getWidth(t)).max().orElse(0);
        int statsPanelW = PADDING * 2 + statsW;
        int stretchPixels = shift ? statsPanelW + PANEL_GAP + computeMinPowersWidth(textRenderer, data) : 0;
        List<Line> powersLines = buildPowersLines(data, shift, textRenderer, stretchPixels);
        List<Line> statsLines = shift ? List.of() : data.stats().stream()
            .map(t -> new Line(t, getRgb(t)))
            .toList();

        int powersW = powersLines.stream().mapToInt(l -> textRenderer.getWidth(l.text)).max().orElse(0);
        int powersContentW = PADDING * 2 + powersW;
        int powersPanelW = shift ? powersContentW + PANEL_GAP + statsPanelW : powersContentW;
        int powersPanelH = PADDING * 2 + powersLines.size() * LINE_HEIGHT;
        int statsPanelH = statsLines.isEmpty() ? 0 : PADDING * 2 + statsLines.size() * LINE_HEIGHT;

        int totalWidth = powersPanelW + (statsLines.isEmpty() ? 0 : PANEL_GAP + statsPanelW);
        int totalHeight = Math.max(powersPanelH, statsPanelH);

        int left = centerX - totalWidth / 2;
        int top = centerY - totalHeight / 2;

        int screenW = context.getScaledWindowWidth();
        int screenH = context.getScaledWindowHeight();
        left = Math.max(4, Math.min(left, screenW - totalWidth - 4));
        top = Math.max(4, Math.min(top, screenH - totalHeight - 4));

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        // Panel 1: Powers (left)
        drawPanel(context, left, top, powersPanelW, powersPanelH, POWERS_PANEL_BG, POWERS_PANEL_BORDER, POWERS_PANEL_ACCENT);
        int yy = top + PADDING;
        for (Line line : powersLines) {
            context.drawTextWithShadow(textRenderer, line.text, left + PADDING, yy, line.color);
            yy += LINE_HEIGHT;
        }

        // Panel 2: Stats (right), disappears on Shift
        if (!statsLines.isEmpty()) {
            int statsLeft = left + powersPanelW + PANEL_GAP;
            drawPanel(context, statsLeft, top, statsPanelW, statsPanelH, STATS_PANEL_BG, STATS_PANEL_BORDER, 0x80483060);
            yy = top + PADDING;
            for (Line line : statsLines) {
                int lineW = textRenderer.getWidth(line.text);
                context.drawTextWithShadow(textRenderer, line.text, statsLeft + statsPanelW - PADDING - lineW, yy, line.color);
                yy += LINE_HEIGHT;
            }
        }

        matrices.pop();
    }

    private void drawPanel(DrawContext context, int x, int y, int w, int h, int bg, int border, int accent) {
        context.fill(x, y, x + w, y + h, bg);
        context.fill(x, y + 1, x + w - 1, y + 2, accent);
        context.fill(x - 1, y - 1, x + w + 1, y, border);
        context.fill(x - 1, y + h, x + w + 1, y + h + 1, border);
        context.fill(x - 1, y, x, y + h, border);
        context.fill(x + w, y, x + w + 1, y + h, border);
    }

    private int computeMinPowersWidth(net.minecraft.client.font.TextRenderer textRenderer, EssenceTooltipData data) {
        int max = textRenderer.getWidth(data.name());
        for (EssenceTooltipData.PowerEntry p : data.powers()) {
            int w = textRenderer.getWidth(Text.literal("").append(p.categoryLabel()).append(Text.literal(": ").formatted(Formatting.DARK_GRAY)).append(p.powerName()));
            max = Math.max(max, w);
        }
        return PADDING * 2 + max;
    }

    private List<Line> buildPowersLines(EssenceTooltipData data, boolean shift,
            net.minecraft.client.font.TextRenderer textRenderer, int stretchPixels) {
        List<Line> out = new ArrayList<>();
        out.add(new Line(data.name(), 0xFFE066FF));

        int wrapPixels = shift && stretchPixels > 0 ? stretchPixels - PADDING * 2 : 0;
        if (shift) {
            boolean first = true;
            for (EssenceTooltipData.PowerEntry p : data.powers()) {
                if (!first) out.add(new Line(Text.literal(""), 0));
                first = false;
                out.add(new Line(Text.literal("").append(p.categoryLabel()).append(Text.literal(": ").formatted(Formatting.DARK_GRAY)).append(p.powerName()), 0xFFF5F5F5));
                List<Text> wrapped = wrapWidth(p.description(), "  › ", "    ", wrapPixels, textRenderer);
                for (Text t : wrapped) {
                    out.add(new Line(t.copy().formatted(Formatting.GRAY, Formatting.ITALIC), 0xFFB8B0C0));
                }
            }
            out.add(new Line(Text.literal(""), 0));
            out.add(new Line(Text.literal("  ◆ Hold Shift to pickup from ground").formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC), 0xFFD966FF));
        } else {
            for (EssenceTooltipData.PowerEntry p : data.powers()) {
                out.add(new Line(Text.literal("").append(p.categoryLabel()).append(Text.literal(": ").formatted(Formatting.DARK_GRAY)).append(p.powerName()), 0xFFFFFF));
            }
        }
        return out;
    }

    private int getRgb(Text text) {
        return text.getStyle().getColor() != null ? text.getStyle().getColor().getRgb() : 0xFFFFFF;
    }

    /** Pixel-based wrap: scales to available width. When maxPixels <= 0, falls back to char-based. */
    private static List<Text> wrapWidth(String text, String firstPrefix, String contPrefix, int maxPixels,
            net.minecraft.client.font.TextRenderer textRenderer) {
        List<Text> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        if (maxPixels <= 0) {
            return wrap(text, firstPrefix, contPrefix, DESC_WRAP_CHARS);
        }
        int prefixW = Math.max(textRenderer.getWidth(firstPrefix), textRenderer.getWidth(contPrefix));
        int maxContentPixels = maxPixels - prefixW;
        if (maxContentPixels <= 0) return wrap(text, firstPrefix, contPrefix, DESC_WRAP_CHARS);

        String[] words = text.split("\\s+");
        StringBuilder cur = new StringBuilder(firstPrefix);
        String pref = firstPrefix;
        for (String w : words) {
            String toAdd = (cur.length() > pref.length() ? " " : "") + w;
            int curContentW = textRenderer.getWidth(cur.substring(pref.length()));
            int addW = textRenderer.getWidth(toAdd);
            if (cur.length() > pref.length() && curContentW + addW > maxContentPixels) {
                lines.add(Text.literal(cur.toString()));
                cur = new StringBuilder(contPrefix);
                pref = contPrefix;
                toAdd = w;
            }
            if (cur.length() > pref.length()) cur.append(" ");
            cur.append(w);
        }
        if (!cur.isEmpty()) lines.add(Text.literal(cur.toString()));
        return lines;
    }

    private static List<Text> wrap(String text, String firstPrefix, String contPrefix, int contentWidthChars) {
        List<Text> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        int maxContent = contentWidthChars - Math.max(firstPrefix.length(), contPrefix.length());
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
