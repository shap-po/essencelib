package com.github.shap_po.essencelib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ItemEntity;

/**
 * Holds the ItemEntity currently being rendered (for Collector's Intuition outline).
 * Set/cleared by ItemEntityRendererMixin; read by CollectorIntuitionItemRendererMixin.
 */
@Environment(EnvType.CLIENT)
public final class ItemEntityRenderContext {

    private static final ThreadLocal<ItemEntity> CURRENT = ThreadLocal.withInitial(() -> null);

    public static void set(ItemEntity entity) {
        CURRENT.set(entity);
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static ItemEntity get() {
        return CURRENT.get();
    }

    private ItemEntityRenderContext() {}
}
