package com.github.shap_po.essencelib.util;

/**
 * Thread-local flag for silhouette rendering mode.
 * When true, entity rendering uses a fixed dark color (solid silhouette).
 */
public final class SilhouetteRendering {

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    /** Silhouette color: solid black (R=0, G=0, B=0, A=255) */
    public static final int SILHOUETTE_R = 0;
    public static final int SILHOUETTE_G = 0;
    public static final int SILHOUETTE_B = 0;
    public static final int SILHOUETTE_A = 255;

    public static void setActive(boolean active) {
        ACTIVE.set(active);
    }

    public static boolean isActive() {
        return Boolean.TRUE.equals(ACTIVE.get());
    }
}
