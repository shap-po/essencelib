package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EssenceLibParticles {

    public static final SimpleParticleType ESSENCE_WISP = FabricParticleTypes.simple(true);

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, EssenceLib.identifier("essence_wisp"), ESSENCE_WISP);
    }
}
