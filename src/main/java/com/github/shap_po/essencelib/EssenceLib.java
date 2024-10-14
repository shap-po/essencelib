package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.essence.EssenceLoader;
import com.github.shap_po.essencelib.networking.ModPackets;
import com.github.shap_po.essencelib.networking.ModPacketsC2S;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssenceLib implements ModInitializer {
    public static final String MOD_ID = "essencelib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {// This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world from EssenceLib!");

        ModDataComponentTypes.registerDataComponentTypes();
        ModItems.register();

        ModPackets.register();
        ModPacketsC2S.register();

        EssenceLoader essenceLoader = new EssenceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(essenceLoader);
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
