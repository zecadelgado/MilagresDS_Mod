package com.stefani.MilagresDSMod.magic.visual.lightning;

import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class LightningSpearEntity extends Entity {
    private int lifetime = 26;

    public LightningSpearEntity(EntityType<? extends LightningSpearEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
        if (!level().isClientSide && --lifetime <= 0) {
            discard();
            return;
        }

        HitResult hit = level().clip(new ClipContext(
                position(), position().add(getDeltaMovement().scale(1.2)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.MISS) {
            if (level().isClientSide) {
                spawnBurst(ParticleRegistry.LIGHTNING_SPARK.get(), 16);
            }
            discard();
        }

        if (level().isClientSide) {
            spawnTrail(ParticleRegistry.LIGHTNING_SPARK.get(), 3);
        }
    }

    private void spawnTrail(SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double sx = (random.nextDouble() - 0.5) * 0.02;
            double sy = (random.nextDouble() - 0.5) * 0.02;
            double sz = (random.nextDouble() - 0.5) * 0.02;
            level().addParticle(type, getX(), getY(), getZ(), sx, sy, sz);
        }
    }

    private void spawnBurst(SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2;
            double b = random.nextDouble() * Math.PI;
            double r = 0.25 + random.nextDouble() * 0.25;
            double sx = Math.cos(a) * Math.sin(b) * r;
            double sy = Math.cos(b) * r;
            double sz = Math.sin(a) * Math.sin(b) * r;
            level().addParticle(type, getX(), getY(), getZ(), sx, sy, sz);
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("life", lifetime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        lifetime = tag.getInt("life");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
