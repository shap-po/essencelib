package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TrinketItemPowersComponent.class, remap = false)
public interface TrinketItemPowersComponentAccessor {
    @Accessor(value = "entries", remap = false)
    ObjectLinkedOpenHashSet<TrinketItemPowersComponent.Entry> getEntries();
} 