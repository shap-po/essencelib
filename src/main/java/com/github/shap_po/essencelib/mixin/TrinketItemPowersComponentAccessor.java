package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.shappoli.integration.trinkets.component.item.TrinketItemPowersComponent;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrinketItemPowersComponent.class)
public interface TrinketItemPowersComponentAccessor {
    @Accessor("entries")
    ObjectLinkedOpenHashSet<TrinketItemPowersComponent.Entry> getEntries();
} 