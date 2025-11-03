package com.stefani.MilagresDSMod.client.magic.visual.lightning;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class LightningSpearGeoRenderer extends GeoEntityRenderer<LightningSpearEntity> {
    public LightningSpearGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LightningSpearGeoModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.0f;
    }

    @Override
    public RenderType getRenderType(LightningSpearEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void render(LightningSpearEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.02f, 1.02f, 1.02f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
