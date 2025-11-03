package com.stefani.MilagresDSMod.magic.visual.flame;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FlameSlingGeoModel extends GeoModel<FlameSlingEntity> {
    @Override
    public ResourceLocation getModelResource(FlameSlingEntity animatable) {
        return new ResourceLocation(MilagresDSMod.MODID, "geo/flame_sling_model.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FlameSlingEntity animatable) {
        return new ResourceLocation(MilagresDSMod.MODID, "textures/entity/spells/flame_sling_base.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FlameSlingEntity animatable) {
        return new ResourceLocation(MilagresDSMod.MODID, "animations/flame_sling.anim.json");
    }
}
