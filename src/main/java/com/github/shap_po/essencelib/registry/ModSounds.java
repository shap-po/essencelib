package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    public static final Identifier NUISANCE_ALLAY_LAUGH_ID = EssenceLib.identifier("nuisance.allay_laugh");
    public static final SoundEvent NUISANCE_ALLAY_LAUGH = SoundEvent.of(NUISANCE_ALLAY_LAUGH_ID);

    private ModSounds() {}

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, NUISANCE_ALLAY_LAUGH_ID, NUISANCE_ALLAY_LAUGH);
    }
}
