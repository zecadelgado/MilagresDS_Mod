package com.stefani.MilagresDSMod.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class LightningSparkParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected LightningSparkParticle(ClientLevel level, double x, double y, double z,
                                     double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        // Increase the size of each spark and extend its lifetime to create a more
        // substantial lightning effect reminiscent of the heavy arcs seen in other
        // lightning spear mods.  Larger particles and longer lifetimes give the
        // bolt a fuller, more persistent trail.
        this.setSize(0.03f, 0.03f);
        this.quadSize = 0.25f + level.random.nextFloat() * 0.10f;
        this.lifetime = 10 + level.random.nextInt(10);
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
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {

        @Override
            public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                           double x, double y, double z, double vx, double vy, double vz) {
                return new LightningSparkParticle(level, x, y, z, vx, vy, vz, sprites);
            }
        }
}
