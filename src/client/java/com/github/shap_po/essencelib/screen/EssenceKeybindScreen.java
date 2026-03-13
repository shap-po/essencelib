package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.EssenceLibClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class EssenceKeybindScreen extends Screen {

    private final Screen parent;

    public EssenceKeybindScreen(Screen parent) {
        super(Text.translatable("screen.essencelib.keybinds"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int y = 40;
        int rowHeight = 25;
        int maxSlots = Math.min(EssenceLib.MAX_SLOT_COUNT, EssenceLibClient.KEY_BINDINGS.size());

        for (int i = 0; i < maxSlots; i++) {
            final int slot = i;
            KeyBinding kb = EssenceLibClient.KEY_BINDINGS.get(i);

            ButtonWidget changeBtn = ButtonWidget.builder(
                Text.translatable("screen.essencelib.keybind.change"),
                b -> {
                    if (client != null) {
                        client.setScreen(new ListeningOverlay(this, slot, kb));
                    }
                }
            ).dimensions(width / 2 - 100, y + i * rowHeight, 200, 20).build();

            addDrawableChild(changeBtn);
        }

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            b -> close()
        ).dimensions(width / 2 - 100, height - 29, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFF);

        int y = 44;
        int rowHeight = 25;
        int maxSlots = Math.min(EssenceLib.MAX_SLOT_COUNT, EssenceLibClient.KEY_BINDINGS.size());

        for (int i = 0; i < maxSlots; i++) {
            KeyBinding kb = EssenceLibClient.KEY_BINDINGS.get(i);
            String keyName = kb.getBoundKeyLocalizedText().getString();
            if (keyName.isEmpty()) keyName = "key.keyboard.unknown";
            Text label = Text.translatable("key.essencelib.active.slot_" + i)
                .append(Text.literal(": ").formatted(Formatting.GRAY))
                .append(Text.literal(keyName).formatted(Formatting.YELLOW));
            context.drawTextWithShadow(textRenderer, label, width / 2 - 100, y + i * rowHeight, 0xFFFFFF);
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

    /** Small overlay that captures the next key press and assigns it to the slot's KeyBinding. */
    @Environment(EnvType.CLIENT)
    public static class ListeningOverlay extends Screen {
        private final EssenceKeybindScreen parent;
        private final int slot;
        private final KeyBinding keyBinding;

        public ListeningOverlay(EssenceKeybindScreen parent, int slot, KeyBinding keyBinding) {
            super(Text.translatable("screen.essencelib.keybind.press_key"));
            this.parent = parent;
            this.slot = slot;
            this.keyBinding = keyBinding;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBackground(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 20, 0xFFFFFF);
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("screen.essencelib.keybind.press_key.hint").formatted(Formatting.GRAY),
                width / 2, height / 2, 0xAAAAAA
            );
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                close();
                return true;
            }
            InputUtil.Key key = InputUtil.fromKeyCode(keyCode, scanCode);
            keyBinding.setBoundKey(key);
            KeyBinding.updateKeysByCode();
            if (client != null) {
                client.options.write();
                client.setScreen(parent);
            }
            return true;
        }

        @Override
        public void close() {
            if (client != null) client.setScreen(parent);
        }
    }
}
