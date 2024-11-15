package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SlotLinkedKeysGenerator {
    public static JsonObject createSlotLinkedKeys(int count) {
        JsonArray values = new JsonArray();

        for (int i = 0; i < count; i++) {
            values.add(createSlotLinkedKey(i, "soul", "essence"));
        }

        JsonObject object = new JsonObject();
        object.add("values", values);

        return object;
    }

    private static JsonObject createSlotLinkedKey(int index, String group, String slotName) {
        JsonObject slot = new JsonObject();
        slot.add("group", new JsonPrimitive(group));
        slot.add("slot", new JsonPrimitive(slotName));
        slot.add("index", new JsonPrimitive(index));

        JsonObject key = new JsonObject();
        key.add("key", new JsonPrimitive(slotTranslationKey(index)));

        JsonObject slotLinkedKey = new JsonObject();
        slotLinkedKey.add("slot", slot);
        slotLinkedKey.add("key", key);

        return slotLinkedKey;
    }

    public static String slotTranslationKey(int index) {
        return "key." + EssenceLib.MOD_ID + ".active.slot_" + index;
    }
}