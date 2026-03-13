package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.collector.CollectorRushHelper;
import com.github.shap_po.essencelib.component.CollectorRushComponent;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.registry.ModTags;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private long sneakStartTime = -1;

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        ItemStack stack = itemEntity.getStack();

        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            // Require player to be within 1 block (on top of) the essence to pick up
            if (player.squaredDistanceTo(itemEntity) > 1.0 * 1.0) {
                ci.cancel();
                return;
            }
            if (player.isSneaking()) {
                if (sneakStartTime == -1) {
                    sneakStartTime = System.currentTimeMillis();
                } else {
                    long elapsed = System.currentTimeMillis() - sneakStartTime;
                    if (elapsed >= 5000) {
                        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
                        if (optional.isPresent()) {
                            TrinketComponent comp = optional.get();
                            boolean hasEmptySlot = comp.getInventory().values().stream()
                                .flatMap(group -> group.values().stream())
                                .anyMatch(inv -> {
                                    for (int i = 0; i < inv.size(); i++) {
                                        if (inv.getStack(i).isEmpty()) {
                                            return true;
                                        }
                                    }
                                    return false;
                                });

                            Identifier essenceId = stack.get(ModDataComponentTypes.ESSENCE_ID);
                            if (essenceId != null && MobEssenceTrinketItem.hasEssenceInPossession(player, essenceId)) {
                                player.sendMessage(Text.literal("You already have this essence").formatted(Formatting.RED), true);
                            } else if (hasEmptySlot) {
                                player.getInventory().insertStack(stack);
                                itemEntity.discard();
                            } else {
                                player.sendMessage(Text.literal("Full of Essence").formatted(Formatting.RED), true);
                            }
                        }
                        ci.cancel();
                        return;
                    } else {
                        int secondsLeft = 5 - (int) (elapsed / 1000);
                        player.sendMessage(Text.literal("Shift to pick up in " + secondsLeft + " seconds").formatted(Formatting.YELLOW), true);
                    }
                }
            } else {
                sneakStartTime = -1;
            }
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z", shift = At.Shift.BEFORE))
    private void essencelib$onVanillaPickup(PlayerEntity player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        ItemStack stack = itemEntity.getStack();
        if (player.getWorld().isClient()) return;
        if (stack.isEmpty() || stack.getItem() == Items.AIR || stack.isIn(ModTags.ITEM_IGNORELIST)) return;

        CollectorRushComponent comp = CollectorRushComponent.getNullable(player);
        if (comp != null) {
            boolean wasNew = comp.addCollectedItem(stack);
            if (wasNew) CollectorRushHelper.triggerRushForNewItemPickup(player, stack);
        }
    }

    @Inject(method = "canMerge", at = @At("HEAD"), cancellable = true)
    private void canMerge(CallbackInfoReturnable<Boolean> cir) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        ItemStack stack = itemEntity.getStack();

        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            cir.setReturnValue(false);
        }
    }

}

