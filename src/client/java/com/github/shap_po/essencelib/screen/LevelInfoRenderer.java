package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.level.LevelManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LevelInfoRenderer {
    private static final Identifier BADGE_TEXTURE = EssenceLib.identifier("textures/gui/badge.png");

    // Match essence tooltip styling
    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 11;
    private static final int PANEL_BG = 0xF0181220;
    private static final int PANEL_BORDER = 0xFF9B4DCF;
    private static final int PANEL_ACCENT = 0x80483060;
    private static final int PROGRESS_BAR_HEIGHT = 6;
    private static final int PROGRESS_BAR_FILL = 0xFF9B4DCF;
    private static final int PROGRESS_BAR_BG = 0x80483060;
    private static final int TITLE_COLOR = 0xFF9B4DCF;
    private static final int FOOTER_SCALE = 75; // percent

    public static void renderInInventory(DrawContext context, int x, int y, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || player.isCreative()) {
            return;
        }

        int level = LevelManager.getLevel(player);
        int totalKills = LevelManager.getCurrentUniqueKillsCount(player);

        int badgeX = x + 77;
        int badgeY = y + 26;

        // Draw badge texture
        context.drawTexture(BADGE_TEXTURE, badgeX, badgeY, 0, 0, 16, 16, 16, 16);

        // Draw level text in the middle of the badge with shadow and outline
        String levelText = String.valueOf(level);
        int textWidth = client.textRenderer.getWidth(levelText);
        int textX = badgeX + (17 - textWidth) / 2;
        int textY = badgeY + (17 - client.textRenderer.fontHeight) / 2;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (Math.abs(dx) + Math.abs(dy) > 2) continue;
                context.drawText(client.textRenderer, Text.literal(levelText), textX + dx, textY + dy, 0x000000, false);
            }
        }
        context.drawTextWithShadow(client.textRenderer, Text.literal(levelText), textX, textY, 0xFFFFFF);

        // Custom tooltip panel when hovering
        if (isOverBadge(x, y, mouseX, mouseY)) {
            renderLevelTooltip(context, client, level, totalKills, mouseX, mouseY);
        }
    }

    private static void renderLevelTooltip(DrawContext context, MinecraftClient client, int level, int totalKills, int mouseX, int mouseY) {
        var textRenderer = client.textRenderer;

        // Build content
        String title = Text.translatable("screen.essencelib.level_progress").getString();
        String levelLine = Text.translatable("screen.essencelib.level_tooltip.level", level, LevelManager.MAX_LEVEL).getString();
        String slotsLine = level == 1
            ? Text.translatable("screen.essencelib.level_tooltip.slots_singular").getString()
            : Text.translatable("screen.essencelib.level_tooltip.slots_plural", level).getString();

        int requiredForNext = level >= LevelManager.MAX_LEVEL ? 0 : LevelManager.getRequiredKills(level + 1);
        int requiredForCurrent = level == 0 ? 0 : LevelManager.getRequiredKills(level);
        float progress = 0f;
        String progressText;
        if (level >= LevelManager.MAX_LEVEL) {
            progressText = Text.translatable("screen.essencelib.level_tooltip.max").getString();
            progress = 1f;
        } else if (level == 0) {
            progress = requiredForNext > 0 ? (float) totalKills / requiredForNext : 0f;
            progressText = Text.translatable("screen.essencelib.level_tooltip.kills", totalKills, requiredForNext).getString();
        } else {
            int neededForNext = requiredForNext - requiredForCurrent;
            int currentProgress = totalKills - requiredForCurrent;
            progress = neededForNext > 0 ? (float) currentProgress / neededForNext : 0f;
            progress = Math.min(1f, Math.max(0f, progress));
            progressText = Text.translatable("screen.essencelib.level_tooltip.kills", currentProgress, neededForNext).getString();
        }

        String footer = Text.translatable("tooltip.essencelib.level_badge").getString();

        // Measure dimensions
        int titleW = textRenderer.getWidth(title);
        int levelW = textRenderer.getWidth(levelLine);
        int slotsW = textRenderer.getWidth(slotsLine);
        int progressTextW = textRenderer.getWidth(progressText);
        int footerW = (int) (textRenderer.getWidth(footer) * FOOTER_SCALE / 100f);

        int barWidth = 120;
        int contentW = Math.max(Math.max(titleW, levelW), Math.max(slotsW, Math.max(progressTextW, barWidth)));
        int panelW = PADDING * 2 + contentW;
        int panelH = PADDING * 2
            + LINE_HEIGHT * 3  // title, level, slots
            + 4 + PROGRESS_BAR_HEIGHT  // bar
            + 4 + LINE_HEIGHT  // progress text
            + 6 + (int) (LINE_HEIGHT * FOOTER_SCALE / 100f);  // footer

        // Position: offset from cursor, keep on screen
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY + 12;
        int screenW = context.getScaledWindowWidth();
        int screenH = context.getScaledWindowHeight();
        if (tooltipX + panelW > screenW - 4) tooltipX = mouseX - panelW - 12;
        if (tooltipY + panelH > screenH - 4) tooltipY = screenH - panelH - 4;
        if (tooltipX < 4) tooltipX = 4;
        if (tooltipY < 4) tooltipY = 4;

        // Draw panel (essence-style)
        drawPanel(context, tooltipX, tooltipY, panelW, panelH);

        int yy = tooltipY + PADDING;

        // Title
        context.drawTextWithShadow(textRenderer, Text.literal(title).formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), tooltipX + PADDING, yy, TITLE_COLOR);
        yy += LINE_HEIGHT;

        // Level & slots
        context.drawTextWithShadow(textRenderer, Text.literal(levelLine), tooltipX + PADDING, yy, 0xFFFFFF);
        yy += LINE_HEIGHT;
        context.drawTextWithShadow(textRenderer, Text.literal(slotsLine).formatted(Formatting.GRAY), tooltipX + PADDING, yy, 0xAAAAAA);
        yy += LINE_HEIGHT + 4;

        // Progress bar (LitRPG Style)
        int barX = tooltipX + PADDING;
        // Dark metallic outer border
        context.fill(barX - 1, yy - 1, barX + barWidth + 1, yy + PROGRESS_BAR_HEIGHT + 1, 0xFF2A2A35);
        // Inner shadow background
        context.fill(barX, yy, barX + barWidth, yy + PROGRESS_BAR_HEIGHT, 0xFF111115);
        
        int fillW = (int) (barWidth * progress);
        if (fillW > 0) {
            // Bright base fill
            context.fill(barX, yy, barX + fillW, yy + PROGRESS_BAR_HEIGHT, PROGRESS_BAR_FILL);
            // Top highlight for 3D glassy effect
            context.fill(barX, yy, barX + fillW, yy + 1, 0x60FFFFFF);
            // Bottom shadow
            context.fill(barX, yy + PROGRESS_BAR_HEIGHT - 1, barX + fillW, yy + PROGRESS_BAR_HEIGHT, 0x40000000);
            
            // Add vertical segment ticks every 20px (for that blocky/RPG tick look)
            for (int tickX = 20; tickX < fillW; tickX += 20) {
                context.fill(barX + tickX, yy, barX + tickX + 1, yy + PROGRESS_BAR_HEIGHT, 0x40000000);
            }
        }
        
        // Add framing corner dots for progress bar
        context.fill(barX - 2, yy - 1, barX, yy + 1, 0xFFD4AF37); // TL dot
        context.fill(barX + barWidth, yy - 1, barX + barWidth + 2, yy + 1, 0xFFD4AF37); // TR dot
        context.fill(barX - 2, yy + PROGRESS_BAR_HEIGHT - 1, barX, yy + PROGRESS_BAR_HEIGHT + 1, 0xFFD4AF37); // BL dot
        context.fill(barX + barWidth, yy + PROGRESS_BAR_HEIGHT - 1, barX + barWidth + 2, yy + PROGRESS_BAR_HEIGHT + 1, 0xFFD4AF37); // BR dot
        
        yy += PROGRESS_BAR_HEIGHT + 4;

        // Progress text
        context.drawTextWithShadow(textRenderer, Text.literal(progressText).formatted(Formatting.GRAY), tooltipX + PADDING, yy, 0xAAAAAA);
        yy += LINE_HEIGHT + 6;

        // Footer (fine print)
        context.getMatrices().push();
        context.getMatrices().translate(tooltipX + PADDING, yy, 0);
        context.getMatrices().scale(FOOTER_SCALE / 100f, FOOTER_SCALE / 100f, 1f);
        context.getMatrices().translate(-(tooltipX + PADDING), -yy, 0);
        context.drawTextWithShadow(textRenderer, Text.literal(footer).formatted(Formatting.GRAY, Formatting.ITALIC), tooltipX + PADDING, yy, 0x888888);
        context.getMatrices().pop();
    }

    private static void drawPanel(DrawContext context, int x, int y, int w, int h) {
        // RPG Panel Base
        context.fill(x, y, x + w, y + h, PANEL_BG);
        
        // Inner Bevel (Accent)
        context.fill(x, y, x + w, y + 1, PANEL_ACCENT);
        context.fill(x, y, x + 1, y + h, PANEL_ACCENT);
        
        // Main Border (Purple Outer)
        context.fill(x - 1, y - 1, x + w + 1, y, PANEL_BORDER);
        context.fill(x - 1, y + h, x + w + 1, y + h + 1, PANEL_BORDER);
        context.fill(x - 1, y, x, y + h, PANEL_BORDER);
        context.fill(x + w, y, x + w + 1, y + h, PANEL_BORDER);
        
        // LitRPG Corner Nodes (Gold)
        int cornerColor = 0xFFFFD700;
        context.fill(x - 2, y - 2, x + 1, y + 1, cornerColor); // TL
        context.fill(x + w - 1, y - 2, x + w + 2, y + 1, cornerColor); // TR
        context.fill(x - 2, y + h - 1, x + 1, y + h + 2, cornerColor); // BL
        context.fill(x + w - 1, y + h - 1, x + w + 2, y + h + 2, cornerColor); // BR
        
        // Inner cutouts for depth
        context.fill(x - 1, y - 1, x, y, 0xFF000000); // TL
        context.fill(x + w, y - 1, x + w + 1, y, 0xFF000000); // TR
        context.fill(x - 1, y + h, x, y + h + 1, 0xFF000000); // BL
        context.fill(x + w, y + h, x + w + 1, y + h + 1, 0xFF000000); // BR
    }

    /** Returns true if the mouse is over the level badge. */
    public static boolean isOverBadge(int screenX, int screenY, int mouseX, int mouseY) {
        int badgeX = screenX + 77;
        int badgeY = screenY + 26;
        return mouseX >= badgeX && mouseX <= badgeX + 16 && mouseY >= badgeY && mouseY <= badgeY + 16;
    }
}
