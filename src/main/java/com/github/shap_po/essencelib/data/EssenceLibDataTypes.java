package com.github.shap_po.essencelib.data;

import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.essence.EssenceReference;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;

public class EssenceLibDataTypes {
    public static final SerializableDataType<Essence> ESSENCE = SerializableDataTypes.IDENTIFIER.comapFlatMap(EssenceManager::getResult, Essence::getId);
    public static final SerializableDataType<EssenceReference> ESSENCE_REFERENCE = SerializableDataTypes.IDENTIFIER.xmap(EssenceReference::new, EssenceReference::id);
}
