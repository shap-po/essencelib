package com.github.shap_po.essencelib.collector;

import com.github.shap_po.essencelib.registry.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import org.jetbrains.annotations.Nullable;

/**
 * Computes Collector's Rush capacity as 75% of total collectible items on the server.
 * Excludes air and creative-only items (item_ignorelist tag). Players hunt for the remaining 25%.
 */
public final class CollectorRushCapacity {

    private static final double CAPACITY_FRACTION = 0.75;
    private static @Nullable Integer cachedTotalItems = null;
    private static @Nullable Integer cachedCapacity = null;

    /** Total collectible item types (excludes air and item_ignorelist). */
    public static int getTotalItemCount() {
        if (cachedTotalItems == null) {
            cachedTotalItems = (int) Registries.ITEM.stream()
                .filter(item -> item != Items.AIR && !item.getDefaultStack().isIn(ModTags.ITEM_IGNORELIST))
                .count();
        }
        return cachedTotalItems;
    }

    /** Max collected items to store; 75% of total. Oldest removed when over. */
    public static int getCapacity() {
        if (cachedCapacity == null) {
            int total = getTotalItemCount();
            cachedCapacity = Math.max(1, (int) Math.ceil(total * CAPACITY_FRACTION));
        }
        return cachedCapacity;
    }

    /** Call when registries change (e.g. datapack reload). */
    public static void invalidateCache() {
        cachedTotalItems = null;
        cachedCapacity = null;
    }

    private CollectorRushCapacity() {}
}
