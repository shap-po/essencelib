package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.level.LevelManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LevelInfoRenderer {
    private static final Identifier BADGE_TEXTURE = EssenceLib.identifier("textures/gui/badge.png");

    public static void renderInInventory(DrawContext context, int x, int y, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || player.isCreative()) {
            return;
        }

        int level = LevelManager.getLevel(player);

        int badgeX = x + 77;
        int badgeY = y + 26;

        // Draw badge texture
        context.drawTexture(BADGE_TEXTURE, badgeX, badgeY, 0, 0, 16, 16, 16, 16);

        // Draw level text in the middle of the badge with shadow and outline
        String levelText = String.valueOf(level);
        int textWidth = client.textRenderer.getWidth(levelText);
        int textX = badgeX + (17 - textWidth) / 2;
        int textY = badgeY + (17 - client.textRenderer.fontHeight) / 2;

        // Draw black outline (double thickness)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (Math.abs(dx) + Math.abs(dy) > 2) continue; // Skip corners to make the outline smoother
                context.drawText(client.textRenderer, Text.literal(levelText), textX + dx, textY + dy, 0x000000, false);
            }
        }

        // Draw main text with shadow
        context.drawTextWithShadow(client.textRenderer, Text.literal(levelText), textX, textY, 0xFFFFFF);

        // Add hover-over functionality
        if (mouseX >= badgeX && mouseX <= badgeX + 16 &&
            mouseY >= badgeY && mouseY <= badgeY + 16) {
            if (level >= LevelManager.MAX_LEVEL) {
                context.drawTooltip(client.textRenderer, Text.literal("You have reached the maximum level!"), mouseX, mouseY);
                return;
            }

            int requiredKills = LevelManager.getRequiredKills(level + 1);
            int uniqueKills = LevelManager.getCurrentUniqueKillsCount(player);

            context.drawTooltip(client.textRenderer, Text.literal(uniqueKills + " / " + requiredKills + " Unique kills for next level"),
                mouseX, mouseY);
        }
    }
}
