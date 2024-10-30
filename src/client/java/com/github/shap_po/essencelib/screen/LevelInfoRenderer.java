package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.util.LevelingUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class LevelInfoRenderer {
    public static void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null) {
            int uniqueKills = LevelingUtil.getCurrentEntityCount(player);
            int totalEntities = LevelingUtil.getTotalEntityCount();

            int level = LevelingUtil.getLevel(uniqueKills, totalEntities);
            int requiredKills = LevelingUtil.getKillsToNextLevel(level, totalEntities);

            String levelText = "Level: " + level;
            int x = context.getScaledWindowWidth() / 2 + 90;
            int y = context.getScaledWindowHeight() / 2 - 77;

            context.drawText(client.textRenderer, Text.literal(levelText), x, y, 0xFFFFFF, true);

            if (mouseX >= x && mouseX <= x + client.textRenderer.getWidth(levelText) &&
                mouseY >= y && mouseY <= y + client.textRenderer.fontHeight) {
                context.drawTooltip(client.textRenderer, Text.literal(uniqueKills + " / " + requiredKills + " kills for next level"),
                    mouseX, mouseY);
            }
        }
    }
}
