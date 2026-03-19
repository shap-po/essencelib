package com.github.shap_po.essencelib.power;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.power.type.ModifyPlayerModelPowerType;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerTypes;

public class EssenceLibPowerTypes {

    public static final PowerConfiguration<ModifyPlayerModelPowerType> MODIFY_PLAYER_MODEL = PowerTypes.register(
        PowerConfiguration.of(EssenceLib.identifier("modify_player_model"), ModifyPlayerModelPowerType.DATA_FACTORY)
    );

    public static void register() {
    }
}
