package com.stefani.MilagresDSMod.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

/**
 * Base para todas as partículas que usam sprite sheet translúcido.
 * Garante que NUNCA retornaremos renderType = null.
 */
public abstract class BaseTranslucentParticle extends TextureSheetParticle {
    protected final SpriteSet sprites;

    protected BaseTranslucentParticle(ClientLevel level, double x, double y, double z,
                                      double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
