package com.github.shap_po.essencelib.loot.function;

import com.github.shap_po.essencelib.EssenceLib;
import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModLootFunctionTypes {
    public static final LootFunctionType<SetEssenceLootFunction> SET_ESSENCE = register("set_essence", SetEssenceLootFunction.CODEC);

    public static void register() {
    }

    @SuppressWarnings({"SameParameterValue"})
    private static <T extends LootFunction> LootFunctionType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, EssenceLib.identifier(id), new LootFunctionType<T>(codec));
    }
}
