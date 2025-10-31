package com.stefani.MilagresDSMod.magic.visual.lightning;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LightningSpearRenderer extends EntityRenderer<LightningSpearEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(MilagresDSMod.MODID, "lightning_spear"), "main");
    private static final ResourceLocation TEX = new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/lightning_spear.png");

    private final ModelPart body;

    public LightningSpearRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.body = ctx.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.5f, -5f, 1f, 1f, 10f), PartPose.ZERO);
        for (int i = 0; i < 4; i++) {
            float rot = i * 0.3926991f; // 22.5° em radianos
            root.addOrReplaceChild("seg" + i, CubeListBuilder.create().texOffs(0, 12).addBox(-0.4f, -0.4f, -3f, 0.8f, 0.8f, 6f),
                    PartPose.rotation(0f, rot, 0.15f * i));
        }
        root.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(0, 22).addBox(-0.35f, -0.35f, -7f, 0.7f, 0.7f, 2f), PartPose.ZERO);
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void render(LightningSpearEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        VertexConsumer base = buffer.getBuffer(RenderType.entityTranslucent(TEX));
        body.render(poseStack, base, light, OverlayTexture.NO_OVERLAY);

        VertexConsumer swirl = buffer.getBuffer(RenderType.energySwirl(TEX, (entity.tickCount + partialTick) * 0.01f, 0));
        poseStack.scale(1.05f, 1.05f, 1.05f);
        body.render(poseStack, swirl, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(LightningSpearEntity entity) {
        return TEX;
    }
}
