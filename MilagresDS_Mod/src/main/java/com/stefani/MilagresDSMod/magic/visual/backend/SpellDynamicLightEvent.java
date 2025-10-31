package com.stefani.MilagresDSMod.magic.visual.backend;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

public class SpellDynamicLightEvent extends Event {
    private final ServerLevel level;
    private final Entity entity;
    private final int rgb;
    private final float radius;
    private final int durationMs;

    public SpellDynamicLightEvent(ServerLevel level, Entity entity, int rgb, float radius, int durationMs) {
        this.level = level;
        this.entity = entity;
        this.rgb = rgb;
        this.radius = radius;
        this.durationMs = durationMs;
    }

    public ServerLevel level() {
        return level;
    }

    public Entity entity() {
        return entity;
    }

    public int rgb() {
        return rgb;
    }

    public float radius() {
        return radius;
    }

    public int durationMs() {
        return durationMs;
    }

    public Vec3 position() {
        return entity.position();
    }
}
