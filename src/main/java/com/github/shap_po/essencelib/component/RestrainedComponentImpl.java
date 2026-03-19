package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestrainedComponentImpl implements RestrainedComponent {
    private final PlayerEntity provider;
    private boolean restrained;
    private BlockPos anchorPos;
    private RegistryKey<World> dimension;

    public RestrainedComponentImpl(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public boolean isRestrained() {
        return restrained;
    }

    @Override
    public void restrain(BlockPos anchor, RegistryKey<World> dimension) {
        this.anchorPos = anchor.toImmutable();
        this.dimension = dimension;
        this.restrained = true;
        RestrainedComponent.KEY.sync(provider);
    }

    @Override
    public void release() {
        if (!restrained) return;
        this.restrained = false;
        this.anchorPos = null;
        this.dimension = null;
        RestrainedComponent.KEY.sync(provider);
    }

    @Override
    @Nullable
    public BlockPos getAnchorPos() {
        return anchorPos;
    }

    @Override
    @Nullable
    public RegistryKey<World> getDimension() {
        return dimension;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        restrained = tag.getBoolean("restrained");
        if (tag.contains("anchor_x") && tag.contains("anchor_y") && tag.contains("anchor_z")) {
            anchorPos = new BlockPos(tag.getInt("anchor_x"), tag.getInt("anchor_y"), tag.getInt("anchor_z"));
        } else {
            anchorPos = null;
        }
        dimension = tag.contains("dimension")
            ? RegistryKey.of(RegistryKeys.WORLD, net.minecraft.util.Identifier.of(tag.getString("dimension")))
            : null;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        tag.putBoolean("restrained", restrained);
        if (anchorPos != null) {
            tag.putInt("anchor_x", anchorPos.getX());
            tag.putInt("anchor_y", anchorPos.getY());
            tag.putInt("anchor_z", anchorPos.getZ());
        }
        if (dimension != null) {
            tag.putString("dimension", dimension.getValue().toString());
        }
    }
}
