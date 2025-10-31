package com.stefani.MilagresDSMod.entity;

import com.stefani.MilagresDSMod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class LightningSpearEntity extends ThrownItemProjectile {

    public LightningSpearEntity(EntityType<? extends ThrownItemProjectile> type, Level level) {
        super(type, level);
    }

    public LightningSpearEntity(Level level, LivingEntity thrower) {
        super(ModEntities.LIGHTNING_SPEAR.get(), thrower, level);
    }

    public LightningSpearEntity(Level level, double x, double y, double z) {
        super(ModEntities.LIGHTNING_SPEAR.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.LIGHTNING_SPEAR_ITEM.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && result.getEntity() != null) {
            DamageSource src = level().damageSources().thrown(this, getOwner());
            result.getEntity().hurt(src, 10.0F);
            discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                        getX(), getY(), getZ(),
                        (random.nextDouble() - 0.5D) * 0.2D,
                        (random.nextDouble() - 0.5D) * 0.2D,
                        (random.nextDouble() - 0.5D) * 0.2D);
            }
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { }
}
