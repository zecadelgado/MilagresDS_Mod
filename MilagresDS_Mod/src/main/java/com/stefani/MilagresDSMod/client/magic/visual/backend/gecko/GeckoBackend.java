package com.stefani.MilagresDSMod.client.magic.visual.backend.gecko;

import com.stefani.MilagresDSMod.client.magic.visual.flame.FlameSlingRenderer;
import com.stefani.MilagresDSMod.client.magic.visual.heal.HealRingRenderer;
import com.stefani.MilagresDSMod.client.magic.visual.lightning.LightningSpearGeoRenderer;
import com.stefani.MilagresDSMod.registry.ModEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;

public final class GeckoBackend {
    private GeckoBackend() {}

    public static boolean registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LIGHTNING_SPEAR.get(), LightningSpearGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.FLAME_SLING.get(), FlameSlingRenderer::new);
        event.registerEntityRenderer(ModEntities.HEAL_RING.get(), HealRingRenderer::new);
        return true;
    }
}

