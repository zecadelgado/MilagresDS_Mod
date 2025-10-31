package com.stefani.MilagresDSMod.magic.visual.heal;

import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class HealAreaEntity extends Entity {
    private int lifetime = 52;
    private int ownerId = -1;
    private boolean followOwner = true;

    public HealAreaEntity(EntityType<? extends HealAreaEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (followOwner && ownerId >= 0) {
                Entity owner = level().getEntity(ownerId);
                if (owner != null) {
                    setPos(owner.getX(), owner.getY() - 0.1, owner.getZ());
                }
            }
            if (--lifetime <= 0) {
                discard();
            }
        } else {
            for (int i = 0; i < 4; i++) {
                double ang = random.nextDouble() * Math.PI * 2;
                double r = 2.6 + random.nextDouble() * 0.7;
                double x = getX() + Math.cos(ang) * r;
                double z = getZ() + Math.sin(ang) * r;
                level().addParticle(ParticleRegistry.HEAL_GLOW.get(), x, getY() + 0.15 + random.nextDouble() * 0.6, z, 0, 0.01, 0);
            }
        }
    }

    public void setOwner(Entity entity) {
        this.ownerId = entity == null ? -1 : entity.getId();
    }

    public void setFollowOwner(boolean followOwner) {
        this.followOwner = followOwner;
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
        tag.putInt("owner", ownerId);
        tag.putBoolean("follow", followOwner);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        lifetime = tag.getInt("life");
        ownerId = tag.getInt("owner");
        followOwner = tag.getBoolean("follow");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
