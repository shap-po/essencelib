package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.collector.CollectorRushCapacity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class CollectorRushComponentImpl implements CollectorRushComponent {

    private final PlayerEntity provider;
    /** LinkedHashSet preserves insertion order so we can remove oldest first. */
    private final LinkedHashSet<String> collectedIds = new LinkedHashSet<>();

    public CollectorRushComponentImpl(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public Set<String> getCollectedItemIds() {
        return Collections.unmodifiableSet(collectedIds);
    }

    @Override
    public boolean addCollectedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return addCollectedItem(id);
    }

    @Override
    public boolean addCollectedItem(Identifier itemId) {
        if (itemId == null) return false;
        String key = itemId.toString();
        boolean wasNew = collectedIds.add(key);
        if (wasNew) {
            trimToCapacity(CollectorRushCapacity.getCapacity());
            sync();
        }
        return wasNew;
    }

    @Override
    public boolean hasCollectedItem(Identifier itemId) {
        return itemId != null && collectedIds.contains(itemId.toString());
    }

    @Override
    public boolean hasCollectedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return hasCollectedItem(Registries.ITEM.getId(stack.getItem()));
    }

    @Override
    public void trimToCapacity(int maxSize) {
        while (collectedIds.size() > maxSize) {
            Iterator<String> it = collectedIds.iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            } else {
                break;
            }
        }
    }

    @Override
    public void clear() {
        if (collectedIds.isEmpty()) return;
        collectedIds.clear();
        sync();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        collectedIds.clear();
        NbtList list = tag.getList("collected_items", NbtElement.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            String id = list.getString(i);
            if (id != null && !id.isEmpty()) {
                collectedIds.add(id);
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = new NbtList();
        for (String id : collectedIds) {
            list.add(NbtString.of(id));
        }
        tag.put("collected_items", list);
    }

    @Override
    public boolean shouldSyncWith(net.minecraft.server.network.ServerPlayerEntity player) {
        return player == this.provider;
    }

    @Override
    public void sync() {
        KEY.sync(this.provider);
    }
}
