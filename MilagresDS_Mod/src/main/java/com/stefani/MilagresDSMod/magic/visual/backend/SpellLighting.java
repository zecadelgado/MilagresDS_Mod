package com.stefani.MilagresDSMod.magic.visual.backend;

import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

public final class SpellLighting {
    private SpellLighting() {}

    public static void emitTravelLight(ServerLevel level, Entity entity, int rgb, float radius, int durationMs) {
        if (radius <= 0f) {
            return;
        }
        MinecraftForge.EVENT_BUS.post(new SpellDynamicLightEvent(level, entity, rgb, radius, durationMs));
        if (entity instanceof FlameSlingEntity flame) {
            int glow = Math.max(4, durationMs / 50);
            flame.pulseGlow(glow);
        }
    }
}
