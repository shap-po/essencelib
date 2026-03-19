package com.github.shap_po.essencelib.block;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.block.entity.DownedCorpseBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import java.util.UUID;

public class DownedCorpseBlockModel extends GeoModel<DownedCorpseBlockEntity> {
    private static final Identifier MODEL = EssenceLib.identifier("geo/downed_player.geo.json");
    private static final Identifier ANIMATION = EssenceLib.identifier("animations/downed_player.animation.json");
    private static final Identifier FALLBACK_SKIN = Identifier.ofVanilla("textures/entity/player/wide/steve.png");

    @Override
    public Identifier getModelResource(DownedCorpseBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(DownedCorpseBlockEntity animatable) {
        UUID ownerUuid = animatable.getOwnerUuid();
        if (ownerUuid == null) {
            return FALLBACK_SKIN;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) {
            return FALLBACK_SKIN;
        }
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(ownerUuid);
        if (entry == null || entry.getSkinTextures() == null || entry.getSkinTextures().texture() == null) {
            return FALLBACK_SKIN;
        }
        return entry.getSkinTextures().texture();
    }

    @Override
    public Identifier getAnimationResource(DownedCorpseBlockEntity animatable) {
        return ANIMATION;
    }
}
