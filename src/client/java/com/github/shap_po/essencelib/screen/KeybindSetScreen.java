package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.EssenceLibClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

/**
 * Small popup when shift+right-clicking an essence slot. Has Cancel and Set buttons.
 * Click Set to enter listening mode; after ~1 sec, the next key/mouse press is assigned.
 */
@Environment(EnvType.CLIENT)
public class KeybindSetScreen extends Screen {

    private static final int PANEL_W = 220;
    private static final int PANEL_H = 90;
    private static final int PADDING = 12;
    private static final int LISTENING_DELAY_TICKS = 20;

    private final Screen parent;
    private final int slot;
    private final KeyBinding keyBinding;
    private boolean listening = false;
    private int listeningTicks = 0;

    public KeybindSetScreen(Screen parent, int slot, KeyBinding keyBinding) {
        super(Text.translatable("screen.essencelib.keybind.set_title"));
        this.parent = parent;
        this.slot = slot;
        this.keyBinding = keyBinding;
    }

    @Override
    protected void init() {
        if (listening) return;

        int cx = width / 2;
        int by = height / 2 + PANEL_H / 2 - PADDING - 20;
        int btnW = 90;
        int gap = 10;

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cancel"), b -> close())
            .dimensions(cx - btnW - gap / 2, by, btnW, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.essencelib.keybind.set_button"), b -> startListening())
            .dimensions(cx + gap / 2, by, btnW, 20).build());
    }

    private void startListening() {
        listening = true;
        listeningTicks = 0;
        clearChildren();
    }

    @Override
    public void tick() {
        if (listening) listeningTicks++;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark overlay without blur
        context.fill(0, 0, width, height, 0xE005050A);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int cx = width / 2;
        int cy = height / 2;
        int left = cx - PANEL_W / 2;
        int top = cy - PANEL_H / 2;

        // --- LitRPG Popup Panel ---
        context.fill(left, top, left + PANEL_W, top + PANEL_H, 0xF0161820); // BG
        
        int borderC = 0xFF5D4037; // Bronze
        int accentC = 0xFF8D6E63;
        
        // Borders
        context.fill(left - 1, top - 1, left + PANEL_W + 1, top, borderC); // T
        context.fill(left - 1, top + PANEL_H, left + PANEL_W + 1, top + PANEL_H + 1, borderC); // B
        context.fill(left - 1, top, left, top + PANEL_H, borderC); // L
        context.fill(left + PANEL_W, top, left + PANEL_W + 1, top + PANEL_H, borderC); // R
        
        // Inner highlight
        context.fill(left, top, left + PANEL_W, top + 1, accentC);
        context.fill(left, top, left + 1, top + PANEL_H, accentC);
        
        // Golden Nodes
        int cColor = 0xFFFFD700;
        context.fill(left - 2, top - 2, left + 2, top + 2, cColor);
        context.fill(left + PANEL_W - 2, top - 2, left + PANEL_W + 2, top + 2, cColor);
        context.fill(left - 2, top + PANEL_H - 2, left + 2, top + PANEL_H + 2, cColor);
        context.fill(left + PANEL_W - 2, top + PANEL_H - 2, left + PANEL_W + 2, top + PANEL_H + 2, cColor);

        // Corner cutouts
        context.fill(left - 1, top - 1, left + 1, top + 1, 0xFF000000);
        context.fill(left + PANEL_W - 1, top - 1, left + PANEL_W + 1, top + 1, 0xFF000000);
        context.fill(left - 1, top + PANEL_H - 1, left + 1, top + PANEL_H + 1, 0xFF000000);
        context.fill(left + PANEL_W - 1, top + PANEL_H - 1, left + PANEL_W + 1, top + PANEL_H + 1, 0xFF000000);

        if (!listening) {
            Text title = Text.translatable("screen.essencelib.keybind.set_title").formatted(Formatting.GOLD, Formatting.BOLD);
            Text slotLabel = Text.translatable("key.essencelib.active.slot_" + slot).formatted(Formatting.GRAY);
            int tx = cx - textRenderer.getWidth(title) / 2;
            int ty = top + PADDING;
            context.drawTextWithShadow(textRenderer, title, tx, ty, 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, slotLabel, cx - textRenderer.getWidth(slotLabel) / 2, ty + 14, 0xAAAAAA);
        } else {
            Text prompt = Text.translatable("screen.essencelib.keybind.press_key").formatted(Formatting.YELLOW);
            Text hint = listeningTicks < LISTENING_DELAY_TICKS
                ? Text.translatable("screen.essencelib.keybind.waiting").formatted(Formatting.GRAY)
                : Text.translatable("screen.essencelib.keybind.press_key.hint").formatted(Formatting.GRAY);
            context.drawTextWithShadow(textRenderer, prompt, cx - textRenderer.getWidth(prompt) / 2, top + PADDING + 8, 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, hint, cx - textRenderer.getWidth(hint) / 2, top + PADDING + 22, 0xAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void applyKey(InputUtil.Key key) {
        keyBinding.setBoundKey(key);
        KeyBinding.updateKeysByCode();
        if (client != null) {
            client.options.write();
            client.setScreen(parent);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        if (!listening || listeningTicks < LISTENING_DELAY_TICKS) return true;
        applyKey(InputUtil.fromKeyCode(keyCode, scanCode));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!listening || listeningTicks < LISTENING_DELAY_TICKS) return super.mouseClicked(mouseX, mouseY, button);
        if (button >= 0) {
            applyKey(InputUtil.Type.MOUSE.createFromCode(button));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
