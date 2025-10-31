package com.stefani.MilagresDSMod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stefani.MilagresDSMod.entity.LightningSpearEntity;
import com.stefani.MilagresDSMod.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LightningSpearRenderer extends EntityRenderer<LightningSpearEntity> {
    private final ItemRenderer itemRenderer;
    private final ItemStack renderStack;

    public LightningSpearRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.renderStack = new ItemStack(ModItems.LIGHTNING_SPEAR_ITEM.get());
    }

    @Override
    public void render(LightningSpearEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F;
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F;

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        poseStack.scale(1.0F, 1.0F, 1.0F);

        itemRenderer.renderStatic(renderStack, ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(LightningSpearEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
