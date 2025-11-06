package com.stefani.MilagresDSMod.magic.visual.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Classe-base para projéteis mágicos (inspiração: ISS AbstractMagicProjectile).
 * - isola cliente vs servidor
 * - fornece hooks estáveis para trilha/impacto/velocidade
 * - remove necessidade de lógica cliente no lado servidor
 */
public abstract class BaseMagicProjectile extends Projectile {
    protected static final int EXPIRE_TICKS = 15 * 20;
    protected float damage;

    public BaseMagicProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    /** CLIENTE: chamada a cada tick para desenhar trilhas */
    protected abstract void trailParticlesClient();
    /** SERVIDOR: chamada no impacto para burst/efeitos no mundo */
    protected abstract void impactParticlesServer(double x, double y, double z);
    /** velocidade base do projétil */
    protected abstract float projectileSpeed();
    /** som opcional de impacto */
    protected abstract SoundEvent impactSound();

    public void setDamage(float dmg) { this.damage = dmg; }
    public float getDamage() { return damage; }

    public void shoot(Vec3 dir) { setDeltaMovement(dir.scale(projectileSpeed())); }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > EXPIRE_TICKS) { discard(); return; }
        if (level().isClientSide) { trailParticlesClient(); }
        // viagem simples com gravidade leve (igual ISS)
        setPos(position().add(getDeltaMovement()));
        if (!this.isNoGravity()) {
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x, v.y - 0.05f, v.z);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!level().isClientSide) {
            impactParticlesServer(getX(), getY(), getZ());
            SoundEvent s = impactSound();
            if (s != null) level().playSound(null, getX(), getY(), getZ(), s, SoundSource.PLAYERS, 2f, 0.95f + level().random.nextFloat()*0.1f);
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putInt("Age", this.tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.damage = tag.getFloat("Damage");
        this.tickCount = tag.getInt("Age");
    }
}
