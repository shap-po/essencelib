package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.component.item.MobEssenceItemComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModDataComponentTypes {
    public static final ComponentType<MobEssenceItemComponent> MOB_ESSENCE = ComponentType.<MobEssenceItemComponent>builder()
        .codec(MobEssenceItemComponent.CODEC)
        .packetCodec(MobEssenceItemComponent.PACKET_CODEC)
        .build();

    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, EssenceLib.identifier("mob_essence"), MOB_ESSENCE);
    }
}
