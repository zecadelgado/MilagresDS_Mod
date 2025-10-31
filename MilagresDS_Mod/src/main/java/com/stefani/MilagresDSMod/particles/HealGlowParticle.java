package com.stefani.MilagresDSMod.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class HealGlowParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected HealGlowParticle(ClientLevel level, double x, double y, double z,
                               double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.setSize(0.02f, 0.02f);
        this.quadSize = 0.18f + level.random.nextFloat() * 0.06f;
        this.lifetime = 18 + level.random.nextInt(13);
        this.gravity = 0f;
        this.yd = 0.01 + Math.abs(vy) * 0.025;
        this.setSpriteFromAge(sprites);
        this.setColor(0.98f, 0.94f, 0.72f);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        this.yd *= 0.98;
        this.quadSize *= 0.995f + (random.nextFloat() * 0.003f);
        this.alpha = 1.0f - ((float) this.age / this.lifetime);
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
            return new HealGlowParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
