package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.command.EssenceLibCommand;
import com.github.shap_po.essencelib.component.UniqueKillsCounterComponent;
import com.github.shap_po.essencelib.component.UniqueKillsCounterComponentImpl;
import com.github.shap_po.essencelib.essence.EssenceLoader;
import com.github.shap_po.essencelib.networking.ModPackets;
import com.github.shap_po.essencelib.networking.ModPacketsC2S;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssenceLib implements ModInitializer, EntityComponentInitializer {
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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> EssenceLibCommand.register(dispatcher));
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(UniqueKillsCounterComponent.KEY, UniqueKillsCounterComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
