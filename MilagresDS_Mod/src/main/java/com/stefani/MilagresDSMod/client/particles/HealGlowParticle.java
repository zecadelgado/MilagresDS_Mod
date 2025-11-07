package com.stefani.MilagresDSMod.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class HealGlowParticle extends BaseTranslucentParticle {

    protected HealGlowParticle(ClientLevel level, double x, double y, double z,
                               double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz, sprites);
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
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {

        @Override
            public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                           double x, double y, double z, double vx, double vy, double vz) {
                return new HealGlowParticle(level, x, y, z, vx, vy, vz, sprites);
            }
        }
}
