package com.github.shap_po.essencelib.condition.type;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.condition.type.entity.ManaEntityConditionType;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.EntityConditionTypes;

public class EssenceLibEntityConditionTypes {
    public static final ConditionConfiguration<ManaEntityConditionType> MANA = EntityConditionTypes.register(ConditionConfiguration.of(EssenceLib.identifier("mana"), ManaEntityConditionType.DATA_FACTORY));

    public static void register() {
    }
}
