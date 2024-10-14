package com.github.shap_po.essencelib.command;

import com.github.shap_po.essencelib.command.argument.EssenceArgumentType;
import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceLoader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EssenceLibCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("essencelib")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("list")
                    .executes(EssenceLibCommand::sendEssenceList)
                )
                .then(literal("info")
                    .then(argument("essence", EssenceArgumentType.essence())
                        .executes(EssenceLibCommand::sendEssenceInfo)
                    )
                )
        );
    }

    private static int sendEssenceList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        Collection<Essence> essences = EssenceLoader.values();

        int count = essences.size();
        if (count == 0) {
            source.sendError(Text.translatable("commands.essencelib.list.fail"));
            return 0;
        }

        List<Text> texts = new LinkedList<>();
        for (Essence essence : essences) {
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(essence.getName()));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/essencelib info " + essence.getId());
            texts.add(Text.literal(essence.getId().toString())
                .setStyle(Style.EMPTY
                    .withHoverEvent(hoverEvent)
                    .withClickEvent(clickEvent))
            );
        }
        source.sendFeedback(() -> Text.translatable("commands.essencelib.list.pass", count, Texts.join(texts, Text.of(", "))), true);

        return count;
    }

    private static int sendEssenceInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        Essence essence = EssenceArgumentType.getEssence(context, "essence");
        source.sendFeedback(() -> Text.translatable("commands.essencelib.info", essence.toString()), true);

        return 0;
    }
}
