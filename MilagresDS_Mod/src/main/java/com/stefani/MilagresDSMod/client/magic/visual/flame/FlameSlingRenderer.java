package com.stefani.MilagresDSMod.client.magic.visual.flame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class FlameSlingRenderer extends GeoEntityRenderer<FlameSlingEntity> {
    private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_base.png");
    private static final ResourceLocation[] FLAME_SCROLL = new ResourceLocation[] {
            ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_fire_0.png"),
            ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_fire_1.png"),
            ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_fire_2.png"),
            ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_fire_3.png")
    };

    public FlameSlingRenderer(EntityRendererProvider.Context context) {
        super(context, new FlameSlingGeoModel());
        this.shadowRadius = 0.18f;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        addRenderLayer(new FlameTongueLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FlameSlingEntity animatable) {
        return BASE;
    }

    @Override
    public void render(FlameSlingEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float jiggle = 1.0f + (float) Math.sin((entity.tickCount + partialTick) * 0.35f) * 0.02f;
        poseStack.scale(jiggle, jiggle, jiggle);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static class FlameTongueLayer extends GeoRenderLayer<FlameSlingEntity> {
        private FlameTongueLayer(FlameSlingRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, FlameSlingEntity entity, BakedGeoModel bakedModel, RenderType renderType,
                           MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            float time = entity.tickCount + partialTick;
            int frame = (entity.tickCount / 2) % FLAME_SCROLL.length;
            ResourceLocation scrollTex = FLAME_SCROLL[frame];
            float uOffset = time * 0.01f;
            float vOffset = time * 0.017f;
            RenderType swirl = RenderType.energySwirl(scrollTex, uOffset, vOffset);
            getRenderer().reRender(bakedModel, poseStack, bufferSource, entity, swirl, bufferSource.getBuffer(swirl), partialTick,
                    packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 0.85f);
        }
    }
}
