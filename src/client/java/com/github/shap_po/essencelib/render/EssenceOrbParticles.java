package com.github.shap_po.essencelib.render;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.mixin.client.ItemEntityAccessor;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class EssenceOrbParticles {
    private static final boolean ENABLED = false;

    private static final int TICK_INTERVAL = 1;
    private static final int PARTICLES_PER_TICK = 1;
    private static final int BURST_INTERVAL = 40;
    private static final int BURST_EXTRA = 0;

    // Wisp-like "galaxy cloud" around the top orb anchor.
    private static final float CLOUD_RADIUS_XZ = 0.11f;
    private static final float CLOUD_RADIUS_Y = 0.04f;
    private static final float CLOUD_CORE_RADIUS = 0.03f;
    private static final float DRIFT_UP = 0.0045f;
    private static final float DRIFT_OUT = 0.0012f;
    private static final float DRIFT_IN = 0.0010f;
    private static final float ORBIT_STRENGTH = 0.008f;
    private static final float TURBULENCE = 0.0025f;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(EssenceOrbParticles::tick);
    }

    private static void tick(MinecraftClient client) {
        if (!ENABLED) return;
        World world = client.world;
        var player = client.player;
        if (world == null || player == null) return;
        long tick = world.getTime();
        double range = 14.0;
        for (ItemEntity entity : world.getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(range), e -> true)) {
            if (entity.getStack().isEmpty() || !(entity.getStack().getItem() instanceof MobEssenceTrinketItem)) continue;
            if ((tick + entity.getId()) % TICK_INTERVAL != 0) continue;
            boolean burst = (tick + entity.getId()) % BURST_INTERVAL == 0;
            spawnEssenceParticles(client, world, entity, burst);
        }

        // Held item particles (Wispy effect in hand)
        for (PlayerEntity p : world.getPlayers()) {
            if (p.getMainHandStack().getItem() instanceof MobEssenceTrinketItem) {
                spawnHeldItemParticles(world, p, p.getMainHandStack(), true);
            }
            if (p.getOffHandStack().getItem() instanceof MobEssenceTrinketItem) {
                spawnHeldItemParticles(world, p, p.getOffHandStack(), false);
            }
        }
    }

    private static void spawnHeldItemParticles(World world, PlayerEntity player, ItemStack stack, boolean mainHand) {
        // Intentionally disabled for visual clarity.
    }

    private static void spawnEssenceParticles(MinecraftClient client, World world, ItemEntity entity, boolean burst) {
        if (client.player == null) return;
        Identifier essenceId = entity.getStack().get(ModDataComponentTypes.ESSENCE_ID);
        boolean canTake = essenceId == null || !MobEssenceTrinketItem.hasEssenceInPossession(client.player, essenceId);
        double cx = entity.getX();
        // Match SoulOrbEntityRenderer's vertical anchor (top floating orb), not item center.
        float age = entity.getItemAge();
        float uniqueOffset = ((ItemEntityAccessor) (Object) entity).essencelib$getUniqueOffset();
        float bob = MathHelper.sin(age / 10.0f + uniqueOffset) * 0.1f + 0.1f;
        double cy = entity.getY() + 1.0 + bob;
        double cz = entity.getZ();

        Random rand = world.random;
        int count = PARTICLES_PER_TICK + (burst ? BURST_EXTRA : 0);

        for (int i = 0; i < count; i++) {
            Vec3d pos = pointInCloud(rand);
            double px = cx + pos.x;
            double py = cy + pos.y;
            double pz = cz + pos.z;
            Vec3d vel = velocityInCloud(rand, pos, canTake);

            world.addParticle(ParticleTypes.END_ROD, px, py, pz, vel.x, vel.y, vel.z);
        }

        // Ambient "lightning" crackle near the orb shell.
        if (canTake && rand.nextFloat() < 0.08f) {
            double lx = cx + (rand.nextDouble() - 0.5) * CLOUD_RADIUS_XZ * 1.6;
            double ly = cy + (rand.nextDouble() - 0.5) * CLOUD_RADIUS_Y * 1.4;
            double lz = cz + (rand.nextDouble() - 0.5) * CLOUD_RADIUS_XZ * 1.6;
            double lvx = (rand.nextDouble() - 0.5) * 0.01;
            double lvy = (rand.nextDouble() - 0.5) * 0.007;
            double lvz = (rand.nextDouble() - 0.5) * 0.01;
            world.addParticle(ParticleTypes.ELECTRIC_SPARK, lx, ly, lz, lvx, lvy, lvz);
        }
    }

    private static Vec3d pointInCloud(Random rand) {
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        // Bias to the cloud shell, but keep particles in the volume for a nebula look.
        double shellBias = Math.pow(rand.nextDouble(), 0.55);
        double radius = MathHelper.lerp(shellBias, CLOUD_CORE_RADIUS, CLOUD_RADIUS_XZ);
        double y = (rand.nextDouble() * 2.0 - 1.0) * CLOUD_RADIUS_Y;
        return new Vec3d(
            radius * Math.cos(theta),
            y,
            radius * Math.sin(theta)
        );
    }

    private static Vec3d velocityInCloud(Random rand, Vec3d fromCenter, boolean canTake) {
        Vec3d outDir = fromCenter.normalize();

        double up = DRIFT_UP * (0.55 + rand.nextDouble() * 0.9);
        double out = DRIFT_OUT * (0.4 + rand.nextDouble() * 1.2);
        double inward = DRIFT_IN * (0.2 + rand.nextDouble() * 1.0);

        // Counter-rotating eddies make it feel like a chaotic galaxy cloud.
        double tangent = ORBIT_STRENGTH * (canTake ? 1 : -1) * (rand.nextBoolean() ? 1 : -1);
        double tx = -fromCenter.z * tangent;
        double tz = fromCenter.x * tangent;

        double noiseX = (rand.nextDouble() - 0.5) * TURBULENCE;
        double noiseY = (rand.nextDouble() - 0.5) * TURBULENCE * 0.8;
        double noiseZ = (rand.nextDouble() - 0.5) * TURBULENCE;

        return new Vec3d(
            outDir.x * (out - inward) + tx + noiseX,
            up + noiseY,
            outDir.z * (out - inward) + tz + noiseZ
        );
    }

    private EssenceOrbParticles() {}
}
