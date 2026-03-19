package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.EssenceLibClient;
import com.github.shap_po.essencelib.util.ActiveEssenceHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class EssenceKeybindScreen extends Screen {

    private final Screen parent;

    public EssenceKeybindScreen(Screen parent) {
        super(Text.translatable("screen.essencelib.keybinds"));
        this.parent = parent;
    }

    /** Slots that have Active essence equipped (only these get keybind options). */
    private java.util.List<Integer> activeSlots() {
        if (client == null || client.player == null) return java.util.List.of();
        return ActiveEssenceHelper.getEquippedSlotsWithActivePower(client.player);
    }

    @Override
    protected void init() {
        java.util.List<Integer> slots = activeSlots();
        int y = 65; // Shifted down for panel header
        int rowHeight = 30;

        for (int i = 0; i < slots.size(); i++) {
            final int slot = slots.get(i);
            KeyBinding kb = EssenceLibClient.KEY_BINDINGS.get(slot);

            ButtonWidget changeBtn = ButtonWidget.builder(
                Text.translatable("screen.essencelib.keybind.change"),
                b -> {
                    if (client != null) {
                        client.setScreen(new KeybindSetScreen(this, slot, kb));
                    }
                }
            ).dimensions(width / 2 + 10, y + i * rowHeight, 100, 20).build();

            addDrawableChild(changeBtn);
        }

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            b -> close()
        ).dimensions(width / 2 - 60, height - 60, 120, 20).build());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark overlay without blur
        context.fill(0, 0, width, height, 0xE005050A);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        // --- LitRPG Panel Background ---
        int panelWidth = 240;
        int panelHeight = height - 60;
        int panelX = width / 2 - panelWidth / 2;
        int panelY = 30;
        
        // Solid background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF0161820);
        
        // Outer border (dark brown/bronze)
        int borderC = 0xFF5D4037;
        context.fill(panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY, borderC); // Top
        context.fill(panelX - 1, panelY + panelHeight, panelX + panelWidth + 1, panelY + panelHeight + 1, borderC); // Bottom
        context.fill(panelX - 1, panelY, panelX, panelY + panelHeight, borderC); // Left
        context.fill(panelX + panelWidth, panelY, panelX + panelWidth + 1, panelY + panelHeight, borderC); // Right
        
        // Inner Bevel
        int accentC = 0xFF8D6E63;
        context.fill(panelX, panelY, panelX + panelWidth, panelY + 1, accentC);
        context.fill(panelX, panelY, panelX + 1, panelY + panelHeight, accentC);
        
        // Corner Nodes
        int cornerColor = 0xFFFFD700;
        int cSize = 4;
        context.fill(panelX - 1, panelY - 1, panelX + cSize, panelY + cSize, cornerColor);
        context.fill(panelX + panelWidth - cSize, panelY - 1, panelX + panelWidth + 1, panelY + cSize, cornerColor);
        context.fill(panelX - 1, panelY + panelHeight - cSize, panelX + cSize, panelY + panelHeight + 1, cornerColor);
        context.fill(panelX + panelWidth - cSize, panelY + panelHeight - cSize, panelX + panelWidth + 1, panelY + panelHeight + 1, cornerColor);
        
        // Corner cutouts
        context.fill(panelX, panelY, panelX + 2, panelY + 2, 0xFF000000);
        context.fill(panelX + panelWidth - 2, panelY, panelX + panelWidth, panelY + 2, 0xFF000000);
        context.fill(panelX, panelY + panelHeight - 2, panelX + 2, panelY + panelHeight, 0xFF000000);
        context.fill(panelX + panelWidth - 2, panelY + panelHeight - 2, panelX + panelWidth, panelY + panelHeight, 0xFF000000);

        // Header Line
        context.fill(panelX + 10, panelY + 24, panelX + panelWidth - 10, panelY + 25, 0x80FFFFFF);

        context.drawTextWithShadow(textRenderer, title.copy().formatted(Formatting.GOLD, Formatting.BOLD), width / 2 - textRenderer.getWidth(title) / 2, panelY + 10, 0xFFFFFF);

        java.util.List<Integer> slots = activeSlots();
        if (slots.isEmpty()) {
            Text noActive = Text.translatable("screen.essencelib.keybind.no_active").formatted(Formatting.GRAY);
            context.drawTextWithShadow(textRenderer, noActive, width / 2 - textRenderer.getWidth(noActive) / 2, 70, 0xAAAAAA);
        } else {
            int y = 70; // Label Y matching the button Y + a bit for centering
            int rowHeight = 30;
            for (int i = 0; i < slots.size(); i++) {
                int slot = slots.get(i);
                KeyBinding kb = EssenceLibClient.KEY_BINDINGS.get(slot);
                String keyName = kb.getBoundKeyLocalizedText().getString();
                if (keyName.isEmpty()) keyName = "key.keyboard.unknown";
                Text label = Text.translatable("key.essencelib.active.slot_" + slot)
                    .append(Text.literal(": ").formatted(Formatting.GRAY))
                    .append(Text.literal(keyName).formatted(Formatting.YELLOW));
                context.drawTextWithShadow(textRenderer, label, width / 2 - 110, y + i * rowHeight + 1, 0xFFFFFF); // Left side
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) {
            client.options.write();
            client.setScreen(parent);
        }
    }

}
