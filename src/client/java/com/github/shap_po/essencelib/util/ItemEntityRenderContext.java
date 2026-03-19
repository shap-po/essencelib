package com.github.shap_po.essencelib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ItemEntity;

/**
 * Per-thread render context for the currently rendered ItemEntity.
 */
@Environment(EnvType.CLIENT)
public final class ItemEntityRenderContext {

    private static final ThreadLocal<ItemEntity> CURRENT = new ThreadLocal<>();

    public static void set(ItemEntity entity) {
        CURRENT.set(entity);
    }

    public static ItemEntity get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    private ItemEntityRenderContext() {}
}
