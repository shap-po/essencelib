package com.github.shap_po.essencelib.action.type;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.action.type.entity.ConsumeManaEntityActionType;
import com.github.shap_po.essencelib.action.type.entity.RestoreManaEntityActionType;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.action.type.EntityActionTypes;

public class EssenceLibEntityActionTypes {

    public static final ActionConfiguration<ConsumeManaEntityActionType> CONSUME_MANA = EntityActionTypes.register(
        ActionConfiguration.of(EssenceLib.identifier("consume_mana"), ConsumeManaEntityActionType.DATA_FACTORY)
    );

    public static final ActionConfiguration<RestoreManaEntityActionType> RESTORE_MANA = EntityActionTypes.register(
        ActionConfiguration.of(EssenceLib.identifier("restore_mana"), RestoreManaEntityActionType.DATA_FACTORY)
    );

    public static void register() {
    }
}
