package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.networking.ModPacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EssenceLibClient implements ClientModInitializer {
    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ModPacketsS2C.register();
    }
}
