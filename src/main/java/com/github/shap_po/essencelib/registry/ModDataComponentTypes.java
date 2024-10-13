package com.github.shap_po.essencelib.registry;


import com.github.shap_po.essencelib.EssenceLib;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<Integer> DECAY_TIMER =
            register("decay_timer", builder -> builder.codec(Codec.INT));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(EssenceLib.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        EssenceLib.LOGGER.info("Registering Data Component Types for " + EssenceLib.MOD_ID);
    }
}
