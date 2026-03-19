package com.github.shap_po.essencelib.screen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.github.shap_po.essencelib.client.ClientPowerHudData;
import com.github.shap_po.essencelib.component.DownedComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.PowerUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consolidated, labeled resource tracker to avoid stacked anonymous Apoli bars.
 */
public final class EssenceResourceHudRenderer implements HudRenderCallback {

    private static final int BASE_BAR_W = 52;
    private static final int BASE_BAR_H = 4;
    private static final int BASE_GAP = 2;
    private static final int PADDING = 2;
    private static final int DEFAULT_RIGHT_MARGIN = 8;
    private static final int DEFAULT_BOTTOM_MARGIN = 54;

    private static final int TRACK_BG = 0x5C101822;
    private static final int BORDER = 0x882A3550;
    private static final int TEXT_COLOR = 0xBDE6F8FF;
    private static final Gson GSON = new Gson();
    private static final String HUD_STYLE_CLASSPATH = "/data/essencelib/power_hud_styles.json";

    private static int hudX = -1;
    private static int hudY = -1;
    private static int sizePreset = 1; // 0=tiny, 1=small, 2=medium, 3=large
    private static boolean configLoaded = false;
    private static boolean stylesLoaded = false;
    private static boolean dragging = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static final Map<Identifier, HudStyle> STYLE_OVERRIDES = new HashMap<>();

    private record ActiveTrack(String label, float ratio, int startColor, int endColor, int trackBgColor, int borderColor, ItemStack iconStack) {}
    private record HudStyle(String label, Integer max, Integer startColor, Integer endColor, Integer trackBgColor, Integer borderColor, @Nullable Item iconItem, boolean enabled) {}

    @Override
    public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        if (DownedComponent.isDowned(player)) return;
        ensureStylesLoaded(client);

        List<ActiveTrack> active = collectActiveTracks(player);
        if (active.isEmpty()) return;

        int boxH = getBoxHeight(active.size());
        int boxW = getBoxWidth();
        ensureHudPosition(client, boxW, boxH);
        int x = clamp(hudX, 2, Math.max(2, client.getWindow().getScaledWidth() - boxW - 2));
        int y = clamp(hudY, 2, Math.max(2, client.getWindow().getScaledHeight() - boxH - 2));
        hudX = x;
        hudY = y;
        context.fill(x - PADDING, y - PADDING, x + boxW, y + boxH, 0x4010141C);

        var text = client.textRenderer;
        for (int i = 0; i < active.size(); i++) {
            ActiveTrack t = active.get(i);
            int barW = getBarWidth();
            int barH = getBarHeight();
            int icon = getIconSize();
            int rowH = getRowHeight();
            int yy = y + i * rowH;
            int barX = x + icon + 2;
            int filled = Math.max(0, Math.min(barW, (int) (barW * t.ratio())));
            int barY = yy + 7;

            drawScaledItem(context, t.iconStack(), x, yy + 1, icon);
            context.fill(barX, barY, barX + barW, barY + barH, t.trackBgColor());
            context.drawBorder(barX, barY, barW, barH, t.borderColor());
            if (filled > 0) {
                for (int px = 0; px < filled; px++) {
                    float p = (float) px / (barW - 1);
                    int c = lerpColor(t.startColor(), t.endColor(), p);
                    context.fill(barX + px, barY, barX + px + 1, barY + barH, c);
                }
            }

            context.drawText(text, Text.literal(t.label()), barX, yy, TEXT_COLOR, false);
        }
    }

    public static void renderInventoryEditorHint(DrawContext context, int screenW, int screenH) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        tickInventoryDrag(client);
        List<ActiveTrack> active = collectActiveTracks(player);
        if (active.isEmpty()) return;
        int boxW = getBoxWidth();
        int boxH = getBoxHeight(active.size());
        ensureHudPosition(client, boxW, boxH);
        context.drawBorder(hudX - 1, hudY - 1, boxW + 2, boxH + 2, 0x70B6D7FF);
        context.drawText(client.textRenderer, Text.literal("L-drag move | R-click size"), hudX, hudY - 9, 0x90D7EEFF, false);
    }

    public static boolean onInventoryMouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return false;
        ensureStylesLoaded(client);
        List<ActiveTrack> active = collectActiveTracks(player);
        if (active.isEmpty()) return false;
        int boxW = getBoxWidth();
        int boxH = getBoxHeight(active.size());
        ensureHudPosition(client, boxW, boxH);
        int mx = (int) mouseX;
        int my = (int) mouseY;
        if (mx >= hudX - PADDING && mx <= hudX + boxW + PADDING && my >= hudY - PADDING && my <= hudY + boxH + PADDING) {
            if (button == 0) {
                dragging = true;
                dragOffsetX = mx - hudX;
                dragOffsetY = my - hudY;
                return true;
            }
            if (button == 1) {
                sizePreset = (sizePreset + 1) % 4;
                saveConfig(client);
                return true;
            }
        }
        return false;
    }

    public static boolean onInventoryMouseReleased(int button) {
        if (!dragging || button != 0) return false;
        dragging = false;
        saveConfig(MinecraftClient.getInstance());
        return true;
    }

    private static List<ActiveTrack> collectActiveTracks(ClientPlayerEntity player) {
        List<ActiveTrack> out = new ArrayList<>();
        if (STYLE_OVERRIDES.isEmpty()) {
            return out;
        }
        for (var entry : STYLE_OVERRIDES.entrySet()) {
            Identifier powerId = entry.getKey();
            HudStyle style = entry.getValue();
            if (!style.enabled()) {
                continue;
            }
            Integer current = getResourceValue(player, powerId);
            if (current == null) continue;
            int max = style.max() != null ? style.max() : 100;
            String label = style.label() != null ? style.label() : powerId.getPath();
            int startColor = style.startColor() != null ? style.startColor() : 0xD06DE9FF;
            int endColor = style.endColor() != null ? style.endColor() : 0xD02D8EFF;
            int trackBg = style.trackBgColor() != null ? style.trackBgColor() : TRACK_BG;
            int border = style.borderColor() != null ? style.borderColor() : BORDER;
            Item icon = style.iconItem() != null ? style.iconItem() : Items.EXPERIENCE_BOTTLE;
            int clamped = Math.max(0, Math.min(current, max));
            float ratio = max <= 0 ? 0f : (float) clamped / (float) max;
            out.add(new ActiveTrack(label, ratio, startColor, endColor, trackBg, border, new ItemStack(icon)));
        }
        return out;
    }

    private static @Nullable Integer getResourceValue(ClientPlayerEntity player, Identifier powerId) {
        // Only show bars for powers currently held by this player.
        Power power = PowerManager.getNullable(powerId);
        if (power == null) return null;
        PowerType type = PowerUtil.getNullablePowerType(power, player);
        if (type == null) return null;
        Integer synced = ClientPowerHudData.get(powerId);
        if (synced != null) {
            return Math.max(0, synced);
        }
        // Fallback prevents bars disappearing if sync data misses one id.
        return Math.max(0, PowerUtil.getResourceValue(type));
    }

    private static int lerpColor(int start, int end, float t) {
        int sa = (start >>> 24) & 0xFF;
        int sr = (start >>> 16) & 0xFF;
        int sg = (start >>> 8) & 0xFF;
        int sb = start & 0xFF;
        int ea = (end >>> 24) & 0xFF;
        int er = (end >>> 16) & 0xFF;
        int eg = (end >>> 8) & 0xFF;
        int eb = end & 0xFF;
        int a = (int) (sa + (ea - sa) * t);
        int r = (int) (sr + (er - sr) * t);
        int g = (int) (sg + (eg - sg) * t);
        int b = (int) (sb + (eb - sb) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int getBoxWidth() {
        return getBarWidth() + getIconSize() + 8;
    }

    private static int getBoxHeight(int activeCount) {
        int rowH = getRowHeight();
        return PADDING * 2 + (activeCount * rowH) - getGap();
    }

    private static int getCurrentBoxHeight(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return getBoxHeight(1);
        List<ActiveTrack> active = collectActiveTracks(player);
        return getBoxHeight(Math.max(1, active.size()));
    }

    private static void ensureHudPosition(MinecraftClient client, int boxW, int boxH) {
        if (!configLoaded) loadConfig(client);
        if (hudX >= 0 && hudY >= 0) return;
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        hudX = Math.max(2, sw - boxW - DEFAULT_RIGHT_MARGIN);
        hudY = Math.max(2, sh - boxH - DEFAULT_BOTTOM_MARGIN);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void loadConfig(MinecraftClient client) {
        configLoaded = true;
        Path path = getConfigPath(client);
        if (!Files.exists(path)) return;
        try {
            String raw = Files.readString(path, StandardCharsets.UTF_8);
            JsonObject obj = GSON.fromJson(raw, JsonObject.class);
            if (obj != null) {
                if (obj.has("x")) hudX = obj.get("x").getAsInt();
                if (obj.has("y")) hudY = obj.get("y").getAsInt();
                if (obj.has("size")) sizePreset = clamp(obj.get("size").getAsInt(), 0, 3);
            }
        } catch (Exception ignored) {
        }
    }

    private static void saveConfig(MinecraftClient client) {
        try {
            Path path = getConfigPath(client);
            Files.createDirectories(path.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("x", hudX);
            obj.addProperty("y", hudY);
            obj.addProperty("size", sizePreset);
            Files.writeString(path, GSON.toJson(obj), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private static Path getConfigPath(MinecraftClient client) {
        return client.runDirectory.toPath().resolve("config").resolve("essencelib_resource_hud.json");
    }

    private static void ensureStylesLoaded(MinecraftClient client) {
        if (stylesLoaded) return;
        stylesLoaded = true;
        STYLE_OVERRIDES.clear();
        try (var stream = EssenceResourceHudRenderer.class.getResourceAsStream(HUD_STYLE_CLASSPATH)) {
            if (stream == null) return;
            String raw = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject root = GSON.fromJson(raw, JsonObject.class);
            if (root == null) return;
            loadStyles(root);
        } catch (Exception ignored) {}
    }

    private static void loadStyles(JsonObject root) {
        for (var entry : root.entrySet()) {
            Identifier id = Identifier.tryParse(entry.getKey());
            if (id == null || !entry.getValue().isJsonObject()) continue;
            JsonObject obj = entry.getValue().getAsJsonObject();
            String label = obj.has("label") ? obj.get("label").getAsString() : null;
            Integer max = obj.has("max") ? obj.get("max").getAsInt() : null;
            Integer start = obj.has("start_color") ? parseColor(obj.get("start_color")) : null;
            Integer end = obj.has("end_color") ? parseColor(obj.get("end_color")) : null;
            Integer trackBg = obj.has("track_bg_color") ? parseColor(obj.get("track_bg_color")) : null;
            Integer border = obj.has("border_color") ? parseColor(obj.get("border_color")) : null;
            Item iconItem = obj.has("icon_item") ? parseItem(obj.get("icon_item").getAsString()) : null;
            boolean enabled = !obj.has("enabled") || obj.get("enabled").getAsBoolean();
            STYLE_OVERRIDES.put(id, new HudStyle(label, max, start, end, trackBg, border, iconItem, enabled));
        }
    }

    private static @Nullable Integer parseColor(JsonElement e) {
        try {
            if (e == null) return null;
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) {
                return e.getAsInt();
            }
            String s = e.getAsString().trim();
            if (s.isEmpty()) return null;
            if (s.startsWith("#")) s = s.substring(1);
            long parsed = Long.parseLong(s, 16);
            if (s.length() == 6) {
                parsed |= 0xFF000000L;
            }
            return (int) parsed;
        } catch (Exception ex) {
            return null;
        }
    }

    private static @Nullable Item parseItem(String itemId) {
        try {
            Identifier id = Identifier.tryParse(itemId);
            if (id == null) return null;
            return Registries.ITEM.getOrEmpty(id).orElse(null);
        } catch (Exception ex) {
            return null;
        }
    }

    private static void tickInventoryDrag(MinecraftClient client) {
        if (!dragging) return;
        int boxW = getBoxWidth();
        int boxH = getCurrentBoxHeight(client);
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int mx = (int) (client.mouse.getX() * sw / (double) client.getWindow().getWidth());
        int my = (int) (client.mouse.getY() * sh / (double) client.getWindow().getHeight());
        int nx = mx - dragOffsetX;
        int ny = my - dragOffsetY;
        hudX = clamp(nx, 2, Math.max(2, sw - boxW - 2));
        hudY = clamp(ny, 2, Math.max(2, sh - boxH - 2));
    }

    private static int getBarWidth() {
        return switch (sizePreset) {
            case 0 -> BASE_BAR_W - 6;
            case 1 -> BASE_BAR_W;
            case 2 -> BASE_BAR_W + 14;
            default -> BASE_BAR_W + 26;
        };
    }

    private static int getBarHeight() {
        return switch (sizePreset) {
            case 0 -> BASE_BAR_H;
            case 1 -> BASE_BAR_H;
            case 2 -> BASE_BAR_H + 1;
            default -> BASE_BAR_H + 2;
        };
    }

    private static int getGap() {
        return switch (sizePreset) {
            case 0 -> BASE_GAP;
            case 1 -> BASE_GAP + 1;
            default -> BASE_GAP + 2;
        };
    }

    private static int getIconSize() {
        return switch (sizePreset) {
            case 0 -> 7;
            case 1 -> 9;
            case 2 -> 11;
            default -> 13;
        };
    }

    private static int getRowHeight() {
        return Math.max(getIconSize() + 1, getBarHeight() + 7) + getGap();
    }

    private static void drawScaledItem(DrawContext context, ItemStack stack, int x, int y, int size) {
        float s = size / 16.0f;
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0.0f);
        matrices.scale(s, s, 1.0f);
        context.drawItem(stack, 0, 0);
        matrices.pop();
    }
}
