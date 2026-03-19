package com.github.shap_po.essencelib.action.type;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.action.type.bientity.PullTowardsActorBiEntityActionType;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.BiEntityActionTypes;

public class EssenceLibBiEntityActionTypes {

    public static final ActionConfiguration<PullTowardsActorBiEntityActionType> PULL_TOWARDS_ACTOR = BiEntityActionTypes.register(
        ActionConfiguration.of(EssenceLib.identifier("pull_towards_actor"), PullTowardsActorBiEntityActionType.DATA_FACTORY)
    );

    public static void register() {
    }
}
