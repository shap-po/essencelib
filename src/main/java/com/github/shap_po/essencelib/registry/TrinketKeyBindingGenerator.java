package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TrinketKeyBindingGenerator {
    public static JsonObject createTrinketKeyBindings(int count) {
        JsonArray values = new JsonArray();

        for (int i = 0; i < count; i++) {
            values.add(createTrinketKeyBinding(i, "soul", "essence"));
        }

        JsonObject object = new JsonObject();
        object.add("values", values);

        return object;
    }

    private static JsonObject createTrinketKeyBinding(int index, String group, String slotName) {
        JsonObject slot = new JsonObject();
        slot.add("group", new JsonPrimitive(group));
        slot.add("name", new JsonPrimitive(slotName));
        slot.add("index", new JsonPrimitive(index));

        JsonArray slots = new JsonArray();
        slots.add(slot);

        JsonArray keys = new JsonArray();
        // Add both continuous and non-continuous versions for slot-specific key
        keys.add(createKeyRef(slotTranslationKey(index), false));
        keys.add(createKeyRef(slotTranslationKey(index), true));

        JsonObject trinketKeyBinding = new JsonObject();
        trinketKeyBinding.add("slots", slots);
        trinketKeyBinding.add("keys", keys);

        return trinketKeyBinding;
    }

    private static JsonObject createKeyRef(String keyTranslation, boolean continuous) {
        JsonObject key = new JsonObject();
        key.add("key", new JsonPrimitive(keyTranslation));
        key.add("category", new JsonPrimitive(EssenceLib.KEYBINDINGS_CATEGORY));
        key.add("continuous", new JsonPrimitive(continuous));
        return key;
    }

    public static String slotTranslationKey(int index) {
        return "key." + EssenceLib.MOD_ID + ".active.slot_" + index;
    }

    public static String trinketKeyBindingId() {
        return EssenceLib.MOD_ID;
    }
}
