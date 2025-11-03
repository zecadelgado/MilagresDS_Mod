package com.stefani.MilagresDSMod.magic.visual.lightning;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LightningSpearGeoModel extends GeoModel<LightningSpearEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "geo/lightning_spear_model.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/lightning_spear.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "animations/lightning_spear.anim.json");

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
