package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.util.DownedStateHelper;
import com.github.shap_po.essencelib.util.StructureRollbackTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow
    protected ServerPlayerEntity player;
    @Unique
    private BlockPos essencelib$lastBreakPos;
    @Unique
    private BlockState essencelib$lastBreakState;
    @Unique
    private final Map<BlockPos, BlockState> essencelib$preInteractSnapshot = new HashMap<>();

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockBreakWhenDowned(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!DownedComponent.isDowned(player)) {
            if (!StructureRollbackTracker.isApplyingRollback(player)) {
                essencelib$lastBreakPos = pos.toImmutable();
                essencelib$lastBreakState = player.getServerWorld().getBlockState(pos);
            }
            return;
        }
        DownedStateHelper.sendDownedActionbar(player);
        cir.setReturnValue(false);
        cir.cancel();
    }

    @Inject(method = "tryBreakBlock", at = @At("RETURN"))
    private void essencelib$trackBreakForRollback(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (StructureRollbackTracker.isApplyingRollback(player)) return;
        if (essencelib$lastBreakPos == null || essencelib$lastBreakState == null) return;
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) return;
        BlockState newState = serverWorld.getBlockState(essencelib$lastBreakPos);
        StructureRollbackTracker.record(player, serverWorld, essencelib$lastBreakPos, essencelib$lastBreakState, newState);
    }

    @Inject(method = "interactBlock", at = @At("HEAD"))
    private void essencelib$capturePreInteractSnapshot(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
                                                       BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        essencelib$preInteractSnapshot.clear();
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (StructureRollbackTracker.isApplyingRollback(this.player)) return;
        BlockPos center = hitResult.getBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = center.add(dx, dy, dz).toImmutable();
                    essencelib$preInteractSnapshot.put(p, serverWorld.getBlockState(p));
                }
            }
        }
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void essencelib$trackPlaceForRollback(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
                                                  BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (!cir.getReturnValue().isAccepted()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (serverWorld.getBlockEntity(hitResult.getBlockPos()) instanceof ChestBlockEntity) {
            NuisanceAllayHelper.notifyChestOpened(serverWorld, hitResult.getBlockPos());
        }
        if (StructureRollbackTracker.isApplyingRollback(this.player)) return;
        if (essencelib$preInteractSnapshot.isEmpty()) return;
        for (var entry : essencelib$preInteractSnapshot.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState oldState = entry.getValue();
            BlockState newState = serverWorld.getBlockState(pos);
            if (oldState != newState) {
                StructureRollbackTracker.record(this.player, serverWorld, pos, oldState, newState);
            }
        }
        essencelib$preInteractSnapshot.clear();
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockBlockUseWhenDowned(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
                                                    BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (!DownedComponent.isDowned(player)) {
            return;
        }
        DownedStateHelper.sendDownedActionbar(player);
        cir.setReturnValue(ActionResult.FAIL);
        cir.cancel();
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockItemUseWhenDowned(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
                                                   CallbackInfoReturnable<ActionResult> cir) {
        if (!DownedComponent.isDowned(player)) {
            return;
        }
        DownedStateHelper.sendDownedActionbar(player);
        cir.setReturnValue(ActionResult.FAIL);
        cir.cancel();
    }
}
