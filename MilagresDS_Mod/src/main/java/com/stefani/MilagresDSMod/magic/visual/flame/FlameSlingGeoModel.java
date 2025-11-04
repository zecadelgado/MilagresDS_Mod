package com.stefani.MilagresDSMod.magic.visual.flame;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FlameSlingGeoModel extends GeoModel<FlameSlingEntity> {
    @Override
    public ResourceLocation getModelResource(FlameSlingEntity animatable) {
        return rl("geo/flame_sling.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FlameSlingEntity animatable) {
        return rl("textures/entity/flame_sling.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FlameSlingEntity animatable) {
        return rl("animations/flame_sling.animation.json");
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(MilagresDSMod.MODID, path);
    }
}
