package com.stefani.MilagresDSMod.magic.visual.flame;

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

public class FlameSlingEntity extends Entity {
    private int lifetime = 52;

    public FlameSlingEntity(EntityType<? extends FlameSlingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(getDeltaMovement().add(0, -0.01, 0));
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
                burst(ParticleRegistry.EMBER.get(), 22);
            }
            discard();
        }

        if (level().isClientSide) {
            trail(ParticleRegistry.EMBER.get(), 2);
        }
    }

    private void trail(SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double sx = (random.nextDouble() - 0.5) * 0.01;
            double sy = (random.nextDouble()) * 0.02;
            double sz = (random.nextDouble() - 0.5) * 0.01;
            level().addParticle(type, getX(), getY(), getZ(), sx, sy, sz);
        }
    }

    private void burst(SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2;
            double b = random.nextDouble() * Math.PI;
            double r = 0.2 + random.nextDouble() * 0.35;
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
