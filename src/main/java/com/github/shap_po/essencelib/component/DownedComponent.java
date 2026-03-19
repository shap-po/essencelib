package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

/**
 * Tracks whether a player is in the "downed" state (would have died but is instead
 * incapacitated until revived). Used for the downed/revive system.
 */
public interface DownedComponent extends AutoSyncedComponent {

    @ApiStatus.Internal
    ComponentKey<DownedComponent> KEY = ComponentRegistry.getOrCreate(EssenceLib.identifier("downed"), DownedComponent.class);

    static Optional<DownedComponent> getOptional(@Nullable PlayerEntity entity) {
        if (entity != null && entity.asComponentProvider().getComponentContainer() != null) {
            return KEY.maybeGet(entity);
        }
        return Optional.empty();
    }

    @Nullable
    static DownedComponent getNullable(@Nullable PlayerEntity entity) {
        return getOptional(entity).orElse(null);
    }

    static boolean isDowned(@Nullable PlayerEntity entity) {
        DownedComponent c = getNullable(entity);
        return c != null && c.isDowned();
    }

    boolean isDowned();

    void setDowned(boolean downed);

    int getEssenceSlotsOccupied();

    void setEssenceSlotsOccupied(int occupied);

    int getLastDownedCauseCode();

    void setLastDownedCauseCode(int causeCode);

    int getLastDownedEpochSeconds();

    void setLastDownedEpochSeconds(int epochSeconds);

    @Nullable UUID getLastDownedKillerUuid();

    void setLastDownedKillerUuid(@Nullable UUID killerUuid);

    boolean hasCorpseLocation();

    @Nullable Identifier getCorpseDimensionId();

    @Nullable BlockPos getCorpsePos();

    void setCorpseLocation(@Nullable Identifier dimensionId, @Nullable BlockPos pos);

    void clearCorpseLocation();
}
