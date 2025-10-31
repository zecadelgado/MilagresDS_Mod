package com.stefani.MilagresDSMod.magic.visual.heal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class HealAreaRenderer extends EntityRenderer<HealAreaEntity> {
    private static final ResourceLocation RING = new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/heal_ring.png");
    private static final ResourceLocation CROSS = new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/heal_cross_soft.png");

    public HealAreaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(HealAreaEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        float baseRadius = entity.getAmbientRadius(partialTick);
        float alpha = 0.55f;

        VertexConsumer ringConsumer = buffer.getBuffer(RenderType.entityTranslucent(RING));
        poseStack.translate(0, 0.01, 0);
        renderRingQuad(poseStack, ringConsumer, baseRadius, alpha, light);

        for (HealAreaEntity.PulseInstance pulse : entity.getActivePulses()) {
            float radius = pulse.getRadius(partialTick);
            float localAlpha = 0.45f * pulse.getFade(partialTick);
            if (localAlpha <= 0.01f) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(0, pulse.getLayerHeight(), 0);
            renderRingQuad(poseStack, ringConsumer, radius, localAlpha, light);
            poseStack.popPose();
        }

        VertexConsumer crossConsumer = buffer.getBuffer(RenderType.entityTranslucent(CROSS));
        float t = entity.tickCount + partialTick;
        for (int i = 0; i < 2; i++) {
            float ang = (i * 0.5f + t * 0.03f) * ((float) Math.PI * 2);
            float r = 1.2f + (i * 1.2f);
            double x = Math.cos(ang) * r;
            double z = Math.sin(ang) * r;
            poseStack.pushPose();
            poseStack.translate(x, 0.2 + 0.1 * Math.sin(t * 0.2f + i), z);
            float scale = 0.7f + 0.2f * (float) Math.sin(t * 0.4f + i);
            poseStack.scale(scale, scale, scale);
            billboard(poseStack);
            quadSprite(poseStack, crossConsumer, 0.85f, light);
            poseStack.popPose();
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, light);
    }

    private static void renderRingQuad(PoseStack poseStack, VertexConsumer consumer, float radius, float alpha, int light) {
        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        int a = (int) (255 * alpha);
        consumer.vertex(matrix, -radius, 0, -radius).color(255, 255, 255, a).uv(0, 1).uv2(light).endVertex();
        consumer.vertex(matrix, -radius, 0, radius).color(255, 255, 255, a).uv(0, 0).uv2(light).endVertex();
        consumer.vertex(matrix, radius, 0, radius).color(255, 255, 255, a).uv(1, 0).uv2(light).endVertex();
        consumer.vertex(matrix, radius, 0, -radius).color(255, 255, 255, a).uv(1, 1).uv2(light).endVertex();
        poseStack.popPose();
    }

    private static void billboard(PoseStack poseStack) {
        var cameraRotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        poseStack.mulPose(cameraRotation);
    }

    private static void quadSprite(PoseStack poseStack, VertexConsumer consumer, float alpha, int light) {
        Matrix4f matrix = poseStack.last().pose();
        float size = 0.6f;
        int a = (int) (255 * alpha);
        consumer.vertex(matrix, -size, -size, 0).color(255, 255, 255, a).uv(0, 1).uv2(light).endVertex();
        consumer.vertex(matrix, -size, size, 0).color(255, 255, 255, a).uv(0, 0).uv2(light).endVertex();
        consumer.vertex(matrix, size, size, 0).color(255, 255, 255, a).uv(1, 0).uv2(light).endVertex();
        consumer.vertex(matrix, size, -size, 0).color(255, 255, 255, a).uv(1, 1).uv2(light).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(HealAreaEntity entity) {
        return RING;
    }
}
