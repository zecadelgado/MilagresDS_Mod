package com.stefani.MilagresDSMod.registry;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MilagresDSMod.MODID);

    public static final RegistryObject<SimpleParticleType> LIGHTNING_SPARK =
            REGISTRY.register("lightning_spark", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> EMBER =
            REGISTRY.register("ember", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> HEAL_GLOW =
            REGISTRY.register("heal_glow", () -> new SimpleParticleType(false));

    private ParticleRegistry() {}
}
