package com.github.shap_po.essencelib.render;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.power.type.ModifyPlayerModelPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArmadilloShellFeatureRenderer
    extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final Identifier DEFAULT_TEXTURE =
        EssenceLib.identifier("textures/entity/armadillo_shell_hologram.png");
    private static final Identifier ARMADILLO_BALL_MODEL_ID = EssenceLib.identifier("armadillo_ball");
    private static final ModelPart BALL_MODEL = createBallModel();

    public ArmadilloShellFeatureRenderer(
        FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context
    ) {
        super(context);
    }

    @Override
    public void render(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        AbstractClientPlayerEntity player,
        float limbAngle,
        float limbDistance,
        float tickDelta,
        float animationProgress,
        float headYaw,
        float headPitch
    ) {
        if (!isBallUpVisualActive(player)) {
            return;
        }
        renderBallModel(matrices, vertexConsumers, light, player, tickDelta);
    }

    public static boolean isBallUpVisualActive(AbstractClientPlayerEntity player) {
        return PowerHolderComponent.KEY.get(player).getPowerTypes(ModifyPlayerModelPowerType.class).stream()
            .anyMatch(p -> p.getModel().equals(ARMADILLO_BALL_MODEL_ID) && p.isActive());
    }

    public static void renderBallModel(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        AbstractClientPlayerEntity player,
        float tickDelta
    ) {
        matrices.push();
        // Center over player and match scaled hitbox (player at 0.5 scale = 1 block tall)
        matrices.translate(0.0f, 0.5f, 0.0f);
        matrices.scale(0.5f, 0.5f, 0.5f);

        // LAYER 1: Solid inner shell with real armadillo texture (no transparency)
        VertexConsumer solidConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(DEFAULT_TEXTURE));
        int solidTint = 0xFFFFFFFF; // Full opacity, white (shows texture as-is)
        
        for (ModelPart part : BALL_MODEL.traverse().toList()) {
            part.render(matrices, solidConsumer, light, OverlayTexture.DEFAULT_UV, solidTint);
        }

        // LAYER 2: Outer pulsing forcefield (1-2 pixels bigger, blue hologram)
        matrices.push();
        float outerScale = 1.05f; // 5% bigger = ~1-2 pixels at this scale
        matrices.scale(outerScale, outerScale, outerScale);
        
        // Pulsing effect for the forcefield
        float pulse = (float) ((Math.sin((player.age + tickDelta) * 0.2f) + 1.0) * 0.5);
        int alpha = 100 + (int) (pulse * 100.0f); // Pulse between 100-200 alpha
        int red = 100;
        int green = 200;
        int blue = 255;
        int forceFieldTint = (alpha << 24) | (red << 16) | (green << 8) | blue;
        
        VertexConsumer forceFieldConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(DEFAULT_TEXTURE));
        
        for (ModelPart part : BALL_MODEL.traverse().toList()) {
            part.render(matrices, forceFieldConsumer, light, OverlayTexture.DEFAULT_UV, forceFieldTint);
        }
        
        matrices.pop();
        matrices.pop();
    }

    private static ModelPart createBallModel() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        
        // Create a solid shell with multiple layers for better appearance
        // Inner core
        root.addChild(
            "core",
            ModelPartBuilder.create()
            .uv(0, 0)
            .cuboid(-7.0f, -7.0f, -7.0f, 14.0f, 14.0f, 14.0f, new Dilation(0.0f)),
            ModelTransform.NONE
        );
        
        // Outer shell layer for depth
        root.addChild(
            "shell",
            ModelPartBuilder.create()
            .uv(0, 28)
            .cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f, new Dilation(0.0f)),
            ModelTransform.NONE
        );
        
        TexturedModelData modelData = TexturedModelData.of(data, 64, 64);
        ModelPart model = modelData.createModel();
        return model;
    }
}
