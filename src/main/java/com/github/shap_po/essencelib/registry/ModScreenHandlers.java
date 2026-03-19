package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.screen.DownedPlayerLootScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<DownedPlayerLootScreenHandler> DOWNED_LOOT = Registry.register(
        Registries.SCREEN_HANDLER,
        EssenceLib.identifier("downed_loot"),
        new ScreenHandlerType<>((syncId, playerInventory) -> new DownedPlayerLootScreenHandler(syncId, playerInventory), FeatureSet.empty())
    );

    public static void register() {
        // Type registered above
    }
}
