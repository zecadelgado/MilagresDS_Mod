package com.stefani.MilagresDSMod.magic.visual.heal;

import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HealAreaEntity extends Entity implements GeoEntity {
    private static final RawAnimation BREATH = RawAnimation.begin().thenLoop("animation.heal_ring.breathe");

    private static final PulseStage[] STAGES = new PulseStage[] {
            new PulseStage(0, 2.8f, 5.6f, 26, 6, 14, 0.08f, 0.55f, 3.4f, 0.25f),
            new PulseStage(18, 3.4f, 6.4f, 28, 7, 18, 0.16f, 0.6f, 3.8f, 0.3f),
            new PulseStage(36, 4.0f, 7.1f, 30, 8, 22, 0.22f, 0.68f, 4.1f, 0.34f),
            new PulseStage(54, 4.6f, 7.8f, 32, 9, 26, 0.28f, 0.78f, 4.4f, 0.36f)
    };

    private static final int TOTAL_LIFETIME = STAGES[STAGES.length - 1].triggerTick()
            + STAGES[STAGES.length - 1].ringDuration() + 24;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final List<PulseInstance> activePulses = new ArrayList<>();

    private int lifetime = TOTAL_LIFETIME;
    private int ownerId = -1;
    private boolean followOwner = true;
    private int nextStageIndex = 0;

    public HealAreaEntity(EntityType<? extends HealAreaEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        boolean client = level().isClientSide;
        updatePulseSchedule(!client);
        if (!client) {
            if (followOwner && ownerId >= 0) {
                Entity owner = level().getEntity(ownerId);
                if (owner != null) {
                    setPos(owner.getX(), owner.getY() - 0.1, owner.getZ());
                }
            }
            if (--lifetime <= 0 || activePulses.isEmpty() && nextStageIndex >= STAGES.length) {
                discard();
            }
        } else {
            spawnAmbientGlow();
        }
    }

    private void updatePulseSchedule(boolean spawnParticles) {
        while (nextStageIndex < STAGES.length && tickCount >= STAGES[nextStageIndex].triggerTick()) {
            spawnPulse(STAGES[nextStageIndex], spawnParticles);
            nextStageIndex++;
        }

        Iterator<PulseInstance> iterator = activePulses.iterator();
        while (iterator.hasNext()) {
            PulseInstance pulse = iterator.next();
            pulse.tick(this, spawnParticles);
            if (pulse.isComplete()) {
                iterator.remove();
            }
        }
    }

    private void spawnPulse(PulseStage stage, boolean spawnParticles) {
        if (nextStageIndex == 0) {
            followOwner = false;
        }
        activePulses.add(new PulseInstance(stage));
        if (spawnParticles) {
            emitBeams(stage);
            emitMotes(stage);
        }
    }

    private void emitBeams(PulseStage stage) {
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        int count = stage.beamCount();
        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * Math.PI * 2.0;
            double radius = stage.endRadius() * (0.6 + random.nextDouble() * 0.3);
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            double baseY = getY() + 0.25;
            int samples = 14;
            for (int s = 0; s < samples; s++) {
                double y = baseY + (stage.beamHeight() * s / samples);
                server.sendParticles(ParticleRegistry.HEAL_GLOW.get(), x, y, z, 1, 0, 0.01, 0, 0);
            }
        }
    }

    private void emitMotes(PulseStage stage) {
        double baseY = getY() + stage.beamHeight() + 0.6;
        for (int i = 0; i < stage.moteCount(); i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = stage.endRadius() * random.nextDouble();
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            double vy = -0.08 - random.nextDouble() * 0.03;
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleRegistry.HEAL_GLOW.get(), x, baseY, z, 1, 0.05, 0.05, 0.05, Math.abs(vy));
            } else {
                level().addParticle(ParticleRegistry.HEAL_GLOW.get(), x, baseY, z, 0, vy, 0);
            }
        }
    }

    private void spawnAmbientGlow() {
        for (int i = 0; i < 5; i++) {
            double ang = random.nextDouble() * Math.PI * 2;
            double r = 2.8 + random.nextDouble() * 0.9;
            double x = getX() + Math.cos(ang) * r;
            double z = getZ() + Math.sin(ang) * r;
            level().addParticle(ParticleRegistry.HEAL_GLOW.get(), x, getY() + 0.2 + random.nextDouble() * 0.8, z,
                    0, 0.01, 0);
        }
    }

    private void emitPulseRing(PulseStage stage, float radius) {
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        int segments = 64;
        double height = getY() + stage.particleLift();
        for (int i = 0; i < segments; i++) {
            double ang = (Math.PI * 2.0 / segments) * i;
            double x = getX() + Math.cos(ang) * radius;
            double z = getZ() + Math.sin(ang) * radius;
            server.sendParticles(ParticleRegistry.HEAL_GLOW.get(), x, height, z, 1, 0, 0.01, 0, 0);
        }
    }

    public void setOwner(Entity entity) {
        this.ownerId = entity == null ? -1 : entity.getId();
    }

    public void setFollowOwner(boolean followOwner) {
        this.followOwner = followOwner;
    }

    public List<PulseInstance> getActivePulses() {
        return Collections.unmodifiableList(activePulses);
    }

    public float getAmbientRadius(float partialTick) {
        return 3.2f + Mth.sin((tickCount + partialTick) * 0.12f) * 0.4f;
    }

    public float getClockRotation(float partialTick) {
        return (tickCount + partialTick) * 6.0f;
    }

    public float getRuneRotation(float partialTick) {
        return (tickCount + partialTick) * 4.0f;
    }

    public float getRuneLift(float partialTick) {
        return 0.45f + Mth.sin((tickCount + partialTick) * 0.3f) * 0.08f;
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
        tag.putInt("stage", nextStageIndex);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        lifetime = tag.getInt("life");
        ownerId = tag.getInt("owner");
        followOwner = tag.getBoolean("follow");
        nextStageIndex = Mth.clamp(tag.getInt("stage"), 0, STAGES.length);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "heal", 0, state -> {
            state.setAnimation(BREATH);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    public static final class PulseInstance {
        private final PulseStage stage;
        private float radius;
        private float previousRadius;
        private float progress;
        private float previousProgress;
        private int age;

        private PulseInstance(PulseStage stage) {
            this.stage = stage;
            this.radius = stage.startRadius();
            this.previousRadius = this.radius;
            this.progress = 0.0f;
            this.previousProgress = 0.0f;
            this.age = 0;
        }

        private void tick(HealAreaEntity entity, boolean spawnParticles) {
            previousProgress = progress;
            progress = age / (float) stage.ringDuration();
            previousRadius = radius;
            radius = stage.radiusAt(progress);
            if (spawnParticles && radius > 0.05f) {
                entity.emitPulseRing(stage, radius);
            }
            age++;
        }

        private boolean isComplete() {
            return age > stage.ringDuration();
        }

        public float getRadius(float partialTick) {
            return Mth.lerp(partialTick, previousRadius, radius);
        }

        public float getFade(float partialTick) {
            float p = Mth.clamp(Mth.lerp(partialTick, previousProgress, progress), 0.0f, 1.0f);
            return 1.0f - p;
        }

        public float getColumnAlpha(float partialTick) {
            return getFade(partialTick) * 0.9f;
        }

        public float getLayerHeight() {
            return stage.layerHeight();
        }

        public float getLayerThickness() {
            return stage.layerThickness();
        }

        public float getBeamHeight() {
            return stage.beamHeight();
        }

        public int getBeamCount() {
            return stage.beamCount();
        }
    }

    private record PulseStage(int triggerTick, float startRadius, float endRadius, int ringDuration,
                              int beamCount, int moteCount, float layerHeight, float layerThickness,
                              float beamHeight, float particleLift) {
        private float radiusAt(float progress) {
            return Mth.lerp(Mth.clamp(progress, 0.0f, 1.0f), startRadius, endRadius);
        }
    }
}
