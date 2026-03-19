package com.github.shap_po.essencelib.block;

import com.github.shap_po.essencelib.EssenceLib;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeoModel for the guillotine block. Loads geo, texture, and animation from assets.
 * Export guillotine.geo.json from BlockBench (File → Convert Project → GeckoLib Animated Block, then Export).
 */
public class GuillotineBlockModel extends GeoModel<GuillotineBlockEntity> {

    private static final Identifier MODEL = EssenceLib.identifier("geo/guillotine.geo.json");
    private static final Identifier TEXTURE = EssenceLib.identifier("textures/block/guillotine.png");
    private static final Identifier ANIMATION = EssenceLib.identifier("animations/guillotine.animation.json");

    @Override
    public Identifier getModelResource(GuillotineBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GuillotineBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GuillotineBlockEntity animatable) {
        return ANIMATION;
    }
}
