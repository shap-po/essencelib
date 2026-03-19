package com.github.shap_po.essencelib.render;

import com.github.shap_po.essencelib.collector.CollectorRushHelper;
import com.github.shap_po.essencelib.util.ClientAbilityHookConfig;
import com.github.shap_po.essencelib.util.EquippedEssencePowerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;

/**
 * Spawns a subtle hint particle on item entities that are NOT in the player's collection list,
 * when the player has Collector's Rush (lifestyle ability). Soul orb visuals are handled
 * by the dedicated 3D renderer layer.
 */
@Environment(EnvType.CLIENT)
public final class UncollectedItemParticleRenderer {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(UncollectedItemParticleRenderer::tick);
    }

    private static void tick(MinecraftClient client) {
        var player = client.player;
        var world = client.world;
        if (player == null || world == null) return;
        ClientAbilityHookConfig.Config cfg = ClientAbilityHookConfig.get();
        double range = Math.max(0.0, cfg.uncollectedHintRange());
        if (range <= 0.0) return;
        double rangeSq = range * range;
        int interval = Math.max(1, cfg.uncollectedHintInterval());
        ParticleEffect particle = resolveParticle(cfg.uncollectedHintParticle());

        boolean hasHintPower = EquippedEssencePowerHelper.hasVisibleEquippedPower(player, cfg.uncollectedHintRequiredPower());
        long tick = world.getTime();
        for (var entity : world.getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(range), e -> true)) {
            if (player.squaredDistanceTo(entity) > rangeSq) continue;
            if (entity.getStack().isEmpty()) continue;
            long entityId = entity.getId();

            if (!hasHintPower) continue;
            if (CollectorRushHelper.isCollected(player, entity.getStack())) continue;
            if ((tick + entityId) % interval != 0) continue;
            spawnUncollectedHintParticle(world, entity, particle);
        }
    }

    private static ParticleEffect resolveParticle(net.minecraft.util.Identifier particleId) {
        ParticleType<?> type = Registries.PARTICLE_TYPE.get(particleId);
        if (type instanceof ParticleEffect effect) {
            return effect;
        }
        return net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER;
    }

    private static void spawnUncollectedHintParticle(World world, ItemEntity entity, ParticleEffect particle) {
        double x = entity.getX() + (world.random.nextDouble() - 0.5) * 0.3;
        double y = entity.getY() + entity.getHeight() * 0.3 + world.random.nextDouble() * 0.2;
        double z = entity.getZ() + (world.random.nextDouble() - 0.5) * 0.3;

        // Soft green sparkle for items not yet collected while Collector's Rush is active
        world.addParticle(
            particle,
            x, y, z,
            0, 0.015, 0  // very slow upward drift
        );
    }

    private UncollectedItemParticleRenderer() {}
}
