package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.client.lighting.DynamicLightClient;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingRenderer;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearRenderer;
import com.stefani.MilagresDSMod.particles.EmberParticle;
import com.stefani.MilagresDSMod.particles.HealGlowParticle;
import com.stefani.MilagresDSMod.particles.LightningSparkParticle;
import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import com.stefani.MilagresDSMod.registry.RendererRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ClientSetup {
    private ClientSetup() {}

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::onRegisterRenderers);
        modBus.addListener(ClientSetup::onRegisterParticles);
        modBus.addListener(ClientSetup::onRegisterLayers);
        DynamicLightClient.init();
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        RendererRegistry.registerEntityRenderers(evt);
    }

    private static void onRegisterParticles(RegisterParticleProvidersEvent evt) {
        evt.registerSpriteSet(ParticleRegistry.LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
        evt.registerSpriteSet(ParticleRegistry.EMBER.get(), EmberParticle.Provider::new);
        evt.registerSpriteSet(ParticleRegistry.HEAL_GLOW.get(), HealGlowParticle.Provider::new);
    }

    private static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions evt) {
        evt.registerLayerDefinition(LightningSpearRenderer.LAYER, LightningSpearRenderer::createLayer);
        evt.registerLayerDefinition(FlameSlingRenderer.LAYER, FlameSlingRenderer::createLayer);
        // HealAreaRenderer não precisa de layer definition
    }
}
