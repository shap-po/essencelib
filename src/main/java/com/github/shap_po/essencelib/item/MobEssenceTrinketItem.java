package com.github.shap_po.essencelib.item;

import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

public class MobEssenceTrinketItem extends TrinketItem {
    public static final Item KEY_ITEM = Items.STONE;

    public MobEssenceTrinketItem() {
        super(new Settings().maxDamage(1200).rarity(Rarity.RARE));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
        }
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getComponents().getOrDefault(ModDataComponentTypes.CAN_UNEQUIP, false) || (entity instanceof PlayerEntity player && player.isCreative())) {
            return true;
        }

        if (entity instanceof PlayerEntity player) {
            ItemStack offHandItem = player.getOffHandStack();
            if (offHandItem.getItem() == KEY_ITEM) {
                slot.inventory().setStack(slot.index(), ItemStack.EMPTY);
                player.getOffHandStack().decrement(1);
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        // Auto-equip is handled explicitly on pickup in ItemEntityMixin.
        // Keeping it out of inventoryTick avoids accidental duplicate equip/copy behavior.
    }

    /** Returns true if the player already has an essence with this ID (equipped or in inventory). */
    public static boolean hasEssenceInPossession(PlayerEntity player, Identifier essenceId) {
        if (essenceId == null) return false;
        if (hasEssenceEquipped(player, essenceId)) return true;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (!s.isEmpty() && s.getItem() instanceof MobEssenceTrinketItem) {
                if (essenceId.equals(s.get(ModDataComponentTypes.ESSENCE_ID))) return true;
            }
        }
        return false;
    }

    /** Returns true if the entity already has an essence with this ID equipped in a trinket slot. */
    public static boolean hasEssenceEquipped(LivingEntity entity, Identifier essenceId) {
        if (essenceId == null) return false;
        return TrinketsApi.getTrinketComponent(entity)
            .map(comp -> comp.getInventory().values().stream()
                .flatMap(group -> group.values().stream())
                .anyMatch(inv -> {
                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack s = inv.getStack(i);
                        if (!s.isEmpty() && s.getItem() instanceof MobEssenceTrinketItem) {
                            Identifier id = s.get(ModDataComponentTypes.ESSENCE_ID);
                            if (essenceId.equals(id)) return true;
                        }
                    }
                    return false;
                }))
            .orElse(false);
    }

}





