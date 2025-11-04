package com.stefani.MilagresDSMod.magic.visual.heal;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HealRingModel extends GeoModel<HealRingEntity> {
    private static final ResourceLocation MODEL = rl("geo/heal_ring.geo.json");
    private static final ResourceLocation TEXTURE = rl("textures/entity/heal_ring.png");
    private static final ResourceLocation ANIMATION = rl("animations/heal_ring.animation.json");

    @Override
    public ResourceLocation getModelResource(HealRingEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HealRingEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HealRingEntity animatable) {
        return ANIMATION;
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(MilagresDSMod.MODID, path);
    }
}
