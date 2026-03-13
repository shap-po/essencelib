package com.github.shap_po.essencelib.mixin.client;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntityRenderer.class)
public interface ItemEntityRendererAccessor {

    @Accessor("itemRenderer")
    ItemRenderer essencelib$getItemRenderer();

    @Accessor("random")
    Random essencelib$getRandom();
}
