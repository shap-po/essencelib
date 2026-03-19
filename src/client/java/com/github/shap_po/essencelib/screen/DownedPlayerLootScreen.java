package com.github.shap_po.essencelib.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import com.github.shap_po.essencelib.util.DownedIntelHelper;

/**
 * Client screen for looting a downed player's inventory.
 * Uses vanilla generic_54 container texture (5 rows + player inv).
 */
public class DownedPlayerLootScreen extends HandledScreen<DownedPlayerLootScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/gui/container/generic_54.png");
    private static final int PANEL_WIDTH = 62;

    public DownedPlayerLootScreen(DownedPlayerLootScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 222;
        this.backgroundWidth = 176;
        this.playerInventoryTitleY = 108;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        int panelLeft = x - PANEL_WIDTH - 4;
        int panelTop = y;
        int panelRight = x - 4;
        int panelBottom = y + 116;

        context.fill(panelLeft, panelTop, panelRight, panelBottom, 0xD0101010);
        context.fill(panelLeft, panelTop, panelRight, panelTop + 12, 0xC0202020);
        context.fill(panelLeft + 1, panelTop + 1, panelRight - 1, panelBottom - 1, 0x60202020);

        int textX = panelLeft + 6;
        int textY = panelTop + 4;
        context.drawText(textRenderer, Text.translatable("screen.essencelib.loot_intel.title").formatted(Formatting.AQUA, Formatting.BOLD), textX, textY, 0xFFFFFF, false);

        textY += 16;
        context.drawText(textRenderer, Text.translatable("screen.essencelib.loot_intel.occupied_slots"), textX, textY, 0xCFCFCF, false);
        textY += 10;
        context.drawText(textRenderer, Text.literal(String.valueOf(handler.getOccupiedEssenceSlots())).formatted(Formatting.GOLD), textX, textY, 0xFFFFFF, false);

        textY += 14;
        context.drawText(textRenderer, Text.translatable("screen.essencelib.loot_intel.killed_by"), textX, textY, 0xCFCFCF, false);
        textY += 10;
        context.drawText(textRenderer, resolveCauseLabel(handler.getCauseCode()).copy().formatted(Formatting.RED), textX, textY, 0xFFFFFF, false);

        textY += 14;
        context.drawText(textRenderer, Text.translatable("screen.essencelib.loot_intel.time_ago"), textX, textY, 0xCFCFCF, false);
        textY += 10;
        context.drawText(textRenderer, formatElapsed(handler.getDownedEpochSeconds()).copy().formatted(Formatting.YELLOW), textX, textY, 0xFFFFFF, false);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        context.drawText(textRenderer, Text.literal("Armor"), 8, 6, 0x808080, false);
        context.drawText(textRenderer, Text.literal("Offhand"), 8, 96, 0x808080, false);
        context.drawText(textRenderer, playerInventoryTitle, 8, playerInventoryTitleY, 0x404040, false);
    }

    private Text resolveCauseLabel(int causeCode) {
        return switch (causeCode) {
            case DownedIntelHelper.CAUSE_PLAYER -> Text.translatable("screen.essencelib.loot_intel.cause.player");
            case DownedIntelHelper.CAUSE_MOB -> Text.translatable("screen.essencelib.loot_intel.cause.mob");
            case DownedIntelHelper.CAUSE_PROJECTILE -> Text.translatable("screen.essencelib.loot_intel.cause.projectile");
            case DownedIntelHelper.CAUSE_FALL -> Text.translatable("screen.essencelib.loot_intel.cause.fall");
            case DownedIntelHelper.CAUSE_FIRE -> Text.translatable("screen.essencelib.loot_intel.cause.fire");
            case DownedIntelHelper.CAUSE_MAGIC -> Text.translatable("screen.essencelib.loot_intel.cause.magic");
            case DownedIntelHelper.CAUSE_EXPLOSION -> Text.translatable("screen.essencelib.loot_intel.cause.explosion");
            case DownedIntelHelper.CAUSE_VOID -> Text.translatable("screen.essencelib.loot_intel.cause.void");
            case DownedIntelHelper.CAUSE_DROWN -> Text.translatable("screen.essencelib.loot_intel.cause.drown");
            case DownedIntelHelper.CAUSE_STARVE -> Text.translatable("screen.essencelib.loot_intel.cause.starve");
            default -> Text.translatable("screen.essencelib.loot_intel.cause.unknown");
        };
    }

    private Text formatElapsed(int downedEpochSeconds) {
        if (downedEpochSeconds <= 0) {
            return Text.translatable("screen.essencelib.loot_intel.unknown_time");
        }
        long now = System.currentTimeMillis() / 1000L;
        long elapsed = Math.max(0L, now - (long) downedEpochSeconds);
        long minutes = elapsed / 60L;
        long seconds = elapsed % 60L;
        if (minutes > 0L) {
            return Text.translatable("screen.essencelib.loot_intel.time_minutes_seconds", minutes, seconds);
        }
        return Text.translatable("screen.essencelib.loot_intel.time_seconds", seconds);
    }
}
