package com.github.shap_po.essencelib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared cache for dummy entities used by essence renderers.
 * <p>
 * Important: entities are world-bound; if the client world changes (dimension/rejoin/unload),
 * the cache must be cleared before we create/restore any entities.
 */
@Environment(EnvType.CLIENT)
public final class EssenceDummyEntityCache {

    private static final Map<EntityType<?>, Entity> CACHE = new HashMap<>();
    private static World cachedWorld = null;

    private EssenceDummyEntityCache() {}

    public static Entity getOrCreate(EntityType<?> type) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null || type == null) return null;

        // Entities are tied to their world; invalidate on any world instance change.
        if (cachedWorld != world) {
            CACHE.clear();
            cachedWorld = world;
        }

        Entity entity = CACHE.computeIfAbsent(type, t -> t.create(world));
        // Never return an entity whose world was unloaded or no longer matches (join/transition).
        if (entity != null && (entity.getWorld() == null || entity.getWorld() != world)) {
            CACHE.remove(type);
            return null;
        }
        return entity;
    }

    public static void clear() {
        CACHE.clear();
        cachedWorld = null;
    }
}

