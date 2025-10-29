package com.stefani.MilagresDSMod.magic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class SpellContext {
    private final spell spell;
    private final Player player;
    private final Level level;
    private final Vec3 origin;
    private final Vec3 direction;
    private final double range;
    @Nullable
    private final HitResult hitResult;
    @Nullable
    private final EntityHitResult entityHitResult;

    SpellContext(spell spell, Player player, Level level, Vec3 origin, Vec3 direction, double range,
                 @Nullable HitResult hitResult, @Nullable EntityHitResult entityHitResult) {
        this.spell = spell;
        this.player = player;
        this.level = level;
        this.origin = origin;
        this.direction = direction;
        this.range = range;
        this.hitResult = hitResult;
        this.entityHitResult = entityHitResult;
    }

    public static SpellContext create(spell spell, Player player, Level level) {
        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getLookAngle();
        double range = spell.getProperties().getCastRange();
        HitResult hitResult = player.pick(range, 0.0F, false);
        EntityHitResult entityHitResult = hitResult instanceof EntityHitResult entityHit ? entityHit : null;
        return new SpellContext(spell, player, level, origin, direction, range, hitResult, entityHitResult);
    }

    public spell spell() {
        return spell;
    }

    public Player player() {
        return player;
    }

    public Level level() {
        return level;
    }

    public Vec3 origin() {
        return origin;
    }

    public Vec3 direction() {
        return direction;
    }

    public double range() {
        return range;
    }

    @Nullable
    public HitResult hitResult() {
        return hitResult;
    }

    public java.util.Optional<EntityHitResult> entityHitResult() {
        return java.util.Optional.ofNullable(entityHitResult);
    }
}
