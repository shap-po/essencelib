package com.github.shap_po.essencelib.action.type.bientity;

import com.github.shap_po.essencelib.action.type.EssenceLibBiEntityActionTypes;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class PullTowardsActorBiEntityActionType extends BiEntityActionType {

    public static final TypedDataObjectFactory<PullTowardsActorBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("strength", SerializableDataTypes.DOUBLE, 1.0),
        data -> new PullTowardsActorBiEntityActionType(data.getDouble("strength")),
        (actionType, serializableData) -> serializableData.instance()
            .set("strength", actionType.strength)
    );

    private final double strength;

    public PullTowardsActorBiEntityActionType(double strength) {
        this.strength = strength;
    }

    @Override
    public void accept(BiEntityActionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();
        
        if (actor == null || target == null) return;

        Vec3d vec = actor.getPos().subtract(target.getPos()).normalize().multiply(strength);
        
        target.addVelocity(vec.x, vec.y, vec.z);
        target.velocityModified = true;
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EssenceLibBiEntityActionTypes.PULL_TOWARDS_ACTOR;
    }
}
