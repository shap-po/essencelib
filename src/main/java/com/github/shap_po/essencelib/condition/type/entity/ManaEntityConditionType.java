package com.github.shap_po.essencelib.condition.type.entity;

import com.github.shap_po.essencelib.condition.type.EssenceLibEntityConditionTypes;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ManaEntityConditionType extends EntityConditionType {
    public static final TypedDataObjectFactory<ManaEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.FLOAT),
        data -> new ManaEntityConditionType(
            data.get("comparison"),
            data.getInt("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final Comparison comparison;
    private final int compareTo;

    public ManaEntityConditionType(Comparison comparison, int compareTo) {
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(EntityConditionContext context) {
        if (!(context.entity() instanceof LivingEntity livingEntity)) {
            return false;
        }

        return comparison.compare(livingEntity.getAttributeValue(ManaAttributeRegistry.getCurrentManaEntry()), compareTo);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EssenceLibEntityConditionTypes.MANA;
    }
}
