package com.github.shap_po.essencelib.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import com.github.shap_po.essencelib.registry.ModBlockEntities;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.block.ShapeContext;
import org.jetbrains.annotations.Nullable;

/**
 * Guillotine block. Interaction is command-driven for now.
 */
public class GuillotineBlock extends BlockWithEntity {

    public static final MapCodec<GuillotineBlock> CODEC = createCodec(GuillotineBlock::new);
    // Approximate the rendered guillotine model with a hollow center and front basket extension.
    // Keeping this non-cubic makes raycast/collision feel much closer to the visual mesh.
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(1, 0, 1, 15, 2, 15),      // base slab
        Block.createCuboidShape(0, 0, 0, 16, 1, 16),      // lower trim
        Block.createCuboidShape(2, 2, 2, 4, 16, 14),      // left front pillar
        Block.createCuboidShape(12, 2, 2, 14, 16, 14),    // right front pillar
        Block.createCuboidShape(2, 2, 12, 14, 14, 14),    // rear frame
        Block.createCuboidShape(2, 14, 2, 14, 16, 14),    // top beam
        Block.createCuboidShape(5, 2, 2, 11, 8, 4),       // neck shelf
        Block.createCuboidShape(4, 0, -6, 12, 7, 0),      // basket body (front protrusion)
        Block.createCuboidShape(3, 7, -6, 13, 9, 0),      // basket rim
        Block.createCuboidShape(3, 0, -7, 13, 1, 1),      // basket lip
        Block.createCuboidShape(6, 8, 2, 10, 14, 3)       // blade channel brace
    );

    public GuillotineBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GuillotineBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockWithEntity.validateTicker(type, ModBlockEntities.GUILLOTINE, GuillotineBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // Disabled by design: guillotine restraint/execution is command-only right now.
        return ActionResult.PASS;
    }
}
