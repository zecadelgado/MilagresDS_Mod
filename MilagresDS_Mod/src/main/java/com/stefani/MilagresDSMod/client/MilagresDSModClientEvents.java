package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.keybindings.ModKeyBindings;
import com.stefani.MilagresDSMod.client.lighting.DynamicLightClient;
import com.stefani.MilagresDSMod.client.magic.visual.flame.FlameSlingFallbackRenderer;
import com.stefani.MilagresDSMod.client.magic.visual.lightning.LightningSpearVanillaRenderer;
import com.stefani.MilagresDSMod.client.particles.EmberParticle;
import com.stefani.MilagresDSMod.client.particles.HealGlowParticle;
import com.stefani.MilagresDSMod.client.particles.LightningSparkParticle;
import com.stefani.MilagresDSMod.registry.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MilagresDSModClientEvents {
    private MilagresDSModClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(DynamicLightClient::init);
    }

    @SubscribeEvent
    public static void onRegisterParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
        event.registerSpriteSet(ModParticles.EMBER.get(), EmberParticle.Provider::new);
        event.registerSpriteSet(ModParticles.HEAL_GLOW.get(), HealGlowParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LightningSpearVanillaRenderer.LAYER, LightningSpearVanillaRenderer::createLayer);
        event.registerLayerDefinition(FlameSlingFallbackRenderer.LAYER, FlameSlingFallbackRenderer::createLayer);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.OPEN_SPELL_MENU);
        event.register(ModKeyBindings.CAST_SPELL);
        event.register(ModKeyBindings.OPEN_ATTRIBUTES);
    }
}
