package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.keybindings.ModKeyBindings;
import com.stefani.MilagresDSMod.client.lighting.DynamicLightClient;
import com.stefani.MilagresDSMod.client.magic.visual.flame.FlameSlingFallbackRenderer;
import com.stefani.MilagresDSMod.client.particles.EmberParticle;
import com.stefani.MilagresDSMod.client.particles.HealGlowParticle;
import com.stefani.MilagresDSMod.client.particles.LightningSparkParticle;
import com.stefani.MilagresDSMod.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MilagresDSModClientEvents {
    private MilagresDSModClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Register dynamic lighting and schedule our entity renderer registrations.
        event.enqueueWork(() -> {
            // Initialise dynamic lighting module
            DynamicLightClient.init();
            // Register entity renderers via the static EntityRenderers registry.  This ensures
            // that our entities always have a renderer, even if the RegisterRenderers event
            // fails to fire for some reason.
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

    @SubscribeEvent
    public static void onRegisterParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LIGHTNING_SPARK.get(), LightningSparkParticle.Provider::new);
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

    /**
     * Registers entity renderers for all custom entities used by the mod.  This callback hooks
     * the {@link EntityRenderersEvent.RegisterRenderers} fired on the mod bus and delegates
     * to Gecko backend registration.  Without this, some entities would lack a renderer and
     * cause a {@link java.lang.NullPointerException} inside {@code EntityRenderDispatcher.shouldRender}.
     *
     * @param event the renderers registration event
     */
    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // Delegate to Gecko backend to register renderers for all mod entities
        com.stefani.MilagresDSMod.client.magic.visual.backend.gecko.GeckoBackend.registerRenderers(event);
    }

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        // garante providers das partículas custom
        if (ModParticles.LIGHTNING_SPARK != null && ModParticles.LIGHTNING_SPARK.isPresent()) {
            event.registerSpriteSet(ModParticles.LIGHTNING_SPARK.get(), (sprites) ->
                    new ParticleProvider<SimpleParticleType>() {
                        @Override
                        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
                            return new Particle(level, x, y, z, dx, dy, dz, sprites);
                        }
                    });
        }
    }
}
