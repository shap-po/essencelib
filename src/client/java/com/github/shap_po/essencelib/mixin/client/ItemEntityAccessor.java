package com.github.shap_po.essencelib.mixin.client;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {

    @Accessor("uniqueOffset")
    float essencelib$getUniqueOffset();
}
