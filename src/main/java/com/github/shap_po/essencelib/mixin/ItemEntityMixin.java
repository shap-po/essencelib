package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.collector.CollectorRushHelper;
import com.github.shap_po.essencelib.component.CollectorRushComponent;
import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.registry.ModTags;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private long sneakStartTime = -1;
    @Unique
    private boolean essencelib$naturalDespawnChecked = false;

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        ItemStack stack = itemEntity.getStack();

        if (stack.getItem() instanceof MobEssenceTrinketItem) {
            // Prevent client-side ghost/invisible item behavior by handling pickup only on server.
            if (player.getWorld().isClient()) {
                ci.cancel();
                return;
            }

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
                            if (essenceId != null && !player.isCreative() && MobEssenceTrinketItem.hasEssenceInPossession(player, essenceId)) {
                                player.sendMessage(Text.literal("You already have this essence").formatted(Formatting.RED), true);
                            } else {
                                boolean autoEquip = stack.getOrDefault(ModDataComponentTypes.AUTO_EQUIP, true) && !player.isCreative();
                                boolean handled = false;

                                if (autoEquip && hasEmptySlot) {
                                    // Move directly from world item to trinket slot.
                                    ItemStack toEquip = stack.copy();
                                    toEquip.setCount(1);
                                    if (TrinketItem.equipItem(player, toEquip)) {
                                        handled = true;
                                    }
                                }

                                if (!handled) {
                                    // Fallback: move to inventory (single source of truth, no duplicate copies).
                                    ItemStack toInsert = stack.copy();
                                    if (player.getInventory().insertStack(toInsert)) {
                                        handled = true;
                                    }
                                }

                                if (handled) {
                                    itemEntity.discard();
                                } else {
                                    player.sendMessage(Text.literal("Full of Essence").formatted(Formatting.RED), true);
                                }
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

    @Inject(method = "setDespawnImmediately", at = @At("HEAD"))
    private void essencelib$spawnNuisanceAllayOnDespawn(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.getWorld().isClient()) return;
        NuisanceAllayHelper.maybeSpawnFromItemDespawn(itemEntity);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void essencelib$spawnNuisanceAllayOnNaturalDespawn(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.getWorld().isClient()) return;
        if (essencelib$naturalDespawnChecked) return;
        if (itemEntity.getStack().isEmpty()) return;

        // Natural despawn happens around age 6000; spawn-check once right before it expires.
        if (itemEntity.getItemAge() >= 5999) {
            essencelib$naturalDespawnChecked = true;
            NuisanceAllayHelper.maybeSpawnFromItemDespawn(itemEntity);
        }
    }

}

