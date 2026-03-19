package com.github.shap_po.essencelib.block.entity;

import com.github.shap_po.essencelib.registry.ModBlockEntities;
import com.github.shap_po.essencelib.screen.DownedPlayerLootScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class DownedCorpseBlockEntity extends BlockEntity implements Inventory, GeoBlockEntity {
    public static final int LOOT_SIZE = 41;
    private static final RawAnimation DOWNED_ANIM = RawAnimation.begin()
        .thenPlay("Being Downed")
        .thenLoop("Downed");
    public static final String CONTROLLER_NAME = "downed_corpse_controller";

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(LOOT_SIZE, ItemStack.EMPTY);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String ownerName = "";
    private String ownerUuid = "";
    private int occupiedEssenceSlots;
    private int causeCode;
    private int downedEpochSeconds;

    public DownedCorpseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOWNED_CORPSE, pos, state);
    }

    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new DownedPlayerLootScreenHandler(syncId, playerInventory, this, null,
            occupiedEssenceSlots, causeCode, downedEpochSeconds);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName == null ? "" : ownerName;
        markDirty();
    }

    public void setOwnerUuid(@Nullable UUID ownerUuid) {
        this.ownerUuid = ownerUuid == null ? "" : ownerUuid.toString();
        markDirty();
    }

    public @Nullable UUID getOwnerUuid() {
        if (ownerUuid == null || ownerUuid.isBlank()) return null;
        try {
            return UUID.fromString(ownerUuid);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public void setIntel(int occupiedEssenceSlots, int causeCode, int downedEpochSeconds) {
        this.occupiedEssenceSlots = Math.max(0, occupiedEssenceSlots);
        this.causeCode = Math.max(0, causeCode);
        this.downedEpochSeconds = Math.max(0, downedEpochSeconds);
        markDirty();
    }

    @Override
    public int size() {
        return LOOT_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(stacks, slot, amount);
        if (!stack.isEmpty()) {
            markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(stacks, slot);
        if (!stack.isEmpty()) {
            markDirty();
        }
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null || world.getBlockEntity(pos) != this) {
            return false;
        }
        return player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        stacks.clear();
        markDirty();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, stacks, registries);
        ownerName = nbt.contains("OwnerName") ? nbt.getString("OwnerName") : "";
        ownerUuid = nbt.contains("OwnerUuid") ? nbt.getString("OwnerUuid") : "";
        occupiedEssenceSlots = Math.max(0, nbt.getInt("OccupiedEssenceSlots"));
        causeCode = Math.max(0, nbt.getInt("CauseCode"));
        downedEpochSeconds = Math.max(0, nbt.getInt("DownedEpochSec"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, stacks, registries);
        nbt.putString("OwnerName", ownerName == null ? "" : ownerName);
        nbt.putString("OwnerUuid", ownerUuid == null ? "" : ownerUuid);
        nbt.putInt("OccupiedEssenceSlots", occupiedEssenceSlots);
        nbt.putInt("CauseCode", causeCode);
        nbt.putInt("DownedEpochSec", downedEpochSeconds);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER_NAME, 0, state -> {
            state.setAnimation(DOWNED_ANIM);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
