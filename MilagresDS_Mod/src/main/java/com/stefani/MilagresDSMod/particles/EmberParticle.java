package com.stefani.MilagresDSMod.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class EmberParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected EmberParticle(ClientLevel level, double x, double y, double z,
                            double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.setSize(0.02f, 0.02f);
        this.quadSize = 0.14f + level.random.nextFloat() * 0.08f;
        this.lifetime = 16 + level.random.nextInt(12);
        this.gravity = 0.01f;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setSpriteFromAge(sprites);
        this.setColor(1.0f, 0.52f, 0.20f);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        this.xd += (random.nextDouble() - 0.5) * 0.002;
        this.zd += (random.nextDouble() - 0.5) * 0.002;
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
                return new EmberParticle(level, x, y, z, vx, vy, vz, sprites);
            }
        }
}
