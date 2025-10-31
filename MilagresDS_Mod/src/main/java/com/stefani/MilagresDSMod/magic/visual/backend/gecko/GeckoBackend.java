package com.stefani.MilagresDSMod.magic.visual.backend.gecko;

import com.stefani.MilagresDSMod.magic.visual.heal.HealRingGeoRenderer;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.ModList;

public final class GeckoBackend {
    private static final boolean ENABLED = ModList.get().isLoaded("geckolib");

    private GeckoBackend() {}

    public static boolean registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!ENABLED) {
            return false;
        }
        event.registerEntityRenderer(EntityRegistry.HEAL_AREA.get(), HealRingGeoRenderer::new);
        return true;
    }
}
