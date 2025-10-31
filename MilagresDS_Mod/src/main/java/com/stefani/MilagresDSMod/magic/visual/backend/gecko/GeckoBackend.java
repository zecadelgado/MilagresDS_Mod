package com.stefani.MilagresDSMod.magic.visual.backend.gecko;

import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingRenderer;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;

public final class GeckoBackend {
    private GeckoBackend() {}

    public static boolean registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.FLAME_SLING.get(), FlameSlingRenderer::new);
        return true;
    }
}
