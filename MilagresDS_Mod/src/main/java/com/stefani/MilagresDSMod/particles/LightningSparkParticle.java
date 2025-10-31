package com.stefani.MilagresDSMod.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class LightningSparkParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected LightningSparkParticle(ClientLevel level, double x, double y, double z,
                                     double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.setSize(0.02f, 0.02f);
        this.quadSize = 0.18f + level.random.nextFloat() * 0.08f;
        this.lifetime = 8 + level.random.nextInt(7);
        this.gravity = 0f;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setSpriteFromAge(sprites);
        this.setColor(0.97f, 0.89f, 0.48f);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        float t = (float) this.age / this.lifetime;
        float flicker = 0.85f + 0.15f * Mth.sin((this.age + this.random.nextFloat()) * 0.9f);
        this.alpha = (1.0f - t) * flicker;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<net.minecraft.core.particles.SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(net.minecraft.core.particles.SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double vx, double vy, double vz) {
            return new LightningSparkParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
