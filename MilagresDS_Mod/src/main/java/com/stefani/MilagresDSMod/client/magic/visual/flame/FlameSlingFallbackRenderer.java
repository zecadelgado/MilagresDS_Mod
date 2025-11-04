package com.stefani.MilagresDSMod.client.magic.visual.flame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FlameSlingFallbackRenderer extends EntityRenderer<FlameSlingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "flame_sling"), "main");
    private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/flame_sling.png");
    private static final ResourceLocation OVER0 = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/flame_sling_fire_0.png");
    private static final ResourceLocation OVER1 = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/flame_sling_fire_1.png");
    private static final ResourceLocation OVER2 = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/flame_sling_fire_2.png");
    private static final ResourceLocation OVER3 = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/flame_sling_fire_3.png");

    private final ModelPart body;

    public FlameSlingFallbackRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.body = ctx.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-4, -4, -4, 8, 8, 8), PartPose.ZERO);
        for (int i = 0; i < 6; i++) {
            float rx = (i % 3 - 1) * 0.174533f; // 10°
            float ry = (i / 3 - 0.5f) * 0.209439f; // 12°
            root.addOrReplaceChild("ring" + i, CubeListBuilder.create().texOffs(0, 16).addBox(-4, -1, -4, 8, 2, 8),
                    PartPose.rotation(rx, ry, 0));
        }
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void render(FlameSlingEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        float jiggle = 1.0f + (float) Math.sin((entity.tickCount + partialTick) * 0.35f) * 0.02f;
        poseStack.scale(jiggle, jiggle, jiggle);

        VertexConsumer base = buffer.getBuffer(RenderType.entityCutoutNoCull(BASE));
        body.render(poseStack, base, light, OverlayTexture.NO_OVERLAY);

        ResourceLocation overlayTex = switch ((entity.tickCount / 2) % 4) {
            case 0 -> OVER0;
            case 1 -> OVER1;
            case 2 -> OVER2;
            default -> OVER3;
        };
        VertexConsumer overlay = buffer.getBuffer(RenderType.energySwirl(overlayTex, (entity.tickCount + partialTick) * 0.01f, 0));
        body.render(poseStack, overlay, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(FlameSlingEntity entity) {
        return BASE;
    }
}
