package com.stefani.MilagresDSMod.magic.visual.lightning;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LightningSpearGeoModel extends GeoModel<LightningSpearEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(MilagresDSMod.MODID, "geo/LightningSpearModel.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/lightning_spear.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(MilagresDSMod.MODID, "animations/LightningSpear.anim.json");

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
}
