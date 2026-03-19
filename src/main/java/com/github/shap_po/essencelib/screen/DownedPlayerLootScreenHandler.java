package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.registry.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

/**
 * Screen handler for looting a downed player's inventory.
 * Server: lootInv is a copy of the downed player's inv; on close we copy back.
 * Client: lootInv is an empty SimpleInventory that gets synced from server.
 */
public class DownedPlayerLootScreenHandler extends ScreenHandler {
    private static final int LOOT_SLOTS = 41; // main 36 + armor 4 + offhand 1
    private static final int LOOT_MAIN_START = 9;   // PlayerInventory.main index for main grid
    private static final int LOOT_HOTBAR_START = 0; // PlayerInventory.main index for hotbar
    private static final int LOOT_ARMOR_START = 36; // armor 4 slots
    private static final int LOOT_OFFHAND = 40;     // offhand slot
    private static final int INTEL_OCCUPIED_ESSENCE_SLOTS = 0;
    private static final int INTEL_CAUSE_CODE = 1;
    private static final int INTEL_DOWNED_EPOCH_SECONDS = 2;
    private final Inventory lootInv;
    private final PropertyDelegate intel;
    @Nullable
    private final PlayerEntity downedPlayer;

    /** Client constructor: lootInv is a fresh inventory that will be synced. */
    public DownedPlayerLootScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(LOOT_SLOTS), null, 0, 0, 0);
    }

    /** Server constructor: lootInv is a copy of downed player's inv; on close we copy back. */
    public DownedPlayerLootScreenHandler(int syncId, PlayerInventory playerInventory, Inventory lootInv, @Nullable PlayerEntity downedPlayer,
                                         int occupiedEssenceSlots, int causeCode, int downedEpochSeconds) {
        super(ModScreenHandlers.DOWNED_LOOT, syncId);
        this.lootInv = lootInv;
        this.downedPlayer = downedPlayer;
        this.intel = new ArrayPropertyDelegate(3);
        this.intel.set(INTEL_OCCUPIED_ESSENCE_SLOTS, Math.max(0, occupiedEssenceSlots));
        this.intel.set(INTEL_CAUSE_CODE, Math.max(0, causeCode));
        this.intel.set(INTEL_DOWNED_EPOCH_SECONDS, Math.max(0, downedEpochSeconds));
        this.addProperties(this.intel);
        checkSize(lootInv, LOOT_SLOTS);

        // Corpse armor slots (helmet -> boots) at left.
        for (int i = 0; i < 4; i++) {
            addSlot(createLootSlot(lootInv, LOOT_ARMOR_START + i, 8, 18 + i * 18));
        }

        // Corpse offhand slot below armor.
        addSlot(createLootSlot(lootInv, LOOT_OFFHAND, 8, 90));

        // Corpse main inventory (3x9), mapped to player main indices 9..35.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = LOOT_MAIN_START + col + row * 9;
                addSlot(createLootSlot(lootInv, index, 30 + col * 18, 18 + row * 18));
            }
        }

        // Corpse hotbar (9), mapped to player main indices 0..8.
        for (int col = 0; col < 9; col++) {
            int index = LOOT_HOTBAR_START + col;
            addSlot(createLootSlot(lootInv, index, 30 + col * 18, 76));
        }

        // Looter's main inventory (36)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 121 + row * 18));
            }
        }
        // Looter's hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 179));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (player.getWorld().isClient || downedPlayer == null) return;
        // Copy loot inv back to downed player
        DefaultedList<ItemStack> downedMain = downedPlayer.getInventory().main;
        DefaultedList<ItemStack> downedArmor = downedPlayer.getInventory().armor;
        for (int i = 0; i < 36; i++) downedMain.set(i, lootInv.getStack(i));
        for (int i = 0; i < 4; i++) downedArmor.set(i, lootInv.getStack(36 + i));
        downedPlayer.getInventory().offHand.set(0, lootInv.getStack(40));
        downedPlayer.getInventory().markDirty();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if (index < LOOT_SLOTS) {
                if (isProtectedEssence(stackInSlot)) {
                    return ItemStack.EMPTY;
                }
                if (!insertItem(stackInSlot, LOOT_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!insertItem(stackInSlot, 0, LOOT_SLOTS, false)) return ItemStack.EMPTY;
            }
            if (stackInSlot.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return stack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return lootInv.canPlayerUse(player);
    }

    public int getOccupiedEssenceSlots() {
        return Math.max(0, intel.get(INTEL_OCCUPIED_ESSENCE_SLOTS));
    }

    public int getCauseCode() {
        return Math.max(0, intel.get(INTEL_CAUSE_CODE));
    }

    public int getDownedEpochSeconds() {
        return Math.max(0, intel.get(INTEL_DOWNED_EPOCH_SECONDS));
    }

    private static boolean isProtectedEssence(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof MobEssenceTrinketItem;
    }

    private static Slot createLootSlot(Inventory inv, int index, int x, int y) {
        return new Slot(inv, index, x, y) {
            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return !isProtectedEssence(getStack());
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        };
    }
}
