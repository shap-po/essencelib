package com.github.shap_po.essencelib.action.type.entity;

import com.github.shap_po.essencelib.action.type.EssenceLibEntityActionTypes;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.jetbrains.annotations.NotNull;

public class ConsumeManaEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<ConsumeManaEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("amount", SerializableDataTypes.FLOAT),
        data -> new ConsumeManaEntityActionType(data.getFloat("amount")),
        (actionType, serializableData) -> serializableData.instance()
            .set("amount", actionType.amount)
    );

    private final float amount;

    public ConsumeManaEntityActionType(float amount) {
        this.amount = amount;
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof LivingEntity living)) return;

        EntityAttributeInstance instance = living.getAttributeInstance(ManaAttributeRegistry.getCurrentManaEntry());
        if (instance == null) return;

        double current = instance.getBaseValue();
        instance.setBaseValue(Math.max(0, current - amount));
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EssenceLibEntityActionTypes.CONSUME_MANA;
    }
}
