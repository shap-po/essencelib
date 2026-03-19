package com.github.shap_po.essencelib.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DownedComponentImpl implements DownedComponent {
    private final PlayerEntity provider;
    private boolean downed;
    private int essenceSlotsOccupied;
    private int lastDownedCauseCode;
    private int lastDownedEpochSeconds;
    private UUID lastDownedKillerUuid;
    private Identifier corpseDimensionId;
    private BlockPos corpsePos;

    public DownedComponentImpl(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public boolean isDowned() {
        return downed;
    }

    @Override
    public void setDowned(boolean downed) {
        if (this.downed == downed) return;
        this.downed = downed;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public int getEssenceSlotsOccupied() {
        return essenceSlotsOccupied;
    }

    @Override
    public void setEssenceSlotsOccupied(int occupied) {
        int clamped = Math.max(0, occupied);
        if (this.essenceSlotsOccupied == clamped) return;
        this.essenceSlotsOccupied = clamped;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public int getLastDownedCauseCode() {
        return lastDownedCauseCode;
    }

    @Override
    public void setLastDownedCauseCode(int causeCode) {
        if (this.lastDownedCauseCode == causeCode) return;
        this.lastDownedCauseCode = causeCode;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public int getLastDownedEpochSeconds() {
        return lastDownedEpochSeconds;
    }

    @Override
    public void setLastDownedEpochSeconds(int epochSeconds) {
        int clamped = Math.max(0, epochSeconds);
        if (this.lastDownedEpochSeconds == clamped) return;
        this.lastDownedEpochSeconds = clamped;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public @Nullable UUID getLastDownedKillerUuid() {
        return lastDownedKillerUuid;
    }

    @Override
    public void setLastDownedKillerUuid(@Nullable UUID killerUuid) {
        if ((lastDownedKillerUuid == null && killerUuid == null)
            || (lastDownedKillerUuid != null && lastDownedKillerUuid.equals(killerUuid))) {
            return;
        }
        this.lastDownedKillerUuid = killerUuid;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public boolean hasCorpseLocation() {
        return corpseDimensionId != null && corpsePos != null;
    }

    @Override
    public @Nullable Identifier getCorpseDimensionId() {
        return corpseDimensionId;
    }

    @Override
    public @Nullable BlockPos getCorpsePos() {
        return corpsePos;
    }

    @Override
    public void setCorpseLocation(@Nullable Identifier dimensionId, @Nullable BlockPos pos) {
        this.corpseDimensionId = dimensionId;
        this.corpsePos = pos;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public void clearCorpseLocation() {
        if (corpseDimensionId == null && corpsePos == null) return;
        corpseDimensionId = null;
        corpsePos = null;
        DownedComponent.KEY.sync(provider);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        downed = tag.getBoolean("downed");
        essenceSlotsOccupied = Math.max(0, tag.getInt("essence_slots_occupied"));
        lastDownedCauseCode = Math.max(0, tag.getInt("last_downed_cause_code"));
        lastDownedEpochSeconds = Math.max(0, tag.getInt("last_downed_epoch_sec"));
        lastDownedKillerUuid = null;
        if (tag.contains("last_downed_killer_uuid")) {
            try {
                lastDownedKillerUuid = UUID.fromString(tag.getString("last_downed_killer_uuid"));
            } catch (IllegalArgumentException ignored) {
                lastDownedKillerUuid = null;
            }
        }
        corpseDimensionId = null;
        corpsePos = null;
        if (tag.contains("corpse_dim")) {
            corpseDimensionId = Identifier.tryParse(tag.getString("corpse_dim"));
        }
        if (tag.contains("corpse_x") && tag.contains("corpse_y") && tag.contains("corpse_z")) {
            corpsePos = new BlockPos(tag.getInt("corpse_x"), tag.getInt("corpse_y"), tag.getInt("corpse_z"));
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putBoolean("downed", downed);
        tag.putInt("essence_slots_occupied", essenceSlotsOccupied);
        tag.putInt("last_downed_cause_code", lastDownedCauseCode);
        tag.putInt("last_downed_epoch_sec", lastDownedEpochSeconds);
        if (lastDownedKillerUuid != null) {
            tag.putString("last_downed_killer_uuid", lastDownedKillerUuid.toString());
        }
        if (corpseDimensionId != null && corpsePos != null) {
            tag.putString("corpse_dim", corpseDimensionId.toString());
            tag.putInt("corpse_x", corpsePos.getX());
            tag.putInt("corpse_y", corpsePos.getY());
            tag.putInt("corpse_z", corpsePos.getZ());
        }
    }
}
