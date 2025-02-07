package com.github.shap_po.essencelib.command;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.command.argument.EssenceArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModArgumentTypes {
    public static void register() {
        Registry.register(
            Registries.COMMAND_ARGUMENT_TYPE,
            EssenceLib.identifier("essence"),
            ConstantArgumentSerializer.of(() -> EssenceArgumentType.essence())
        );
    }
} 
