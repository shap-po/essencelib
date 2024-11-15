// LevelingUtil.java
package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.component.UniqueKillsCounterComponent;
import com.github.shap_po.essencelib.registry.ModTags;
import com.github.shap_po.shappoli.integration.trinkets.TrinketsIntegration;
import com.github.shap_po.shappoli.integration.trinkets.util.TrinketsSlotModifierUtil;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.ParseResults;
import dev.emi.trinkets.TrinketModifiers;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.TrinketComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;

public class LevelingUtil {
    public static int getTotalEntityCount() {
        return Registries.ENTITY_TYPE.stream()
                .filter(e -> !e.isIn(ModTags.ENTITY_IGNORELIST))
                .mapToInt(entityType -> 1)
                .sum();
    }

    public static int getCurrentEntityCount(PlayerEntity player) {
        return UniqueKillsCounterComponent.getOptional(player)
                .map(UniqueKillsCounterComponent::getUniqueKills)
                .map(k -> k.stream()
                        .map(Registries.ENTITY_TYPE::getOrEmpty)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(e -> !e.isIn(ModTags.ENTITY_IGNORELIST))
                        .mapToInt(entityType -> 1)
                        .sum()
                )
                .orElse(0);
    }

    public static int getLevel(int uniqueKills, int totalEntities) {
        if (totalEntities <= 0) return 0;

        int level = 1;
        int baseRequirement = totalEntities / 20;

        while (uniqueKills >= baseRequirement) {
            uniqueKills -= baseRequirement;
            level++;
            baseRequirement = (int) Math.pow(level, 2) * (totalEntities / 100);
        }

        return level;
    }

    public static int getKillsToNextLevel(int currentLevel, int totalEntities) {
        if (totalEntities <= 0) return 0;

        return (int) Math.pow(currentLevel + 1, 2) * (totalEntities / 100);
    }

    public static void checkAndHandleLevelUp(ServerPlayerEntity player) {
        int totalEntities = getTotalEntityCount();
        int uniqueKills = getCurrentEntityCount(player);
        int currentLevel = getLevel(uniqueKills, totalEntities);
        int requiredKills = getKillsToNextLevel(currentLevel - 1, totalEntities);

        if (uniqueKills >= requiredKills) {
            // Level up the player
            player.networkHandler.sendPacket(new TitleS2CPacket(
                    Text.literal("Level Up!").formatted(Formatting.GOLD, Formatting.BOLD, Formatting.UNDERLINE)
            ));
            player.networkHandler.sendPacket(new TitleS2CPacket(
                    Text.literal("You are now level " + currentLevel).formatted(Formatting.YELLOW, Formatting.ITALIC)
            ));
            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            // Add an essence/soul slot
            addEssenceSlot(player);

            // Show the next number required and their current kills
            int nextRequiredKills = getKillsToNextLevel(currentLevel, totalEntities);
            player.sendMessage(Text.literal("You need " + nextRequiredKills + " unique kills to reach the next level. You currently have " + uniqueKills + " unique kills."), false);
        }
    }

    private static void addEssenceSlot(ServerPlayerEntity player) {
        ServerCommandSource commandSource = player.getCommandSource();
        String command = String.format("/shappoli trinkets slots %s add soul essence 1", player.getName().getString());
        CommandManager commandManager = Objects.requireNonNull(player.getServer()).getCommandManager();
        ParseResults<ServerCommandSource> parseResults = commandManager.getDispatcher().parse(command, commandSource);
        commandManager.execute(parseResults, command);
    }
}