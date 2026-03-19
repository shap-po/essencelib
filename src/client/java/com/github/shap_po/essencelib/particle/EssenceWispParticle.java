package com.github.shap_po.essencelib.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/**
 * End-rod-style glow particle for essence wisps (no Lodestone dependency).
 */
@Environment(EnvType.CLIENT)
public class EssenceWispParticle extends AnimatedParticle {
    private final SpriteProvider essenceSpriteProvider;

    protected EssenceWispParticle(ClientWorld world, double x, double y, double z, double vx, double vy, double vz, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0f);
        this.essenceSpriteProvider = spriteProvider;
        setVelocity(vx, vy, vz);
        setMaxAge(30 + world.random.nextInt(20));
        scale(0.15f);
        setColor(1f, 1f, 1f);
        // Ensure the first rendered frame has a bound sprite.
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteForAge(this.essenceSpriteProvider);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
            return new EssenceWispParticle(world, x, y, z, vx, vy, vz, spriteProvider);
        }
    }
}
