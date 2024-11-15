package com.github.shap_po.essencelib.item;

import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.SlotReference;
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
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

public class MobEssenceTrinketItem extends TrinketItem {
    public static final Item KEY_ITEM = Items.STONE;

    public MobEssenceTrinketItem() {
        super(new Settings().maxDamage(1200).rarity(Rarity.RARE));
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            MobEssenceTooltips.appendTooltip(stack, context, tooltip);
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

        if (entity instanceof LivingEntity && stack.getComponents().getOrDefault(ModDataComponentTypes.AUTO_EQUIP, true) && !(entity instanceof PlayerEntity player && player.isCreative())) {
            MobEssenceTrinketItem.equipItem((LivingEntity) entity, stack);
        }
    }
}





