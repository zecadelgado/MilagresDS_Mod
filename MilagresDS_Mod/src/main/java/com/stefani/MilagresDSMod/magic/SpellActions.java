package com.stefani.MilagresDSMod.magic;

import com.stefani.MilagresDSMod.attribute.IPlayerAttributes;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.Consumer;

public final class SpellActions {
    private SpellActions() {
    }

    public static SpellAction spawnProjectile(Function<SpellContext, Entity> factory) {
        return context -> {
            Entity projectile = factory.apply(context);
            if (projectile != null) {
                context.level().addFreshEntity(projectile);
            }
        };
    }

    public static SpellAction dealDamageToTarget(Function<Level, DamageSource> damageSource, float amount) {
        return context -> context.entityHitResult()
                .map(EntityHitResult::getEntity)
                .ifPresent(entity -> {
                    SpellProperties properties = context.spell().getProperties();
                    float baseDamage = properties.getBaseDamage().orElse(amount);
                    float finalDamage = context.player()
                            .getCapability(playerattributesprovider.PLAYER_ATTRIBUTES)
                            .map(attributes -> (float) Math.max(0.0D, baseDamage
                                    + computeScalingBonus(attributes, properties)))
                            .orElse(baseDamage);
                    if (finalDamage > 0.0F) {
                        entity.hurt(damageSource.apply(context.level()), finalDamage);
                    }
                });
    }

    public static SpellAction healPlayer(float amount) {
        return context -> {
            Player player = context.player();
            player.heal(amount);
        };
    }

    public static SpellAction applyEffect(Supplier<MobEffectInstance> effectSupplier) {
        return context -> {
            MobEffectInstance instance = effectSupplier.get();
            if (instance != null) {
                context.player().addEffect(instance);
            }
        };
    }

    public static SpellAction spawnParticlesAroundPlayer(Supplier<? extends ParticleOptions> particle, int count,
                                                         double spreadX, double spreadY, double spreadZ, double speed) {
        return context -> new SpellProperties.SpellParticles(particle, count, spreadX, spreadY, spreadZ, speed)
                .spawn(context.level(), context.origin());
    }

    private static double computeScalingBonus(IPlayerAttributes attributes, SpellProperties properties) {
        double bonus = 0.0D;
        for (SpellScaling scaling : properties.getScaling().values()) {
            int value = switch (scaling.attribute()) {
                case INTELLIGENCE -> attributes.getIntelligence();
                case FAITH -> attributes.getFaith();
                case ARCANE -> attributes.getArcane();
            };
            bonus += scaling.computeBonus(value);
        }
        return bonus;
    }

    /**
     * Wraps a consumer of {@link SpellContext} in a {@link SpellAction} that only
     * executes its action on the logical server.  When a spell is cast on the client,
     * the returned action will do nothing.  This is useful for spawning entities or
     * performing other server‑side effects as part of a spell.
     *
     * @param consumer the logic to run on the server during spell execution
     * @return a {@link SpellAction} that runs the given consumer when executed on the server
     */
    public static SpellAction runOnServer(Consumer<SpellContext> consumer) {
        return context -> {
            if (!context.level().isClientSide()) {
                consumer.accept(context);
            }
        };
    }

    /**
     * Alias for {@link #runOnServer(Consumer)}.  Some existing spells reference
     * {@code RunOnServer} with a capital R; providing this method ensures those calls
     * continue to work without modification.
     *
     * @param consumer the logic to run on the server during spell execution
     * @return a {@link SpellAction} that runs the given consumer when executed on the server
     */
    public static SpellAction RunOnServer(Consumer<SpellContext> consumer) {
        return runOnServer(consumer);
    }
}

