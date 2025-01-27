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
        slot.add("slot", new JsonPrimitive(slotName));
        slot.add("index", new JsonPrimitive(index));

        JsonObject key = new JsonObject();
        key.add("key", new JsonPrimitive(slotTranslationKey(index)));

        JsonObject trinketKeyBinding = new JsonObject();
        trinketKeyBinding.add("slot", slot);
        trinketKeyBinding.add("key", key);

        return trinketKeyBinding;
    }

    public static String slotTranslationKey(int index) {
        return "key." + EssenceLib.MOD_ID + ".active.slot_" + index;
    }
}
