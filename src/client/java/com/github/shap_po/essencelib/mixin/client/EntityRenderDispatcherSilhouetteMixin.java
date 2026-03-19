package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.render.FixedColorVertexConsumerProvider;
import com.github.shap_po.essencelib.util.SilhouetteRendering;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * When silhouette rendering is active, wraps the VertexConsumerProvider so all
 * entity vertices are drawn with a fixed dark color (solid silhouette).
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherSilhouetteMixin {

    @ModifyVariable(
        method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private VertexConsumerProvider essencelib$wrapForSilhouette(VertexConsumerProvider vertexConsumers) {
        if (!SilhouetteRendering.isActive()) {
            return vertexConsumers;
        }
        return new FixedColorVertexConsumerProvider(
            vertexConsumers,
            SilhouetteRendering.SILHOUETTE_R,
            SilhouetteRendering.SILHOUETTE_G,
            SilhouetteRendering.SILHOUETTE_B,
            SilhouetteRendering.SILHOUETTE_A
        );
    }
}
