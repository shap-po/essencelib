package com.github.shap_po.essencelib.util;

import dev.emi.trinkets.api.TrinketsAttributeModifiersComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Shared stat formatting for essence tooltips and floating labels.
 */
@Environment(EnvType.CLIENT)
public final class EssenceStatHelper {

    /** Single stat line: "Name: +value" or "Name: -value" */
    public static final class StatEntry {
        public final Text text;
        public final boolean positive;

        public StatEntry(Text text, boolean positive) {
            this.text = text;
            this.positive = positive;
        }
    }

    public static List<Text> buildStatLines(ItemStack stack) {
        return buildStatEntries(stack).stream()
            .map(e -> e.text)
            .toList();
    }

    /** Positives first, negatives last — for cleaner tooltip layout. */
    public static List<Text> buildStatLinesGrouped(ItemStack stack) {
        List<StatEntry> entries = buildStatEntries(stack);
        List<Text> result = new ArrayList<>();
        for (StatEntry e : entries) {
            if (e.positive) result.add(e.text);
        }
        for (StatEntry e : entries) {
            if (!e.positive) result.add(e.text);
        }
        return result;
    }

    /** Compact layout: positive and negative share a line with divider. */
    public static List<Text> buildStatLinesCompact(ItemStack stack) {
        List<StatEntry> entries = buildStatEntries(stack);
        List<Text> positives = entries.stream().filter(e -> e.positive).map(e -> e.text).toList();
        List<Text> negatives = entries.stream().filter(e -> !e.positive).map(e -> e.text).toList();

        List<Text> lines = new ArrayList<>();
        int max = Math.max(positives.size(), negatives.size());
        Text divider = Text.literal("  │  ").formatted(Formatting.DARK_GRAY);

        for (int i = 0; i < max; i++) {
            Text left = i < positives.size() ? positives.get(i) : Text.empty();
            Text right = i < negatives.size() ? negatives.get(i) : Text.empty();

            if (left.getString().isEmpty() && right.getString().isEmpty()) continue;
            if (left.getString().isEmpty()) {
                lines.add(Text.literal("        ").append(divider).append(right));
            } else if (right.getString().isEmpty()) {
                lines.add(left);
            } else {
                lines.add(left.copy().append(divider).append(right));
            }
        }
        return lines;
    }

    private static List<StatEntry> buildStatEntries(ItemStack stack) {
        List<StatEntry> entries = new ArrayList<>();
        TrinketsAttributeModifiersComponent attributes = stack.get(TrinketsAttributeModifiersComponent.TYPE);
        if (attributes == null) return entries;

        attributes.modifiers().forEach(entry -> {
            var attribute = entry.attribute();
            var modifier = entry.modifier();
            double scale = getScaleFactor(Registries.ATTRIBUTE.getId(attribute.value()));
            double scaledValue = modifier.value() * scale;
            String displayValue = String.format(scaledValue % 1 == 0 ? "%.0f" : "%.1f", scaledValue);
            if (scaledValue == 0) return;

            Formatting color = scaledValue > 0 ? Formatting.DARK_GREEN : Formatting.RED;
            Text text = Text.translatable(getSimpleAttributeKey(Registries.ATTRIBUTE.getId(attribute.value())))
                .append(Text.literal(":").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(" " + (scaledValue > 0 ? "+" : "") + displayValue).formatted(color));
            entries.add(new StatEntry(text, scaledValue > 0));
        });
        return entries;
    }

    public static String getSimpleAttributeKey(Identifier attributeId) {
        return switch (attributeId.getPath()) {
            case "generic.scale" -> "essencelib.scale";
            case "generic.step_height" -> "essencelib.step_height";
            case "generic.sweeping_damage_ratio" -> "essencelib.sweeping";
            case "generic.burning_time" -> "essencelib.burning_time";
            case "generic.tempt_range" -> "essencelib.tempt_range";
            case "player.entity_interaction_range" -> "essencelib.entity_reach";
            case "player.block_interaction_range" -> "essencelib.block_reach";
            case "generic.armor" -> "essencelib.armor";
            case "generic.armor_toughness" -> "essencelib.toughness";
            case "generic.attack_damage" -> "essencelib.attack";
            case "generic.attack_speed" -> "essencelib.attack_speed";
            case "generic.movement_speed" -> "essencelib.speed";
            case "generic.luck" -> "essencelib.luck";
            case "generic.max_health" -> "essencelib.max_health";
            case "crit_chance" -> "essencelib.critical_chance";
            case "reach_distance" -> "essencelib.reach";
            case "life_steal" -> "essencelib.life_steal";
            case "natural_regeneration" -> "essencelib.regen";
            case "mining_speed" -> "essencelib.mining_speed";
            case "sprinting_speed" -> "essencelib.sprint_speed";
            case "stamina" -> "essencelib.stamina";
            case "fortune" -> "essencelib.fortune";
            case "magic_damage" -> "essencelib.magic_damage";
            case "melee_damage" -> "essencelib.melee_damage";
            case "ranged_damage" -> "essencelib.ranged_damage";
            case "healing" -> "essencelib.healing";
            case "jump" -> "essencelib.jump_power";
            case "resistance" -> "essencelib.resistance";
            case "magic_resistance" -> "essencelib.magic_resist";
            case "melee_resistance" -> "essencelib.melee_resist";
            case "ranged_resistance" -> "essencelib.ranged_resist";
            case "pickaxe_speed" -> "essencelib.pickaxe_speed";
            case "axe_speed" -> "essencelib.axe_speed";
            case "shovel_speed" -> "essencelib.shovel_speed";
            case "knockback" -> "essencelib.knockback";
            case "repair_cost" -> "essencelib.repair_cost";
            case "armor_shred" -> "essencelib.armor_shred";
            case "toughness_shred" -> "essencelib.toughness_shred";
            case "protection_shred" -> "essencelib.protection_shred";
            case "stealth" -> "essencelib.stealth";
            case "fall_reduction" -> "essencelib.fall_reduction";
            case "bow_projectile_speed" -> "essencelib.bow_speed";
            case "crossbow_projectile_speed" -> "essencelib.crossbow_speed";
            case "water_speed" -> "essencelib.water_speed";
            case "lava_speed" -> "essencelib.lava_speed";
            case "lung_capacity" -> "essencelib.lung_capacity";
            case "jump_height" -> "essencelib.jump_power";
            case "dropped_experience" -> "essencelib.xp_drop";
            case "magic_protection" -> "essencelib.magic_prot";
            case "mob_detection_range" -> "essencelib.mob_detect";
            case "water_visibility" -> "essencelib.water_vis";
            case "lava_visibility" -> "essencelib.lava_vis";
            case "critical_bonus_damage" -> "essencelib.critical_damage";
            case "dig_speed" -> "essencelib.digging_speed";
            case "bonus_loot_count_rolls" -> "essencelib.loot_rolls";
            case "bonus_rare_loot_rolls" -> "essencelib.rare_loot";
            case "player.collection_range" -> "essencelib.collection";
            case "width" -> "essencelib.width";
            case "height" -> "essencelib.height";
            case "hitbox_scale" -> "essencelib.hitbox_scale";
            case "hitbox_width" -> "essencelib.hitbox_width";
            case "hitbox_height" -> "essencelib.hitbox_height";
            case "model_scale" -> "essencelib.model_scale";
            case "model_width" -> "essencelib.model_width";
            case "model_height" -> "essencelib.model_height";
            default -> attributeId.toTranslationKey("attribute");
        };
    }

    public static int getScaleFactor(Identifier attributeId) {
        Set<Identifier> scaledAttributes = Set.of(
            Identifier.of("minecraft:generic.movement_speed"),
            Identifier.of("minecraft:generic.attack_speed"),
            Identifier.of("minecraft:generic.luck"),
            Identifier.of("minecraft:generic.knockback_resistance"),
            Identifier.of("minecraft:generic.flying_speed"),
            Identifier.of("minecraft:generic.jump_strength"),
            Identifier.of("minecraft:generic.scale"),
            Identifier.of("minecraft:generic.safe_fall_distance"),
            Identifier.of("minecraft:generic.fall_damage_multiplier"),
            Identifier.of("minecraft:generic.movement_efficiency"),
            Identifier.of("minecraft:generic.water_movement_efficiency"),
            Identifier.of("minecraft:player.block_break_speed"),
            Identifier.of("minecraft:player.sneaking_speed"),
            Identifier.of("minecraft:player.submerged_mining_speed"),
            Identifier.of("minecraft:generic.burning_time"),
            Identifier.of("minecraft:generic.explosion_knockback_resistance"),
            Identifier.of("minecraft:generic.oxygen_bonus"),
            Identifier.of("minecraft:generic.sprinting_speed"),
            Identifier.of("minecraft:generic.tempt_range"),
            Identifier.of("minecraft:generic.step_height"),
            Identifier.of("pufferfish_attributes:crit_chance"),
            Identifier.of("apoli:swimming_speed"),
            Identifier.of("pufferfish_attributes:natural_regeneration"),
            Identifier.of("pufferfish_attributes:life_steal"),
            Identifier.of("pufferfish_attributes:healing"),
            Identifier.of("pufferfish_attributes:resistance"),
            Identifier.of("pufferfish_attributes:magic_resistance"),
            Identifier.of("pufferfish_attributes:melee_resistance"),
            Identifier.of("pufferfish_attributes:ranged_resistance"),
            Identifier.of("pufferfish_attributes:mining_speed"),
            Identifier.of("pufferfish_attributes:sprinting_speed"),
            Identifier.of("pufferfish_attributes:fall_reduction"),
            Identifier.of("pufferfish_attributes:magic_damage"),
            Identifier.of("pufferfish_attributes:melee_damage"),
            Identifier.of("pufferfish_attributes:ranged_damage"),
            Identifier.of("pufferfish_attributes:pickaxe_speed"),
            Identifier.of("pufferfish_attributes:axe_speed"),
            Identifier.of("pufferfish_attributes:shovel_speed"),
            Identifier.of("additionalentityattributes:water_speed"),
            Identifier.of("additionalentityattributes:lava_speed"),
            Identifier.of("additionalentityattributes:dig_speed"),
            Identifier.of("additionalentityattributes:critical_bonus_damage"),
            Identifier.of("additionalentityattributes:water_visibility"),
            Identifier.of("additionalentityattributes:lava_visibility"),
            Identifier.of("additionalentityattributes:width"),
            Identifier.of("additionalentityattributes:height"),
            Identifier.of("additionalentityattributes:hitbox_scale"),
            Identifier.of("additionalentityattributes:hitbox_width"),
            Identifier.of("additionalentityattributes:hitbox_height"),
            Identifier.of("additionalentityattributes:model_scale"),
            Identifier.of("additionalentityattributes:model_width"),
            Identifier.of("additionalentityattributes:model_height")
        );
        return scaledAttributes.contains(attributeId) ? 10 : 1;
    }

    private EssenceStatHelper() {}
}
