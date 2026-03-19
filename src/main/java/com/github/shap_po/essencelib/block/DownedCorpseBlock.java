package com.github.shap_po.essencelib.block;

import com.github.shap_po.essencelib.block.entity.DownedCorpseBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DownedCorpseBlock extends Block implements BlockEntityProvider {

    public DownedCorpseBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DownedCorpseBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }
        if (!(world.getBlockEntity(pos) instanceof DownedCorpseBlockEntity corpse)) {
            return ActionResult.PASS;
        }

        NamedScreenHandlerFactory factory = new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                String owner = corpse.getOwnerName();
                if (owner == null || owner.isBlank()) {
                    owner = "Unknown";
                }
                return Text.translatable("screen.essencelib.corpse_loot").append(": ").append(Text.literal(owner));
            }

            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity ignored) {
                return corpse.createScreenHandler(syncId, playerInventory);
            }
        };
        serverPlayer.openHandledScreen(factory);
        return ActionResult.SUCCESS;
    }
}
