package com.stefani.MilagresDSMod.client.magic.visual.heal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Simple wrapper around {@link HealRingGeoRenderer} used to register the heal ring
 * renderer with the Forge rendering system.  Although {@code HealRingGeoRenderer}
 * contains all of the rendering logic, Forge expects a concrete subclass to
 * instantiate via the registration lambda in {@link com.stefani.MilagresDSMod.client.magic.visual.backend.gecko.GeckoBackend}.
 */
public class HealRingRenderer extends HealRingGeoRenderer {
    public HealRingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}