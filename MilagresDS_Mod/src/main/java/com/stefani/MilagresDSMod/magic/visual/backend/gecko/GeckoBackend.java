package com.stefani.MilagresDSMod.magic.visual.backend.gecko;

import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingRenderer;
import com.stefani.MilagresDSMod.magic.visual.heal.HealAreaRenderer;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearGeoRenderer;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;

public final class GeckoBackend {
    private GeckoBackend() {}

    public static boolean registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.LIGHTNING_SPEAR.get(), LightningSpearGeoRenderer::new);
        event.registerEntityRenderer(EntityRegistry.FLAME_SLING.get(), FlameSlingRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HEAL_AREA.get(), HealAreaRenderer::new);
        return true;
    }
}
