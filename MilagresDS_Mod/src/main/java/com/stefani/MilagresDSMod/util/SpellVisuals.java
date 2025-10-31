package com.stefani.MilagresDSMod.util;

import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import com.stefani.MilagresDSMod.magic.visual.backend.SpellLighting;
import com.stefani.MilagresDSMod.magic.visual.heal.HealAreaEntity;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public final class SpellVisuals {
    private SpellVisuals() {}

    public static void showLightningSpear(Level level, LivingEntity caster, Vec3 dir) {
        if (level.isClientSide) {
            return;
        }
        LightningSpearEntity entity = EntityRegistry.LIGHTNING_SPEAR.get().create(level);
        if (entity == null) {
            return;
        }
        Vec3 hand = caster.position().add(0, caster.getEyeHeight() * 0.7, 0);
        entity.moveTo(hand.x, hand.y, hand.z);
        entity.setDeltaMovement(dir.normalize().scale(1.8));
        level.addFreshEntity(entity);
        tryCastPose(caster, "CastLightning");
        tryDynamicLight(level, entity, 0xF7E27A, 12f, 300);
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
        if (options.chargeSound != null && options.chargeTicks > 0) {
            level.playSound(null, hand.x, hand.y, hand.z, options.chargeSound, SoundSource.PLAYERS, 0.8f, 0.94f + caster.getRandom().nextFloat() * 0.08f);
        }
        if (options.triggerCastPose) {
            tryCastPose(caster, "CastFlame");
        }
        tryDynamicLight(level, entity, options.dynamicLightColor, options.dynamicLightRadius, options.dynamicLightDurationMs);
        if (options.launchSound != null && options.chargeTicks <= 0) {
            level.playSound(null, hand.x, hand.y, hand.z, options.launchSound, SoundSource.PLAYERS, 0.8f, 1.05f + caster.getRandom().nextFloat() * 0.1f);
        }
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
        tryCastPose(caster, "CastHeal");
        tryDynamicLight(level, entity, 0xF9EFAF, 8f, 800);
    }

    private static void tryCastPose(LivingEntity caster, String animName) {
        if (!(caster instanceof Player player)) {
            return;
        }
        if (!ModList.get().isLoaded("playeranimator")) {
            return;
        }
        try {
            Class<?> accessClass = Class.forName("dev.kosmx.playerAnim.api.layered.PlayerAnimationAccess");
            var getData = accessClass.getMethod("getPlayerAssociatedData", Player.class);
            Object data = getData.invoke(null, player);
            if (data == null) {
                return;
            }
            Class<?> anims = Class.forName("com.stefani.MilagresDSMod.magic.visual.backend.playeranim.MyCastAnimations");
            var field = anims.getDeclaredField(animName);
            Object clip = field.get(null);
            if (clip == null) {
                return;
            }
            Class<?> modifierLayer = Class.forName("dev.kosmx.playerAnim.api.layered.ModifierLayer");
            var set = data.getClass().getMethod("setAnimation", modifierLayer);
            set.invoke(data, clip);
        } catch (Throwable ignored) {
        }
    }

    private static void tryDynamicLight(Level level, Entity entity, int rgb, float radius, int durationMs) {
        if (!(level instanceof ServerLevel serverLevel) || radius <= 0f) {
            return;
        }
        SpellLighting.emitTravelLight(serverLevel, entity, rgb, radius, durationMs);
    }

    public static final class FlameSlingCastOptions {
        private final int chargeTicks;
        private final boolean triggerCastPose;
        private final double projectileSpeed;
        private final int dynamicLightColor;
        private final float dynamicLightRadius;
        private final int dynamicLightDurationMs;
        private final int dynamicLightInterval;
        private final @Nullable SoundEvent chargeSound;
        private final @Nullable SoundEvent launchSound;
        private final @Nullable SoundEvent impactSound;

        private FlameSlingCastOptions(Builder builder) {
            this.chargeTicks = builder.chargeTicks;
            this.triggerCastPose = builder.triggerPose;
            this.projectileSpeed = builder.projectileSpeed;
            this.dynamicLightColor = builder.dynamicLightColor;
            this.dynamicLightRadius = builder.dynamicLightRadius;
            this.dynamicLightDurationMs = builder.dynamicLightDurationMs;
            this.dynamicLightInterval = builder.dynamicLightInterval;
            this.chargeSound = builder.chargeSound;
            this.launchSound = builder.launchSound;
            this.impactSound = builder.impactSound;
        }

        public static FlameSlingCastOptions defaults() {
            return builder().build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder toBuilder() {
            return builder()
                    .chargeTicks(chargeTicks)
                    .projectileSpeed(projectileSpeed)
                    .dynamicLight(dynamicLightColor, dynamicLightRadius, dynamicLightDurationMs, dynamicLightInterval)
                    .triggerPose(triggerCastPose)
                    .sounds(chargeSound, launchSound, impactSound);
        }

        public static final class Builder {
            private int chargeTicks = 0;
            private boolean triggerPose = true;
            private double projectileSpeed = 1.2;
            private int dynamicLightColor = 0xFF6A2A;
            private float dynamicLightRadius = 10f;
            private int dynamicLightDurationMs = 500;
            private int dynamicLightInterval = 3;
            private @Nullable SoundEvent chargeSound;
            private @Nullable SoundEvent launchSound;
            private @Nullable SoundEvent impactSound;

            public Builder chargeTicks(int ticks) {
                this.chargeTicks = Math.max(0, ticks);
                return this;
            }

            public Builder triggerPose(boolean trigger) {
                this.triggerPose = trigger;
                return this;
            }

            public Builder projectileSpeed(double speed) {
                this.projectileSpeed = Math.max(0.1, speed);
                return this;
            }

            public Builder dynamicLight(int color, float radius, int durationMs, int intervalTicks) {
                this.dynamicLightColor = color;
                this.dynamicLightRadius = Math.max(0f, radius);
                this.dynamicLightDurationMs = Math.max(0, durationMs);
                this.dynamicLightInterval = Math.max(1, intervalTicks);
                return this;
            }

            public Builder sounds(@Nullable SoundEvent charge, @Nullable SoundEvent launch, @Nullable SoundEvent impact) {
                this.chargeSound = charge;
                this.launchSound = launch;
                this.impactSound = impact;
                return this;
            }

            public FlameSlingCastOptions build() {
                return new FlameSlingCastOptions(this);
            }
        }
    }
}
