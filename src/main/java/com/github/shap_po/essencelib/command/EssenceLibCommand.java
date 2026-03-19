package com.github.shap_po.essencelib.command;

import com.github.shap_po.essencelib.command.argument.EssenceArgumentType;
import com.github.shap_po.essencelib.block.GuillotineBlockEntity;
import com.github.shap_po.essencelib.block.entity.DownedCorpseBlockEntity;
import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.guillotine.RestrainedHelper;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.level.LevelManager;
import com.github.shap_po.essencelib.registry.ModBlocks;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.util.StructureRollbackTracker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
                            .then(argument("power", StringArgumentType.word())
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(
                                    java.util.List.of("active", "passive", "lifestyle"), builder))
                                .executes(EssenceLibCommand::giveEssenceWithPower)
                            )
                        )
                    )
                )
                .then(literal("delete")
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("essence", EssenceArgumentType.essence())
                            .executes(EssenceLibCommand::deleteSpecificEssence)
                        )
                        .then(literal("all")
                            .executes(EssenceLibCommand::deleteAllEssences)
                        )
                    )
                )

                .then(literal("level")
                    .then(literal("kills")
                        .then(argument("player", EntityArgumentType.player())
                            .executes(EssenceLibCommand::getPlayerKills)
                        )
                    )
                    .then(literal("reset")
                        .then(argument("player", EntityArgumentType.player())
                            .executes(EssenceLibCommand::resetPlayerKills)
                        )
                    )
                    .then(literal("progress")
                        .then(argument("player", EntityArgumentType.player())
                            .executes(EssenceLibCommand::getPlayerLevelProgress)
                        )
                    )
                )
                .then(literal("resurrect")
                    .executes(EssenceLibCommand::resurrectSelf)
                    .then(argument("player", EntityArgumentType.player())
                        .executes(EssenceLibCommand::resurrectTarget)
                    )
                )
                .then(literal("revive")
                    .executes(EssenceLibCommand::resurrectSelf)
                    .then(argument("player", EntityArgumentType.player())
                        .executes(EssenceLibCommand::resurrectTarget)
                    )
                )
                .then(literal("guillotine")
                    .then(literal("restrain")
                        .then(argument("player", EntityArgumentType.player())
                            .executes(EssenceLibCommand::restrainPlayerNearestGuillotine)
                            .then(argument("pos", BlockPosArgumentType.blockPos())
                                .executes(EssenceLibCommand::restrainPlayerAtGuillotine)
                            )
                        )
                    )
                    .then(literal("activate")
                        .executes(EssenceLibCommand::activateNearestGuillotine)
                        .then(argument("pos", BlockPosArgumentType.blockPos())
                            .executes(EssenceLibCommand::activateGuillotineAt)
                        )
                    )
                    .then(literal("release")
                        .then(argument("player", EntityArgumentType.player())
                            .executes(EssenceLibCommand::releaseRestrainedPlayer)
                        )
                    )
                )
                .then(literal("rollback")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(EssenceLibCommand::rollbackPlayerStructuresDefault)
                        .then(argument("count", IntegerArgumentType.integer(1, 20000))
                            .executes(EssenceLibCommand::rollbackPlayerStructures)
                        )
                    )
                )
        );

        dispatcher.register(
            literal("revive")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(EssenceLibCommand::resurrectSelf)
                .then(argument("player", EntityArgumentType.player())
                    .executes(EssenceLibCommand::resurrectTarget)
                )
        );
    }

    private static int sendEssenceList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        Collection<Essence> essences = EssenceManager.values();

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
        source.sendFeedback(() -> Text.literal(
            "Registered essences (" + count + "): "
        ).append(Texts.join(texts, Text.of(", "))), true);

        return count;
    }

    private static int sendEssenceInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        Essence essence = EssenceArgumentType.getEssence(context, "essence");
        String droppedBy = essence.getDroppedBy() == null ? "none"
            : net.minecraft.registry.Registries.ENTITY_TYPE.getId(essence.getDroppedBy()).toString();
        String chance = essence.getChance() == null ? "n/a" : String.format("%.4f", essence.getChance());
        int powerCount = essence.getPowerReferences().size();
        int attributeCount = essence.getAttributes().size();

        source.sendFeedback(() -> Text.literal(
            "Essence Info | name=" + essence.getName()
                + ", id=" + essence.getId()
                + ", rarity=" + essence.getRarity()
                + ", powers=" + powerCount
                + ", attributes=" + attributeCount
                + ", dropped_by=" + droppedBy
                + ", chance=" + chance
                + ", auto_equip=" + essence.autoEquip()
                + ", can_unequip=" + essence.canUnequip()
        ), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int giveEssence(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return giveEssenceInternal(context, null);
    }

    private static int giveEssenceWithPower(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String power = StringArgumentType.getString(context, "power");
        return giveEssenceInternal(context, power);
    }

    private static int giveEssenceInternal(CommandContext<ServerCommandSource> context, String powerOverride) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity serverPlayerEntity = EntityArgumentType.getPlayer(context, "player");
        Essence essence = EssenceArgumentType.getEssence(context, "essence");

        ItemStack stack = new ItemStack(com.github.shap_po.essencelib.registry.ModItems.MOB_ESSENCE_ITEM);
        essence.applyToItemStack(stack, source.getWorld().getRandom(), powerOverride);

        boolean success = serverPlayerEntity.getInventory().insertStack(stack);
        String deliveryMode = "inventory";
        if (!success) {
            ItemEntity itemEntity = serverPlayerEntity.dropItem(stack, false);
            if (itemEntity != null) {
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(serverPlayerEntity.getUuid());
                deliveryMode = "dropped_at_player";
            }
        }

        String selectedPowerMode = powerOverride == null ? "random_slot" : powerOverride;
        String finalDeliveryMode = deliveryMode;
        source.sendFeedback(() -> Text.literal(
            "Gave " + essence.getName()
                + " to " + serverPlayerEntity.getName().getString()
                + " | power_mode=" + selectedPowerMode
                + ", delivery=" + finalDeliveryMode
        ), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int getPlayerKills(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity serverPlayerEntity = EntityArgumentType.getPlayer(context, "player");

        LevelComponent component = LevelComponent.getNullable(serverPlayerEntity);
        if (component == null) {
            source.sendError(Text.translatable("commands.essencelib.level.fail", serverPlayerEntity.getDisplayName()));
            return -1;
        }

        Set<Identifier> kills = component.getUniqueKills();
        int count = kills.size();
        if (count == 0) {
            source.sendFeedback(() -> Text.literal(
                serverPlayerEntity.getName().getString() + " has 0 unique kills tracked."
            ), false);
            return 0;
        }

        List<Text> texts = new LinkedList<>();
        for (Identifier id : kills) {
            texts.add(Text.literal(id.toString()));
        }

        source.sendFeedback(() -> Text.literal(
            serverPlayerEntity.getName().getString() + " unique kills (" + count + "): "
        ).append(Texts.join(texts, Text.of(", "))), true);

        return count;
    }

    private static int resetPlayerKills(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity serverPlayerEntity = EntityArgumentType.getPlayer(context, "player");

        LevelComponent component = LevelComponent.getNullable(serverPlayerEntity);
        if (component == null) {
            source.sendError(Text.translatable("commands.essencelib.level.fail", serverPlayerEntity.getDisplayName()));
            return -1;
        }

        component.clearUniqueKills();
        source.sendFeedback(() -> Text.literal(
            "Reset unique kill data for " + serverPlayerEntity.getName().getString() + "."
        ), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int getPlayerLevelProgress(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity serverPlayerEntity = EntityArgumentType.getPlayer(context, "player");

        int currentKills = LevelManager.getCurrentUniqueKillsCount(serverPlayerEntity);
        int totalMobs = LevelManager.getTotalEntityCount();
        int level = LevelManager.getLevel(serverPlayerEntity);
        int maxLevel = LevelManager.MAX_LEVEL;

        if (level >= maxLevel) {
            source.sendFeedback(() -> Text.literal(
                serverPlayerEntity.getName().getString()
                    + " level progress: level " + level + "/" + maxLevel
                    + " (MAX), unique_kills=" + currentKills + "/" + totalMobs
            ), true);
            return Command.SINGLE_SUCCESS;
        }

        int currentLevelFloor = level <= 0 ? 0 : LevelManager.getRequiredKills(level);
        int nextLevelTarget = LevelManager.getRequiredKills(level + 1);
        int towardNext = Math.max(0, currentKills - currentLevelFloor);
        int neededForNext = Math.max(1, nextLevelTarget - currentLevelFloor);
        int remaining = Math.max(0, nextLevelTarget - currentKills);
        int pct = (int) Math.floor((towardNext * 100.0) / neededForNext);

        source.sendFeedback(() -> Text.literal(
            serverPlayerEntity.getName().getString()
                + " level progress: level " + level + "/" + maxLevel
                + " | next_level=" + (level + 1)
                + " in " + remaining + " unique kills"
                + " (" + pct + "% of current level band), total=" + currentKills + "/" + totalMobs
        ), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int resurrectSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        return resurrectPlayer(context.getSource(), player);
    }

    private static int resurrectTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        return resurrectPlayer(context.getSource(), player);
    }

    private static int resurrectPlayer(ServerCommandSource source, ServerPlayerEntity player) {
        DownedComponent comp = DownedComponent.getNullable(player);
        if (comp == null || !comp.isDowned()) {
            source.sendError(Text.translatable("commands.essencelib.resurrect.not_downed", player.getName().getString()));
            return 0;
        }

        essencelib$restoreCorpseLootOnRevive(player, comp);
        comp.setDowned(false);
        float half = player.getMaxHealth() * 0.5f;
        player.setHealth(Math.max(0.5f, half));
        player.getInventory().markDirty();
        player.playerScreenHandler.sendContentUpdates();
        source.sendFeedback(() -> Text.translatable("commands.essencelib.resurrect.done", player.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static void essencelib$restoreCorpseLootOnRevive(ServerPlayerEntity player, DownedComponent comp) {
        if (!comp.hasCorpseLocation()) return;
        Identifier dimId = comp.getCorpseDimensionId();
        BlockPos pos = comp.getCorpsePos();
        if (dimId == null || pos == null) {
            comp.clearCorpseLocation();
            return;
        }

        var server = player.getServer();
        if (server == null) {
            return;
        }

        var world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, dimId));
        if (world == null) {
            comp.clearCorpseLocation();
            return;
        }
        if (!(world.getBlockEntity(pos) instanceof DownedCorpseBlockEntity corpse)) {
            comp.clearCorpseLocation();
            return;
        }

        for (int slot = 0; slot < DownedCorpseBlockEntity.LOOT_SIZE; slot++) {
            ItemStack stack = corpse.getStack(slot);
            if (stack.isEmpty()) continue;
            ItemStack remaining = essencelib$restoreToPlayer(player, slot, stack.copy());
            corpse.setStack(slot, remaining);
        }
        corpse.markDirty();

        if (corpse.isEmpty()) {
            world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
            comp.clearCorpseLocation();
        }
    }

    private static ItemStack essencelib$restoreToPlayer(ServerPlayerEntity player, int slot, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        if (slot >= 0 && slot <= 35) {
            ItemStack target = player.getInventory().main.get(slot);
            if (target.isEmpty()) {
                player.getInventory().main.set(slot, stack);
                return ItemStack.EMPTY;
            }
            player.getInventory().insertStack(stack);
            return stack;
        }
        if (slot >= 36 && slot <= 39) {
            int armorIdx = slot - 36;
            ItemStack target = player.getInventory().armor.get(armorIdx);
            if (target.isEmpty()) {
                player.getInventory().armor.set(armorIdx, stack);
                return ItemStack.EMPTY;
            }
            player.getInventory().insertStack(stack);
            return stack;
        }
        if (slot == 40) {
            ItemStack target = player.getInventory().offHand.get(0);
            if (target.isEmpty()) {
                player.getInventory().offHand.set(0, stack);
                return ItemStack.EMPTY;
            }
            player.getInventory().insertStack(stack);
            return stack;
        }
        return stack;
    }

    private static int deleteSpecificEssence(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        Essence essence = EssenceArgumentType.getEssence(context, "essence");
        RemovalResult result = removeEssences(player, essence.getId());

        source.sendFeedback(() -> Text.literal(
            "Deleted essence " + essence.getName()
                + " from " + player.getName().getString()
                + " | removed_stacks=" + result.totalStacks()
                + " (inventory=" + result.inventoryStacks + ", equipped=" + result.equippedStacks + ")"
        ), true);

        return result.totalStacks();
    }

    private static int restrainPlayerNearestGuillotine(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        BlockPos guillotinePos = findNearestGuillotine(target, 24, 6);
        if (guillotinePos == null) {
            source.sendError(Text.literal("No guillotine found near " + target.getName().getString() + " (radius 24)."));
            return 0;
        }
        return restrainPlayerAt(source, target, guillotinePos);
    }

    private static int restrainPlayerAtGuillotine(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        return restrainPlayerAt(source, target, pos);
    }

    private static int releaseRestrainedPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        RestrainedHelper.release(target);
        source.sendFeedback(() -> Text.literal("Released " + target.getName().getString() + " from guillotine restraint."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int activateNearestGuillotine(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BlockPos center = BlockPos.ofFloored(source.getPosition());
        BlockPos guillotinePos = findNearestGuillotine(source.getWorld(), center, 24, 6);
        if (guillotinePos == null) {
            source.sendError(Text.literal("No guillotine found near " + center.toShortString() + " (radius 24)."));
            return 0;
        }
        return activateGuillotineAt(source, guillotinePos);
    }

    private static int activateGuillotineAt(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        return activateGuillotineAt(source, pos);
    }

    private static int activateGuillotineAt(ServerCommandSource source, BlockPos pos) {
        if (!source.getWorld().getBlockState(pos).isOf(ModBlocks.GUILLOTINE)) {
            source.sendError(Text.literal("Block at " + pos.toShortString() + " is not an essencelib guillotine."));
            return 0;
        }
        if (!(source.getWorld().getBlockEntity(pos) instanceof GuillotineBlockEntity guillotine)) {
            source.sendError(Text.literal("Guillotine block entity missing at " + pos.toShortString() + "."));
            return 0;
        }
        guillotine.activate();
        source.sendFeedback(() -> Text.literal("Activated guillotine at " + pos.toShortString() + "."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int restrainPlayerAt(ServerCommandSource source, ServerPlayerEntity target, BlockPos pos) {
        if (!target.getWorld().getBlockState(pos).isOf(ModBlocks.GUILLOTINE)) {
            source.sendError(Text.literal("Block at " + pos.toShortString() + " is not an essencelib guillotine."));
            return 0;
        }
        RestrainedHelper.restrain(target, pos, target.getWorld().getRegistryKey());
        RestrainedHelper.snapToGuillotinePose(target, pos);
        source.sendFeedback(() -> Text.literal(
            "Restrained " + target.getName().getString() + " at guillotine " + pos.toShortString() + "."
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    private static BlockPos findNearestGuillotine(ServerPlayerEntity player, int horizontalRadius, int verticalRadius) {
        return findNearestGuillotine(player.getWorld(), player.getBlockPos(), horizontalRadius, verticalRadius);
    }

    private static BlockPos findNearestGuillotine(World world, BlockPos center, int horizontalRadius, int verticalRadius) {
        BlockPos bestPos = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -horizontalRadius; dx <= horizontalRadius; dx++) {
            for (int dz = -horizontalRadius; dz <= horizontalRadius; dz++) {
                for (int dy = -verticalRadius; dy <= verticalRadius; dy++) {
                    BlockPos candidate = center.add(dx, dy, dz);
                    if (!world.getBlockState(candidate).isOf(ModBlocks.GUILLOTINE)) continue;
                    double dist = candidate.getSquaredDistance(center);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestPos = candidate.toImmutable();
                    }
                }
            }
        }
        return bestPos;
    }

    private static int rollbackPlayerStructuresDefault(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        int restored = StructureRollbackTracker.rollback(target, 200);
        context.getSource().sendFeedback(() -> Text.literal(
            "Rolled back " + restored + " structure edits for " + target.getName().getString() + " (requested 200)."
        ), true);
        return restored;
    }

    private static int rollbackPlayerStructures(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        int count = IntegerArgumentType.getInteger(context, "count");
        int restored = StructureRollbackTracker.rollback(target, count);
        context.getSource().sendFeedback(() -> Text.literal(
            "Rolled back " + restored + " structure edits for " + target.getName().getString() + " (requested " + count + ")."
        ), true);
        return restored;
    }

    private static int deleteAllEssences(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        RemovalResult result = removeEssences(player, null);

        source.sendFeedback(() -> Text.literal(
            "Deleted all essences from " + player.getName().getString()
                + " | removed_stacks=" + result.totalStacks()
                + " (inventory=" + result.inventoryStacks + ", equipped=" + result.equippedStacks + ")"
        ), true);

        return result.totalStacks();
    }

    private static RemovalResult removeEssences(ServerPlayerEntity player, Identifier targetEssenceId) {
        int removedInventory = 0;
        int removedEquipped = 0;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!isMatchingEssenceStack(stack, targetEssenceId)) continue;
            player.getInventory().setStack(i, ItemStack.EMPTY);
            removedInventory++;
        }

        removedEquipped += TrinketsApi.getTrinketComponent(player).map(comp -> {
            int removed = 0;
            for (var group : comp.getInventory().values()) {
                for (var inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack stack = inv.getStack(i);
                        if (!isMatchingEssenceStack(stack, targetEssenceId)) continue;
                        inv.setStack(i, ItemStack.EMPTY);
                        removed++;
                    }
                }
            }
            return removed;
        }).orElse(0);

        player.getInventory().markDirty();
        player.playerScreenHandler.sendContentUpdates();
        return new RemovalResult(removedInventory, removedEquipped);
    }

    private static boolean isMatchingEssenceStack(ItemStack stack, Identifier targetEssenceId) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MobEssenceTrinketItem)) return false;
        if (targetEssenceId == null) return true;
        Identifier stackEssenceId = stack.get(ModDataComponentTypes.ESSENCE_ID);
        return targetEssenceId.equals(stackEssenceId);
    }

    private static final class RemovalResult {
        private final int inventoryStacks;
        private final int equippedStacks;

        private RemovalResult(int inventoryStacks, int equippedStacks) {
            this.inventoryStacks = inventoryStacks;
            this.equippedStacks = equippedStacks;
        }

        private int totalStacks() {
            return inventoryStacks + equippedStacks;
        }
    }
}
