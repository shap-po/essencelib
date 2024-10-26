package com.github.shap_po.essencelib.registry;


import com.github.shap_po.essencelib.EssenceLib;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<Identifier> ESSENCE_ID = register("essence_id", builder -> builder.codec(Identifier.CODEC));
    public static final ComponentType<Boolean> CAN_UNEQUIP = register("can_unequip", builder -> builder.codec(Codec.BOOL));

    @SuppressWarnings({"SameParameterValue"})
    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(EssenceLib.MOD_ID, name),
            builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void registerDataComponentTypes() {
        EssenceLib.LOGGER.info("Registering Data Component Types for {}", EssenceLib.MOD_ID);
    }
}
