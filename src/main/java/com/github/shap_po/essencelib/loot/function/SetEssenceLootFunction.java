package com.github.shap_po.essencelib.loot.function;

import com.github.shap_po.essencelib.data.EssenceLibDataTypes;
import com.github.shap_po.essencelib.essence.EssenceReference;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;

import java.util.List;

public class SetEssenceLootFunction extends ConditionalLootFunction {
    public static final MapCodec<SetEssenceLootFunction> CODEC = RecordCodecBuilder.mapCodec(
        (instance) -> addConditionsField(instance)
            .and(EssenceLibDataTypes.ESSENCE_REFERENCE.codec()
                .fieldOf("essence")
                .forGetter((function) -> function.essenceRef))
            .apply(instance, SetEssenceLootFunction::new)
    );
    private final EssenceReference essenceRef;

    private SetEssenceLootFunction(List<LootCondition> conditions, EssenceReference essenceRef) {
        super(conditions);
        this.essenceRef = essenceRef;
    }

    @SuppressWarnings({"unused"})
    public static ConditionalLootFunction.Builder<?> builder(EssenceReference essence) {
        return builder((conditions) -> new SetEssenceLootFunction(conditions, essence));
    }

    @Override
    public LootFunctionType<SetEssenceLootFunction> getType() {
        return ModLootFunctionTypes.SET_ESSENCE;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        return essenceRef.getOptionalEssence()
            .map(essence -> essence.applyToItemStack(stack))
            .orElse(stack);
    }
}
