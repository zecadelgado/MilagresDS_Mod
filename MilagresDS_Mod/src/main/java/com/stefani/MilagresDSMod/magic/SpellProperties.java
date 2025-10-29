package com.stefani.MilagresDSMod.magic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class SpellProperties {
    private final int manaCost;
    private final int cooldownTicks;
    private final ResourceLocation icon;
    private final double castRange;
    private final SpellSound castSound;
    private final SpellParticles castParticles;
    private final Float baseDamage;
    private final Float healingAmount;
    private final Component description;
    private final Component effectSummary;

    private SpellProperties(Builder builder) {
        this.manaCost = builder.manaCost;
        this.cooldownTicks = builder.cooldownTicks;
        this.icon = builder.icon;
        this.castRange = builder.castRange;
        this.castSound = builder.castSound;
        this.castParticles = builder.castParticles;
        this.baseDamage = builder.baseDamage;
        this.healingAmount = builder.healingAmount;
        this.description = builder.description;
        this.effectSummary = builder.effectSummary;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public double getCastRange() {
        return castRange;
    }

    public Optional<SpellSound> getCastSound() {
        return Optional.ofNullable(castSound);
    }

    public Optional<SpellParticles> getCastParticles() {
        return Optional.ofNullable(castParticles);
    }

    public Optional<Float> getBaseDamage() {
        return Optional.ofNullable(baseDamage);
    }

    public Optional<Float> getHealingAmount() {
        return Optional.ofNullable(healingAmount);
    }

    public Optional<Component> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<Component> getEffectSummary() {
        return Optional.ofNullable(effectSummary);
    }

    public static final class Builder {
        private Integer manaCost;
        private Integer cooldownTicks;
        private ResourceLocation icon;
        private double castRange = 16.0D;
        private SpellSound castSound;
        private SpellParticles castParticles;
        private Float baseDamage;
        private Float healingAmount;
        private Component description;
        private Component effectSummary;

        private Builder() {
        }

        public Builder manaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        public Builder cooldown(int cooldownTicks) {
            this.cooldownTicks = cooldownTicks;
            return this;
        }

        public Builder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Builder castRange(double castRange) {
            this.castRange = castRange;
            return this;
        }

        public Builder castSound(SoundEvent soundEvent) {
            return castSound(soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        public Builder castSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
            this.castSound = new SpellSound(soundEvent, source, volume, pitch);
            return this;
        }

        public Builder castParticles(Supplier<? extends ParticleOptions> particle, int count, double spreadX, double spreadY,
                                     double spreadZ, double speed) {
            this.castParticles = new SpellParticles(particle, count, spreadX, spreadY, spreadZ, speed);
            return this;
        }

        public Builder castParticles(Supplier<? extends ParticleOptions> particle, int count, double spreadX, double spreadY,
                                     double spreadZ) {
            return castParticles(particle, count, spreadX, spreadY, spreadZ, 0.0D);
        }

        public Builder baseDamage(float baseDamage) {
            this.baseDamage = baseDamage;
            return this;
        }

        public Builder healingAmount(float healingAmount) {
            this.healingAmount = healingAmount;
            return this;
        }

        public Builder description(Component description) {
            this.description = description;
            return this;
        }

        public Builder effectSummary(Component effectSummary) {
            this.effectSummary = effectSummary;
            return this;
        }

        public SpellProperties build() {
            Objects.requireNonNull(manaCost, "Mana cost must be defined");
            Objects.requireNonNull(cooldownTicks, "Cooldown must be defined");
            Objects.requireNonNull(icon, "Icon must be defined");
            return new SpellProperties(this);
        }
    }

    public record SpellSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        public void play(Level level, Player player) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, source, volume, pitch);
        }
    }

    public record SpellParticles(Supplier<? extends ParticleOptions> particle, int count, double spreadX, double spreadY,
                                 double spreadZ, double speed) {
        public void spawn(Level level, Vec3 origin) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(particle.get(), origin.x, origin.y, origin.z, count, spreadX, spreadY, spreadZ, speed);
            }
        }
    }
}
