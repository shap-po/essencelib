package com.github.shap_po.essencelib.mixinclient;

import com.github.shap_po.essencelib.util.LevelingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void drawLevelInfo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null) {
            int uniqueKills = LevelingUtil.getCurrentEntityCount(player);
            int totalEntities = LevelingUtil.getTotalEntityCount();
            int level = calculateLevel(uniqueKills, totalEntities);
            int requiredKills = getRequiredKillsForNextLevel(level, totalEntities);

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

    private int calculateLevel(int uniqueKills, int totalEntities) {
        if (totalEntities <= 0) return 0;

        int level = 1;
        int baseRequirement = totalEntities / 20;

        while (uniqueKills >= baseRequirement) {
            uniqueKills -= baseRequirement;
            level++;
            baseRequirement = (int) Math.pow(level, 2) * (totalEntities / 100);
        }

        return level;
    }

    private int getRequiredKillsForNextLevel(int currentLevel, int totalEntities) {
        if (totalEntities <= 0) return 0;

        return (int) Math.pow(currentLevel + 1, 2) * (totalEntities / 100);
    }
}