package com.github.shap_po.essencelib.toast;

import com.github.shap_po.essencelib.registry.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UniqueKillToast implements Toast {
    private final Identifier entityId;

    public UniqueKillToast(Identifier entityId) {
        this.entityId = entityId;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        // 1.21+ moved toast textures/sprites; draw a stable custom panel instead of hardcoding old texture path.
        context.fill(0, 0, this.getWidth(), this.getHeight(), 0xE0100A14);
        context.fill(0, 0, this.getWidth(), 1, 0xFF8A4DCD);
        context.fill(0, this.getHeight() - 1, this.getWidth(), this.getHeight(), 0xFF6B3D8A);
        context.fill(0, 0, 1, this.getHeight(), 0xFF6B3D8A);
        context.fill(this.getWidth() - 1, 0, this.getWidth(), this.getHeight(), 0xFF6B3D8A);

        // Stable icon path: use spawn egg when available, otherwise use the essence item icon.
        EntityType<?> type = Registries.ENTITY_TYPE.getOrEmpty(entityId).orElse(null);
        ItemStack icon = new ItemStack(ModItems.MOB_ESSENCE_ITEM);
        if (type != null) {
            SpawnEggItem egg = SpawnEggItem.forEntity(type);
            if (egg != null) {
                icon = new ItemStack(egg);
            }
        }
        context.drawItem(icon, 8, 8);

        TextRenderer textRenderer = manager.getClient().textRenderer;
        Text title = Text.translatableWithFallback("toast.essencelib.unique_kill", "Unique Kill!")
            .formatted(Formatting.GOLD, Formatting.BOLD);
        Text mobName = type != null
            ? type.getName()
            : Text.literal(entityId.getPath().replace('_', ' ')).formatted(Formatting.GRAY);
        context.drawText(textRenderer, title, 30, 7, 0xFFFFFFFF, false);
        context.drawText(textRenderer, mobName, 30, 18, 0xFFDDDDDD, false);

        return startTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }
}
