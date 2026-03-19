package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.screen.DownedPlayerLootScreenHandler;
import com.github.shap_po.essencelib.util.DownedStateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When a player right-clicks a downed player, open the downed player's inventory for looting.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityInteractMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void essencelib$openLootWhenDowned(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity downed = (PlayerEntity) (Object) this;
        if (downed.getWorld().isClient) return;
        if (entity instanceof PlayerEntity actor && DownedComponent.isDowned(actor)) {
            DownedStateHelper.sendDownedActionbar(actor);
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
            return;
        }
        if (!DownedComponent.isDowned(downed)) return;
        if (!(entity instanceof ServerPlayerEntity serverLooter)) return;

        // Copy downed player's inventory into a container for the screen
        PlayerInventory downedInv = downed.getInventory();
        SimpleInventory lootInv = new SimpleInventory(41);
        for (int i = 0; i < 36; i++) lootInv.setStack(i, downedInv.main.get(i).copy());
        for (int i = 0; i < 4; i++) lootInv.setStack(36 + i, downedInv.armor.get(i).copy());
        lootInv.setStack(40, downedInv.offHand.get(0).copy());

        NamedScreenHandlerFactory factory = new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("screen.essencelib.downed_loot").append(": ").append(downed.getName());
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                DownedComponent downedComp = DownedComponent.getNullable(downed);
                int occupied = downedComp != null ? downedComp.getEssenceSlotsOccupied() : 0;
                int causeCode = downedComp != null ? downedComp.getLastDownedCauseCode() : 0;
                int downedEpochSeconds = downedComp != null ? downedComp.getLastDownedEpochSeconds() : 0;
                return new DownedPlayerLootScreenHandler(syncId, playerInventory, lootInv, downed, occupied, causeCode, downedEpochSeconds);
            }
        };
        serverLooter.openHandledScreen(factory);
        cir.setReturnValue(ActionResult.SUCCESS);
        cir.cancel();
    }
}
