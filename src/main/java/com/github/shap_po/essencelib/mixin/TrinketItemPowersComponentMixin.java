package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.emi.trinkets.api.TrinketsAttributeModifiersComponent;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;



@Environment(EnvType.CLIENT)
@Mixin(TrinketItemPowersComponent.class)
public abstract class TrinketItemPowersComponentMixin {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void onAppendTooltip(PlayerEntity player, ItemStack stack, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        // Clear existing tooltip but keep original item name
        if (!tooltip.isEmpty()) {
            Text originalName = tooltip.get(0);
            tooltip.clear();
            tooltip.add(originalName.copy().formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
        }

        // Get entries in insertion order
        List<TrinketItemPowersComponent.Entry> orderedEntries = new ArrayList<>(
            ((TrinketItemPowersComponentAccessor) this).getEntries()
        );

        // Display in fixed order with category headers
        addPowerWithCategory(orderedEntries, 0, "Active", Formatting.RED, stack, tooltip, type);
        addPowerWithCategory(orderedEntries, 1, "Passive", Formatting.GREEN, stack, tooltip, type);
        addPowerWithCategory(orderedEntries, 2, "Lifestyle", Formatting.YELLOW, stack, tooltip, type);
        tooltip.add(Text.literal(""));
        

        // Add stats section
        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("Stats").formatted(Formatting.GOLD, Formatting.BOLD));
        addStatChanges(stack, tooltip);

        // Add warning at the end
        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("⚠ WARNING!")
            .formatted(Formatting.RED, Formatting.BOLD)
        );
        tooltip.add(Text.literal("  • Permanent binding")
            .formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.literal("  • Auto-equip on pickup")
            .formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.literal("  • [Hold Shift to Pickup]")
            .formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));


        ci.cancel();
    }

    private void addPowerWithCategory(List<TrinketItemPowersComponent.Entry> entries, int index, String category, 
                                     Formatting color, ItemStack stack, List<Text> tooltip, TooltipType type) {
        if (index < entries.size()) {
            TrinketItemPowersComponent.Entry entry = entries.get(index);
            Power power = PowerManager.getNullable(entry.powerId());
            if (power != null && !entry.hidden()) {
                // Compact category header
                tooltip.add(Text.literal(category)
                    .formatted(color)
                    .append(Text.literal(":").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(" ").append(power.getName()).formatted(Formatting.WHITE)));

                // Single line description without wrapping
                tooltip.add(Text.literal("▹ ").formatted(Formatting.DARK_GRAY)
                    .append(power.getDescription().copy().formatted(Formatting.GRAY, Formatting.ITALIC)));
            }
        }
    }

    private void addStatChanges(ItemStack stack, List<Text> tooltip) {
        TrinketsAttributeModifiersComponent attributes = stack.get(TrinketsAttributeModifiersComponent.TYPE);
        if (attributes != null) {
            attributes.modifiers().forEach(entry -> {
                var attribute = entry.attribute();
                var modifier = entry.modifier();
                
                // Get attribute-specific scaling
                double scale = getScaleFactor(Registries.ATTRIBUTE.getId(attribute.value()));
                double scaledValue = modifier.value() * scale;
                String displayValue = String.format(scaledValue % 1 == 0 ? "%.0f" : "%.1f", scaledValue);
                
                if (scaledValue == 0) return;

                Formatting color = scaledValue > 0 ? Formatting.DARK_GREEN : Formatting.RED;
                    tooltip.add(
                        Text.translatable(getSimpleAttributeKey(Registries.ATTRIBUTE.getId(attribute.value())))
                            .append(Text.literal(":").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(" " + (scaledValue > 0 ? "+" : "") + displayValue).formatted(color))
                    );
            });
            tooltip.add(Text.literal(""));
        }
    }

    private String getSimpleAttributeKey(Identifier attributeId) {
        return switch (attributeId.getPath()) {
            // Vanilla Minecraft
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
            
            // Pufferfish's Attributes
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
            
            // Additional Entity Attributes
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
            
            // Default case
            default -> attributeId.toTranslationKey("attribute");
        };
    }

    private int getScaleFactor(Identifier attributeId) {
        // Attributes that should show 0.1 as +1
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
            // Pufferfish percentage-based attributes
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
            
            // Additional Entity Attributes scaled attributes
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
}
