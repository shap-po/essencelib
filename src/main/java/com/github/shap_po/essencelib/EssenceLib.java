package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.command.EssenceLibCommand;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.component.LevelComponentImpl;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.level.LevelManager;
import com.github.shap_po.essencelib.loot.function.ModLootFunctionTypes;
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

    public static final String KEYBINDINGS_CATEGORY = "key.category." + EssenceLib.MOD_ID;
    public static final int MAX_SLOT_COUNT = 4;

    @Override
    public void onInitialize() {// This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world from EssenceLib!");

        ModDataComponentTypes.registerDataComponentTypes();
        ModItems.register();
        ModPackets.register();
        ModPacketsC2S.register();
        ModLootFunctionTypes.register();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new EssenceManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new LevelManager());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> EssenceLibCommand.register(dispatcher));
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(LevelComponent.KEY, LevelComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
