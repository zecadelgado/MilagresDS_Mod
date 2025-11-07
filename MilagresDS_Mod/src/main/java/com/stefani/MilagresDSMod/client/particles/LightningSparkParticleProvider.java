package com.stefani.MilagresDSMod.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class LightningSparkParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;
    public LightningSparkParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z, double dx, double dy, double dz) {
        return new LightningSparkBillboardParticle(level, x, y, z, dx, dy, dz, this.sprites);
    }
}
