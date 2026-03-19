package com.github.shap_po.essencelib.power.type;

import com.github.shap_po.essencelib.power.EssenceLibPowerTypes;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyPlayerModelPowerType extends PowerType {

    public static final TypedDataObjectFactory<ModifyPlayerModelPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
        new SerializableData()
            .add("model", SerializableDataTypes.IDENTIFIER)
            .add("texture", SerializableDataTypes.IDENTIFIER, null),
        (data, condition) -> new ModifyPlayerModelPowerType(
            data.getId("model"),
            data.getId("texture"),
            condition
        ),
        (powerType, serializableData) -> serializableData.instance()
            .set("model", powerType.model)
            .set("texture", powerType.texture)
    );

    private final Identifier model;
    private final Identifier texture;

    public ModifyPlayerModelPowerType(Identifier model, Identifier texture, Optional<EntityCondition> condition) {
        super(condition);
        this.model = model;
        this.texture = texture;
    }

    public Identifier getModel() {
        return model;
    }

    public Identifier getTexture() {
        return texture;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return EssenceLibPowerTypes.MODIFY_PLAYER_MODEL;
    }
}
