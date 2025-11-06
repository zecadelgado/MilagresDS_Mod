package com.stefani.MilagresDSMod.client.particles;

import com.stefani.MilagresDSMod.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Helper central (padrão ISS) para padrões de partículas:
 * - burst radial de impacto
 * - trilha helicoidal em voo
 * - fallback para partículas vanilla se a textura custom estiver ausente
 */
public final class LightningSparkParticle {
    public LightningSparkParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {}

    private static ParticleOptions spark() {
        return ModParticles.LIGHTNING_SPARK != null && ModParticles.LIGHTNING_SPARK.isPresent()
                ? ModParticles.LIGHTNING_SPARK.get()
                : ParticleTypes.ELECTRIC_SPARK;
    }

    public static void impactBurst(ServerLevel level, Vec3 pos, int count) {
        for (int i = 0; i < count; i++) {
            double a = (Math.PI * 2 * i) / count;
            double s = 0.7 + level.random.nextDouble() * 0.6;
            level.sendParticles(spark(), pos.x, pos.y, pos.z, 1,
                    Math.cos(a)*s, 0.15, Math.sin(a)*s, 0.0);
        }
        // pico de luz e flash curto
        level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
    }

    public static void helixTrail(Level level, double x, double y, double z, int tick, int rings, double baseRadius) {
        for (int ring = 0; ring < rings; ring++) {
            double r = baseRadius + ring * 0.08;
            double ang = (tick * 0.25) + ring * 2.0;
            double xo = Math.cos(ang) * r;
            double zo = Math.sin(ang) * r;
            double yo = -0.1 + ring * 0.05;
            level.addParticle(spark(), x+xo, y+yo, z+zo, 0, 0, 0);
            level.addParticle(ParticleTypes.GLOW, x+xo, y+yo, z+zo, 0, 0, 0);
        }
    }
}
