package com.stefani.MilagresDSMod.util;

import com.stefani.MilagresDSMod.magic.visual.backend.lighting.DynamicLightDispatcher;
import com.stefani.MilagresDSMod.magic.visual.backend.playeranim.PlayerAnimatorCompat;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import com.stefani.MilagresDSMod.magic.visual.heal.HealAreaEntity;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SpellVisuals {
    private static final int DEFAULT_LIGHTNING_SPEAR_CHARGE_TICKS = 28;

    private SpellVisuals() {}

    public static class FlameSlingCastOptions {
        public final int chargeTicks;
        public final double projectileSpeed;
        public final SoundEvent launchSound;
        public final SoundEvent impactSound;
        public final int dynamicLightColor;
        public final float dynamicLightRadius;
        public final int dynamicLightDurationMs;
        public final int dynamicLightInterval;

        public FlameSlingCastOptions(int chargeTicks, double projectileSpeed, SoundEvent launchSound, SoundEvent impactSound,
                                      int dynamicLightColor, float dynamicLightRadius, int dynamicLightDurationMs, int dynamicLightInterval) {
            this.chargeTicks = chargeTicks;
            this.projectileSpeed = projectileSpeed;
            this.launchSound = launchSound;
            this.impactSound = impactSound;
            this.dynamicLightColor = dynamicLightColor;
            this.dynamicLightRadius = dynamicLightRadius;
            this.dynamicLightDurationMs = dynamicLightDurationMs;
            this.dynamicLightInterval = dynamicLightInterval;
        }

        public static FlameSlingCastOptions defaults() {
            return new FlameSlingCastOptions(10, 1.2, null, null, 0xFF6A2A, 10f, 500, 2);
        }
    }

    public static void showLightningSpear(Level level, LivingEntity caster, Vec3 dir) {
        showLightningSpear(level, caster, dir, DEFAULT_LIGHTNING_SPEAR_CHARGE_TICKS);
    }

    public static void showLightningSpear(Level level, LivingEntity caster, Vec3 dir, int chargeTicks) {
        if (level.isClientSide) {
            return;
        }
        LightningSpearEntity entity = EntityRegistry.LIGHTNING_SPEAR.get().create(level);
        if (entity == null) {
            return;
        }
        Vec3 hand = caster.position().add(0, caster.getEyeHeight() * 0.7, 0);
        entity.moveTo(hand.x, hand.y, hand.z);
        entity.configure(caster, dir, chargeTicks);
        level.addFreshEntity(entity);
        PlayerAnimatorCompat.playClip(caster, "LightningSpearCharge");
        int totalDuration = chargeTicks + LightningSpearEntity.MAX_FLIGHT_TICKS + LightningSpearEntity.IMPACT_LINGER_TICKS;
        tryDynamicLight(level, entity, caster, 0xF7E27A, 12f, totalDuration);
    }

    public static void showFlameSling(Level level, LivingEntity caster, Vec3 dir) {
        showFlameSling(level, caster, dir, FlameSlingCastOptions.defaults());
    }

    public static void showFlameSling(Level level, LivingEntity caster, Vec3 dir, FlameSlingCastOptions options) {
        if (level.isClientSide) {
            return;
        }
        FlameSlingEntity entity = EntityRegistry.FLAME_SLING.get().create(level);
        if (entity == null) {
            return;
        }
        Vec3 hand = caster.position().add(0, caster.getEyeHeight() * 0.65, 0);
        entity.moveTo(hand.x, hand.y, hand.z);
        entity.configureSounds(options.launchSound, options.impactSound);
        entity.configureDynamicLight(options.dynamicLightColor, options.dynamicLightRadius, options.dynamicLightDurationMs, options.dynamicLightInterval);
        Vec3 motion = dir.normalize().scale(options.projectileSpeed);
        entity.configureLaunch(motion, options.chargeTicks);
        level.addFreshEntity(entity);
        PlayerAnimatorCompat.playClip(caster, "CastFlame");
        tryDynamicLight(level, entity, caster, 0xFF6A2A, 10f, 20);
    }

    public static void showHeal(Level level, LivingEntity caster) {
        if (level.isClientSide) {
            return;
        }
        HealAreaEntity entity = EntityRegistry.HEAL_AREA.get().create(level);
        if (entity == null) {
            return;
        }
        entity.setOwner(caster);
        entity.moveTo(caster.getX(), caster.getY() - 0.1, caster.getZ());
        level.addFreshEntity(entity);
        PlayerAnimatorCompat.playClip(caster, "CastHeal");
        tryDynamicLight(level, entity, caster, 0xF9EFAF, 8f, 40);
    }

    private static void tryDynamicLight(Level level, Entity entity, LivingEntity caster, int rgb, float radius, int durationTicks) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        DynamicLightDispatcher.emit(serverLevel, entity, caster, rgb, radius, durationTicks);
    }
}
