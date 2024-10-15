package com.github.shap_po.essencelib.command;

import com.github.shap_po.essencelib.command.argument.EssenceArgumentType;
import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceLoader;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
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
                .then(literal("give")
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("essence", EssenceArgumentType.essence())
                            .executes(EssenceLibCommand::giveEssence)
                        )
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

        return Command.SINGLE_SUCCESS;
    }

    private static int giveEssence(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity serverPlayerEntity = EntityArgumentType.getPlayer(context, "player");
        Essence essence = EssenceArgumentType.getEssence(context, "essence");

        ItemStack stack = essence.toItemStack();

        boolean success = serverPlayerEntity.getInventory().insertStack(stack);
        if (!success) {
            ItemEntity itemEntity = serverPlayerEntity.dropItem(stack, false);
            if (itemEntity != null) {
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(serverPlayerEntity.getUuid());
            }
        }

        source.sendFeedback(() -> Text.translatable("commands.essencelib.give", essence.toText(), serverPlayerEntity.getDisplayName()), true);

        return Command.SINGLE_SUCCESS;
    }
}