package com.github.shap_po.essencelib.block;

import com.github.shap_po.essencelib.block.entity.DownedCorpseBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DownedCorpseBlockRenderer extends GeoBlockRenderer<DownedCorpseBlockEntity> {
    private ArmorStandEntity armorPreview;

    public DownedCorpseBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new DownedCorpseBlockModel());
    }

    @Override
    public void render(DownedCorpseBlockEntity animatable, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        renderLootLinkedArmor(animatable, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderLootLinkedArmor(DownedCorpseBlockEntity corpse, float tickDelta, MatrixStack matrices,
                                       VertexConsumerProvider consumers, int packedLight) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return;
        }
        if (armorPreview == null || armorPreview.getWorld() != client.world) {
            armorPreview = new ArmorStandEntity(client.world, 0.0, 0.0, 0.0);
            armorPreview.setInvisible(true);
            armorPreview.setNoGravity(true);
            armorPreview.setShowArms(true);
            armorPreview.setHideBasePlate(true);
            armorPreview.setHeadYaw(0.0f);
            armorPreview.setBodyYaw(0.0f);
        }

        armorPreview.equipStack(EquipmentSlot.FEET, safeCopy(corpse.getStack(36)));
        armorPreview.equipStack(EquipmentSlot.LEGS, safeCopy(corpse.getStack(37)));
        armorPreview.equipStack(EquipmentSlot.CHEST, safeCopy(corpse.getStack(38)));
        armorPreview.equipStack(EquipmentSlot.HEAD, safeCopy(corpse.getStack(39)));

        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        matrices.push();
        matrices.translate(0.5, 0.02, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f));
        matrices.scale(1.0f, 1.0f, 1.0f);
        dispatcher.setRenderShadows(false);
        dispatcher.render(armorPreview, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, consumers, packedLight);
        dispatcher.setRenderShadows(true);
        matrices.pop();

        if (!corpse.getStack(40).isEmpty()) {
            matrices.push();
            matrices.translate(0.66, 0.38, 0.42);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-28.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(80.0f));
            matrices.scale(0.55f, 0.55f, 0.55f);
            client.getItemRenderer().renderItem(
                corpse.getStack(40),
                ModelTransformationMode.GROUND,
                packedLight,
                OverlayTexture.DEFAULT_UV,
                matrices,
                consumers,
                client.world,
                0
            );
            matrices.pop();
        }
    }

    private static ItemStack safeCopy(ItemStack source) {
        return source == null ? ItemStack.EMPTY : source.copy();
    }
}
