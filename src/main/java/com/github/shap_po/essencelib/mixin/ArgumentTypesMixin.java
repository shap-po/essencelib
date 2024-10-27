package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.command.argument.EssenceArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Registers custom argument types for the essence library.
 * <br>
 * Code is based on {@link io.github.apace100.apoli.mixin.ArgumentTypesMixin}.
 */
@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {
    @Shadow
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String string, Class<? extends A> clazz, ArgumentSerializer<A, T> argumentSerializer) {
        throw new AssertionError("Mixins for basic functionality are fun.");
    }

    @Inject(method = "register(Lnet/minecraft/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", at = @At("RETURN"))
    private static void registerEssenceLibArgumentTypes(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> cir) {
        register(registry, EssenceLib.identifier("essence").toString(), EssenceArgumentType.class, ConstantArgumentSerializer.of(EssenceArgumentType::essence));
    }
}
