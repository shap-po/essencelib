package com.github.shap_po.essencelib.block;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Renders the guillotine block with its GeckoLib model and animation.
 */
public class GuillotineBlockRenderer extends GeoBlockRenderer<GuillotineBlockEntity> {

    public GuillotineBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new GuillotineBlockModel());
    }
}
