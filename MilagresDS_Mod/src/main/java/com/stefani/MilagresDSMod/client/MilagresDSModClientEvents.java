package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.keybindings.ModKeyBindings;
import com.stefani.MilagresDSMod.client.lighting.DynamicLightClient;
import com.stefani.MilagresDSMod.client.magic.visual.flame.FlameSlingFallbackRenderer;
import com.stefani.MilagresDSMod.client.particles.EmberParticle;
import com.stefani.MilagresDSMod.client.particles.HealGlowParticle;
import com.stefani.MilagresDSMod.client.particles.LightningSparkParticleProvider;
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
    private MilagresDSModClientEvents() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            DynamicLightClient.init();

            net.minecraft.client.renderer.entity.EntityRenderers.register(
                    com.stefani.MilagresDSMod.registry.ModEntities.LIGHTNING_SPEAR.get(),
                    com.stefani.MilagresDSMod.client.magic.visual.lightning.LightningSpearGeoRenderer::new);

            net.minecraft.client.renderer.entity.EntityRenderers.register(
                    com.stefani.MilagresDSMod.registry.ModEntities.FLAME_SLING.get(),
                    com.stefani.MilagresDSMod.client.magic.visual.flame.FlameSlingRenderer::new);

            net.minecraft.client.renderer.entity.EntityRenderers.register(
                    com.stefani.MilagresDSMod.registry.ModEntities.HEAL_RING.get(),
                    com.stefani.MilagresDSMod.client.magic.visual.heal.HealRingRenderer::new);
        });
    }

    /** ÚNICO ponto de registro dos providers de partículas. */
    @SubscribeEvent
    public static void onRegisterParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LIGHTNING_SPARK.get(), LightningSparkParticleProvider::new);
        event.registerSpriteSet(ModParticles.EMBER.get(), EmberParticle.Provider::new);
        event.registerSpriteSet(ModParticles.HEAL_GLOW.get(), HealGlowParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FlameSlingFallbackRenderer.LAYER, FlameSlingFallbackRenderer::createLayer);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.OPEN_SPELL_MENU);
        event.register(ModKeyBindings.CAST_SPELL);
        event.register(ModKeyBindings.OPEN_ATTRIBUTES);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        com.stefani.MilagresDSMod.client.magic.visual.backend.gecko.GeckoBackend.registerRenderers(event);
    }
}
