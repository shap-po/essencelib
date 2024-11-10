package com.github.shap_po.essencelib.command.argument;

import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class EssenceArgumentType implements ArgumentType<Essence> {
    public static final DynamicCommandExceptionType ESSENCE_NOT_FOUND = new DynamicCommandExceptionType(
        (id) -> Text.translatable("commands.essencelib.essence_not_found", id)
    );

    public static EssenceArgumentType essence() {
        return new EssenceArgumentType();
    }

    public static Essence getEssence(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Essence.class);
    }

    @Override
    public Essence parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInputNonEmpty(reader);
        Essence essence = EssenceManager.getNullable(id);
        if (essence == null) {
            throw ESSENCE_NOT_FOUND.create(id);
        }
        return essence;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(EssenceManager.values().stream().map(Essence::getId), builder);
    }
}
