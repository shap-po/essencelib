package com.github.shap_po.essencelib.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class LevelUpToast implements Toast {
    private final int level;

    public LevelUpToast(int level) {
        this.level = level;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.fill(0, 0, this.getWidth(), this.getHeight(), 0xE0101410);
        context.fill(0, 0, this.getWidth(), 1, 0xFF55AA55);
        context.fill(0, this.getHeight() - 1, this.getWidth(), this.getHeight(), 0xFF2F7F2F);
        context.fill(0, 0, 1, this.getHeight(), 0xFF2F7F2F);
        context.fill(this.getWidth() - 1, 0, this.getWidth(), this.getHeight(), 0xFF2F7F2F);
        
        // Icon: Experience Bottle
        context.drawItem(new ItemStack(Items.EXPERIENCE_BOTTLE), 8, 8);

        TextRenderer textRenderer = manager.getClient().textRenderer;
        context.drawText(textRenderer, Text.translatable("toast.essencelib.level_up").formatted(Formatting.GREEN, Formatting.BOLD), 30, 7, 0xFF500050, false);
        context.drawText(textRenderer, Text.translatable("toast.essencelib.level_up.desc", level), 30, 18, 0xFF000000, false);

        return startTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }
}
