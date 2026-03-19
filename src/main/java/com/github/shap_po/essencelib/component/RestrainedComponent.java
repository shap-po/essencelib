package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Optional;

/**
 * Tracks when a player is restrained (e.g. in a guillotine). They cannot move, sneak out, or
 * escape until released or executed. Used so the guillotine holds the player in place.
 */
public interface RestrainedComponent extends AutoSyncedComponent {

    @ApiStatus.Internal
    ComponentKey<RestrainedComponent> KEY = ComponentRegistry.getOrCreate(EssenceLib.identifier("restrained"), RestrainedComponent.class);

    static Optional<RestrainedComponent> getOptional(@Nullable PlayerEntity entity) {
        if (entity != null && entity.asComponentProvider().getComponentContainer() != null) {
            return KEY.maybeGet(entity);
        }
        return Optional.empty();
    }

    @Nullable
    static RestrainedComponent getNullable(@Nullable PlayerEntity entity) {
        return getOptional(entity).orElse(null);
    }

    static boolean isRestrained(@Nullable PlayerEntity entity) {
        RestrainedComponent c = getNullable(entity);
        return c != null && c.isRestrained();
    }

    /** True if the player is currently restrained and cannot leave. */
    boolean isRestrained();

    /**
     * Restrain the player at the given block position (in the given dimension).
     * They will be held at the center of the block, feet on top of it.
     */
    void restrain(BlockPos anchor, RegistryKey<World> dimension);

    /** Release the player so they can move again. */
    void release();

    @Nullable
    BlockPos getAnchorPos();

    @Nullable
    RegistryKey<World> getDimension();
}
