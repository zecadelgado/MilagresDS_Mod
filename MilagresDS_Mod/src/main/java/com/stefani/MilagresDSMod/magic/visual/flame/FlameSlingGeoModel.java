package com.stefani.MilagresDSMod.magic.visual.flame;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FlameSlingGeoModel extends GeoModel<FlameSlingEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "geo/FlameSlingModel.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_base.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "animations/FlameSling.anim.json");

    @Override
    public ResourceLocation getModelResource(FlameSlingEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FlameSlingEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FlameSlingEntity animatable) {
        return ANIMATION;
    }
}
