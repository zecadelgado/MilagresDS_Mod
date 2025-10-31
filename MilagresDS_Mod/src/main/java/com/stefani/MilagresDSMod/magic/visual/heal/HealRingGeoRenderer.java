package com.stefani.MilagresDSMod.magic.visual.heal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HealRingGeoRenderer extends GeoEntityRenderer<HealAreaEntity> {
    private static final ResourceLocation RUNE_TEXTURE = new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/heal_cross_soft.png");
    private static final ResourceLocation PULSE_TEXTURE = new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/heal_ring.png");
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    public HealRingGeoRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) {
        super(context, new HealRingModel());
        this.shadowRadius = 0.0f;
        addRenderLayer(new RunePlaneLayer(this));
        addRenderLayer(new PulseLayer(this));
        addRenderLayer(new PillarLayer(this));
    }

    @Override
    public RenderType getRenderType(HealAreaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, HealAreaEntity animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        int fullBright = LightTexture.pack(15, 15);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                fullBright, packedOverlay, red, green, blue, alpha);
    }

    private static class RunePlaneLayer extends GeoRenderLayer<HealAreaEntity> {
        private RunePlaneLayer(GeoEntityRenderer<HealAreaEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, HealAreaEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                           MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
                           int packedOverlay, float red, float green, float blue, float alpha) {
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(RUNE_TEXTURE));
            float rotation = animatable.getRuneRotation(partialTick);
            float bob = animatable.getRuneLift(partialTick);

            poseStack.pushPose();
            poseStack.translate(0, bob, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            drawRuneQuad(poseStack, consumer, packedLight, 1.2f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
            drawRuneQuad(poseStack, consumer, packedLight, 1.2f, 0.5f);
            poseStack.popPose();
        }

        private void drawRuneQuad(PoseStack poseStack, VertexConsumer consumer, int light, float size, float alpha) {
            var entry = poseStack.last();
            var matrix = entry.pose();
            int a = (int) (alpha * 255);
            consumer.vertex(matrix, -size, 0.02f, -size).color(255, 255, 255, a).uv(0, 1).uv2(light).endVertex();
            consumer.vertex(matrix, -size, 0.02f, size).color(255, 255, 255, a).uv(0, 0).uv2(light).endVertex();
            consumer.vertex(matrix, size, 0.02f, size).color(255, 255, 255, a).uv(1, 0).uv2(light).endVertex();
            consumer.vertex(matrix, size, 0.02f, -size).color(255, 255, 255, a).uv(1, 1).uv2(light).endVertex();
        }
    }

    private static class PulseLayer extends GeoRenderLayer<HealAreaEntity> {
        private PulseLayer(GeoEntityRenderer<HealAreaEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, HealAreaEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                           MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
                           int packedOverlay, float red, float green, float blue, float alpha) {
            if (animatable.getActivePulses().isEmpty()) {
                return;
            }
            VertexConsumer ringConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(PULSE_TEXTURE));
            int light = LightTexture.pack(15, 15);
            for (HealAreaEntity.PulseInstance pulse : animatable.getActivePulses()) {
                float radius = pulse.getRadius(partialTick);
                float intensity = pulse.getFade(partialTick);
                if (intensity <= 0.01f) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(0, pulse.getLayerHeight(), 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getClockRotation(partialTick)));
                drawHollowDisc(poseStack, ringConsumer, radius, pulse.getLayerThickness(), light,
                        0.55f * intensity);
                poseStack.popPose();
            }
        }

        private void drawHollowDisc(PoseStack poseStack, VertexConsumer consumer, float radius, float thickness,
                                    int light, float alpha) {
            float inner = Math.max(0.05f, radius - thickness);
            int segments = 48;
            int colour = (int) (alpha * 255);
            var entry = poseStack.last();
            var matrix = entry.pose();
            for (int i = 0; i < segments; i++) {
                float ang0 = (float) (i * (Math.PI * 2.0) / segments);
                float ang1 = (float) ((i + 1) * (Math.PI * 2.0) / segments);
                float cos0 = Mth.cos(ang0);
                float sin0 = Mth.sin(ang0);
                float cos1 = Mth.cos(ang1);
                float sin1 = Mth.sin(ang1);

                consumer.vertex(matrix, inner * cos0, 0, inner * sin0).color(255, 255, 255, colour).uv(0, 1).uv2(light).endVertex();
                consumer.vertex(matrix, radius * cos0, 0, radius * sin0).color(255, 255, 255, colour).uv(1, 1).uv2(light).endVertex();
                consumer.vertex(matrix, radius * cos1, 0, radius * sin1).color(255, 255, 255, colour).uv(1, 0).uv2(light).endVertex();
                consumer.vertex(matrix, inner * cos1, 0, inner * sin1).color(255, 255, 255, colour).uv(0, 0).uv2(light).endVertex();
            }
        }
    }

    private static class PillarLayer extends GeoRenderLayer<HealAreaEntity> {
        private PillarLayer(GeoEntityRenderer<HealAreaEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, HealAreaEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                           MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
                           int packedOverlay, float red, float green, float blue, float alpha) {
            if (animatable.getActivePulses().isEmpty()) {
                return;
            }
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(BEAM_TEXTURE));
            int light = LightTexture.pack(15, 15);
            float orbit = animatable.getClockRotation(partialTick) * 0.5f;
            for (HealAreaEntity.PulseInstance pulse : animatable.getActivePulses()) {
                float radius = pulse.getRadius(partialTick);
                float alphaFade = pulse.getColumnAlpha(partialTick);
                if (alphaFade <= 0.01f) {
                    continue;
                }
                int pillarCount = pulse.getBeamCount();
                for (int i = 0; i < pillarCount; i++) {
                    float rot = orbit + (360f / pillarCount) * i;
                    double x = Math.cos(Math.toRadians(rot)) * radius;
                    double z = Math.sin(Math.toRadians(rot)) * radius;
                    poseStack.pushPose();
                    poseStack.translate(x, 0.05, z);
                    poseStack.mulPose(Axis.YP.rotationDegrees(rot));
                    drawPillar(poseStack, consumer, light, pulse.getBeamHeight(), alphaFade);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                    drawPillar(poseStack, consumer, light, pulse.getBeamHeight(), alphaFade * 0.85f);
                    poseStack.popPose();
                }
            }
        }

        private void drawPillar(PoseStack poseStack, VertexConsumer consumer, int light, float height, float alpha) {
            float halfWidth = 0.12f;
            int colour = (int) (alpha * 255);
            var entry = poseStack.last();
            var matrix = entry.pose();

            consumer.vertex(matrix, -halfWidth, 0, 0).color(255, 236, 180, colour).uv(0, 0).uv2(light).endVertex();
            consumer.vertex(matrix, -halfWidth, height, 0).color(255, 236, 180, 0).uv(0, 1).uv2(light).endVertex();
            consumer.vertex(matrix, halfWidth, height, 0).color(255, 236, 180, 0).uv(1, 1).uv2(light).endVertex();
            consumer.vertex(matrix, halfWidth, 0, 0).color(255, 236, 180, colour).uv(1, 0).uv2(light).endVertex();
        }
    }
}
