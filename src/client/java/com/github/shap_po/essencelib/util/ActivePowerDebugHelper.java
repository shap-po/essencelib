package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.mixin.TrinketItemPowersComponentAccessor;
import com.github.shap_po.shappoli.integration.trinkets.component.item.ShappoliTrinketsDataComponentTypes;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class ActivePowerDebugHelper {
    private static boolean enabled = false;
    private static final Map<String, Boolean> LAST_DOWN = new HashMap<>();

    public static void tick(
        MinecraftClient client,
        KeyBinding toggleKey,
        KeyBinding checkKey,
        List<KeyBinding> slotKeys
    ) {
        if (client.player == null) return;

        while (toggleKey.wasPressed()) {
            enabled = !enabled;
            send(client, "Essence debug " + (enabled ? "enabled" : "disabled") + ".");
        }

        while (checkKey.wasPressed()) {
            runCheck(client, slotKeys);
        }

        if (!enabled) return;

        for (int i = 0; i < slotKeys.size(); i++) {
            checkEdge(client, "slot_" + i, slotKeys.get(i), "Slot " + (i + 1));
        }
    }

    private static void runCheck(MinecraftClient client, List<KeyBinding> slotKeys) {
        if (client.player == null) return;
        List<Integer> activeSlots = ActiveEssenceHelper.getEquippedSlotsWithActivePower(client.player);
        boolean anyActive = !activeSlots.isEmpty();

        send(client, "Debug check: anyActive=" + anyActive + " activeSlots=" + activeSlots);

        for (int i = 0; i < slotKeys.size(); i++) {
            KeyBinding kb = slotKeys.get(i);
            boolean hasActiveInSlot = activeSlots.contains(i);
            send(client,
                "Slot " + (i + 1)
                    + " key=[" + kb.getBoundKeyLocalizedText().getString() + "]"
                    + " down=" + kb.isPressed()
                    + " hasActive=" + hasActiveInSlot);
        }

        // Keybind watcher: show hidden *_key powers per equipped essence slot.
        TrinketsApi.getTrinketComponent(client.player).ifPresent(comp -> {
            var soul = comp.getInventory().get("soul");
            if (soul == null) return;
            var essence = soul.get("essence");
            if (essence == null) return;

            for (int i = 0; i < essence.size(); i++) {
                ItemStack stack = essence.getStack(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof MobEssenceTrinketItem)) continue;
                var powers = stack.get(ShappoliTrinketsDataComponentTypes.TRINKET_POWERS);
                if (powers == null) {
                    send(client, "Slot " + (i + 1) + " has no trinket power component.");
                    continue;
                }
                var entries = ((TrinketItemPowersComponentAccessor) (Object) powers).getEntries();
                StringBuilder keyPowers = new StringBuilder();
                for (var entry : entries) {
                    Identifier id = entry.powerId();
                    if (id != null && id.getPath().endsWith("_key")) {
                        if (!keyPowers.isEmpty()) keyPowers.append(", ");
                        keyPowers.append(id);
                    }
                }
                if (keyPowers.isEmpty()) {
                    send(client, "Slot " + (i + 1) + " has no *_key power entry.");
                } else {
                    send(client, "Slot " + (i + 1) + " key powers: " + keyPowers);
                }
            }
        });
    }

    private static void checkEdge(MinecraftClient client, String id, KeyBinding key, String label) {
        boolean down = key.isPressed();
        boolean wasDown = LAST_DOWN.getOrDefault(id, false);
        if (down && !wasDown) {
            String msg = label + " pressed (" + key.getBoundKeyLocalizedText().getString() + ")";
            send(client, msg);
            EssenceLib.LOGGER.info("[EssenceDebug] {}", msg);
        }
        LAST_DOWN.put(id, down);
    }

    private static void send(MinecraftClient client, String message) {
        var player = client.player;
        if (player != null) {
            player.sendMessage(Text.literal("[EssenceDebug] " + message), false);
        }
        EssenceLib.LOGGER.info("[EssenceDebug] {}", message);
    }

    private ActivePowerDebugHelper() {}
}
