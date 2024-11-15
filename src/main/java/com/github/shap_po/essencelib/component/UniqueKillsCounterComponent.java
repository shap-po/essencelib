package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Optional;

public interface UniqueKillsCounterComponent extends AutoSyncedComponent {
    /**
     * <b>Use {@link #getOptional(PlayerEntity)} or {@link #getNullable(PlayerEntity)} wherever possible.</b>
     */
    @ApiStatus.Internal
    ComponentKey<UniqueKillsCounterComponent> KEY = ComponentRegistry.getOrCreate(EssenceLib.identifier("unique_kills"), UniqueKillsCounterComponent.class);

    ImmutableSet<Identifier> getUniqueKills();

    int getUniqueKillsCount();

    void addUniqueKill(Identifier id);

    void addUniqueKill(LivingEntity entity);

    void removeUniqueKill(Identifier id);

    void removeUniqueKill(LivingEntity entity);

    boolean hasUniqueKill(Identifier id);

    boolean hasUniqueKill(LivingEntity entity);

    void clearUniqueKills();

    void sync();

    /**
     * Queries the {@link UniqueKillsCounterComponent} from an {@link Entity}. This is safer and preferred than directly using
     * {@link #KEY} as it handles certain scenarios where unexpected errors may occur.
     *
     * @param entity the entity to get the component from
     * @return the unique kills component, or {@link Optional#empty()} if the entity is either null, its component
     * container hasn't been initialized yet, or if the entity doesn't/can't have the unique kills component
     */
    @SuppressWarnings("ConstantValue")
    static Optional<UniqueKillsCounterComponent> getOptional(@Nullable PlayerEntity entity) {
        EssenceLib.LOGGER.debug("Getting UniqueKillsCounterComponent from entity: {} {}", KEY, entity);
        if (entity != null && entity.asComponentProvider().getComponentContainer() != null) {
            return KEY.maybeGet(entity);
        }

        return Optional.empty();
    }

    /**
     * An alternative version of {@link #getOptional(PlayerEntity)} that returns a <b>nullable</b>
     * {@link UniqueKillsCounterComponent} instead.
     *
     * @param entity the entity to get the component from
     * @return the unique kills component, or {@code null} if the entity is either null, its component container
     * hasn't been initialized yet, or if the entity doesn't/can't have the unique kills component
     */
    @Nullable
    static UniqueKillsCounterComponent getNullable(@Nullable PlayerEntity entity) {
        return getOptional(entity).orElse(null);
    }
}
