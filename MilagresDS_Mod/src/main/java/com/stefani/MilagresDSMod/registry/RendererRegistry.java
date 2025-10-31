package com.stefani.MilagresDSMod.registry;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingRenderer;
import com.stefani.MilagresDSMod.magic.visual.heal.HealAreaRenderer;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearRenderer;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.ModList;

public final class RendererRegistry {
    private RendererRegistry() {}

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        if (ModList.get().isLoaded("geckolib")) {
            tryRegisterGeckoRenderers(evt);
        } else {
            registerVanillaRenderers(evt);
        }
    }

    private static void registerVanillaRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerEntityRenderer(EntityRegistry.LIGHTNING_SPEAR.get(), LightningSpearRenderer::new);
        evt.registerEntityRenderer(EntityRegistry.FLAME_SLING.get(),   FlameSlingRenderer::new);
        evt.registerEntityRenderer(EntityRegistry.HEAL_AREA.get(),     HealAreaRenderer::new);
    }

    private static void tryRegisterGeckoRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        try {
            Class<?> bootstrap = Class.forName("com.stefani.MilagresDSMod.magic.visual.backend.gecko.GeckoBackend");
            var method = bootstrap.getMethod("registerRenderers", EntityRenderersEvent.RegisterRenderers.class);
            Object result = method.invoke(null, evt);
            if (result instanceof Boolean bool && bool) {
                return;
            }
        } catch (ClassNotFoundException e) {
            registerVanillaRenderers(evt);
            return;
        } catch (ReflectiveOperationException e) {
            MilagresDSMod.LOGGER.debug("Gecko renderer bootstrap unavailable, using vanilla visuals", e);
        }
        registerVanillaRenderers(evt);
    }
}
