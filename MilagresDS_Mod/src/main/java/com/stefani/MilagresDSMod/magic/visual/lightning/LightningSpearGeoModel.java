package com.stefani.MilagresDSMod.magic.visual.lightning;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LightningSpearGeoModel extends GeoModel<LightningSpearEntity> {
    private static final ResourceLocation MODEL = rl("geo/lightning_spear.geo.json");
    private static final ResourceLocation TEXTURE = rl("textures/entity/lightning_spear.png");
    private static final ResourceLocation ANIMATION = rl("animations/lightning_spear.animation.json");

    @Override
    public ResourceLocation getModelResource(LightningSpearEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LightningSpearEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LightningSpearEntity animatable) {
        return ANIMATION;
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(MilagresDSMod.MODID, path);
    }
}
