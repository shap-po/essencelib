package com.github.shap_po.essencelib.component;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class UniqueKillsCounterComponentImpl implements UniqueKillsCounterComponent {
    private final Set<Identifier> uniqueKills = new HashSet<>();
    private final PlayerEntity provider;

    public UniqueKillsCounterComponentImpl(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public ImmutableSet<Identifier> getUniqueKills() {
        return ImmutableSet.copyOf(uniqueKills);
    }

    @Override
    public int getUniqueKillsCount() {
        return uniqueKills.size();
    }

    @Override
    public void addUniqueKill(Identifier id) {
        if (provider.isCreative() || uniqueKills.contains(id)) {
            return;
        }
        uniqueKills.add(id);
        sync();
    }

    @Override
    public void addUniqueKill(LivingEntity entity) {
        addUniqueKill(getEntityId(entity));
    }

    @Override
    public void removeUniqueKill(Identifier id) {
        if (!uniqueKills.contains(id)) {
            return;
        }
        uniqueKills.remove(id);
        sync();
    }

    @Override
    public void removeUniqueKill(LivingEntity entity) {
        removeUniqueKill(getEntityId(entity));
    }

    @Override
    public boolean hasUniqueKill(Identifier id) {
        return uniqueKills.contains(id);
    }

    @Override
    public boolean hasUniqueKill(LivingEntity entity) {
        return hasUniqueKill(getEntityId(entity));
    }

    @Override
    public void clearUniqueKills() {
        if (uniqueKills.isEmpty()) {
            return;
        }
        uniqueKills.clear();
        sync();
    }

    private Identifier getEntityId(LivingEntity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType());
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider; // no need to sync with other players
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound compoundTag, RegistryWrapper.WrapperLookup lookup) {
        uniqueKills.clear();
        NbtList powersTag = compoundTag.getList("unique_kills", NbtElement.STRING_TYPE);

        for (int i = 0; i < powersTag.size(); i++) {
            String id = powersTag.getString(i);
            if (id == null) {
                continue;
            }

            Identifier identifier = Identifier.tryParse(id);
            if (identifier == null) {
                continue;
            }

            uniqueKills.add(identifier);
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound compoundTag, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = new NbtList();

        for (Identifier id : uniqueKills) {
            list.add(NbtString.of(id.toString()));
        }

        compoundTag.put("unique_kills", list);
    }

    @Override
    public void sync() {
        KEY.sync(this.provider);
    }
}