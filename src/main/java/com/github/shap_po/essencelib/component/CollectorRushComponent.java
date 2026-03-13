package com.github.shap_po.essencelib.component;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Optional;
import java.util.Set;

/**
 * Stores item IDs the player has picked up for Collector's Rush.
 * Used for tracking "new" items (not re-picking your own drops) and for outline colors
 * (collected vs uncollected items in Collector's Intuition).
 */
public interface CollectorRushComponent extends AutoSyncedComponent {

    @ApiStatus.Internal
    ComponentKey<CollectorRushComponent> KEY = ComponentRegistry.getOrCreate(
        EssenceLib.identifier("collector_rush"), CollectorRushComponent.class);

    static Optional<CollectorRushComponent> getOptional(@Nullable PlayerEntity entity) {
        if (entity != null && entity.asComponentProvider().getComponentContainer() != null) {
            return KEY.maybeGet(entity);
        }
        return Optional.empty();
    }

    @Nullable
    static CollectorRushComponent getNullable(@Nullable PlayerEntity entity) {
        return getOptional(entity).orElse(null);
    }

    /** Immutable view of collected item IDs (e.g. "minecraft:diamond"). */
    Set<String> getCollectedItemIds();

    /** Adds the item from the stack to the collected set. Returns true if it was new. */
    boolean addCollectedItem(ItemStack stack);

    /** Adds the item ID directly. Returns true if it was new. */
    boolean addCollectedItem(Identifier itemId);

    boolean hasCollectedItem(Identifier itemId);

    boolean hasCollectedItem(ItemStack stack);

    /** Removes oldest entries when over capacity (75% of server's item count). */
    void trimToCapacity(int maxSize);

    void clear();

    void sync();
}
