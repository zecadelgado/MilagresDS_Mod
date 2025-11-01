package com.stefani.MilagresDSMod.magic.visual.heal;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HealRingModel extends GeoModel<HealAreaEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "geo/HealRingModel.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/entity/spells/heal_ring.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "animations/HealRing.anim.json");

    @Override
    public ResourceLocation getModelResource(HealAreaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HealAreaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HealAreaEntity animatable) {
        return ANIMATION;
    }
}
