package com.stefani.MilagresDSMod.magic.visual.flame;

import com.stefani.MilagresDSMod.magic.visual.backend.SpellLighting;
import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FlameSlingEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(FlameSlingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3f> DATA_LAUNCH_VECTOR =
            SynchedEntityData.defineId(FlameSlingEntity.class, EntityDataSerializers.VECTOR3);

    private static final int STATE_CHARGING = 0;
    private static final int STATE_FLYING = 1;
    private static final int STATE_IMPACT = 2;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.flame_sling.idle");
    private static final RawAnimation IMPACT_ANIM = RawAnimation.begin().thenPlay("animation.flame_sling.impact");

    private final int impactDuration = 18;
    private int lifetime = 60;
    private int chargeTicks;
    private int impactTicks;
    private int glowTicks;
    private int dynamicLightTicker;
    private int dynamicLightInterval = 2;
    private int dynamicLightDurationMs = 500;
    private int dynamicLightColor = 0xFF6A2A;
    private float dynamicLightRadius = 10f;
    private Vec3 queuedLaunch = Vec3.ZERO;
    private SoundEvent launchSound;
    private SoundEvent impactSound;
    private transient AnimatableInstanceCache geckoCache;

    public FlameSlingEntity(EntityType<? extends FlameSlingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_STATE, STATE_CHARGING);
        entityData.define(DATA_LAUNCH_VECTOR, new Vector3f(0, 0, 0));
    }

    public void configureLaunch(Vec3 motion, int chargeDuration) {
        this.queuedLaunch = motion;
        this.chargeTicks = Math.max(0, chargeDuration);
        this.entityData.set(DATA_LAUNCH_VECTOR, motion.toVector3f());
        if (chargeDuration <= 0) {
            beginFlight();
        } else {
            this.entityData.set(DATA_STATE, STATE_CHARGING);
            setDeltaMovement(Vec3.ZERO);
        }
    }

    public void configureDynamicLight(int rgb, float radius, int durationMs, int intervalTicks) {
        this.dynamicLightColor = rgb;
        this.dynamicLightRadius = Math.max(0f, radius);
        this.dynamicLightDurationMs = Math.max(0, durationMs);
        this.dynamicLightInterval = Math.max(1, intervalTicks);
    }

    public void configureSounds(@Nullable SoundEvent launch, @Nullable SoundEvent impact) {
        this.launchSound = launch;
        this.impactSound = impact;
    }

    public boolean isImpactState() {
        return entityData.get(DATA_STATE) == STATE_IMPACT;
    }

    @Override
    public void tick() {
        super.tick();
        if (glowTicks > 0 && --glowTicks <= 0) {
            setGlowingTag(false);
        }

        switch (entityData.get(DATA_STATE)) {
            case STATE_CHARGING -> tickCharging();
            case STATE_FLYING -> tickFlying();
            case STATE_IMPACT -> tickImpact();
        }
    }

    private void tickCharging() {
        if (level().isClientSide) {
            spawnOrbitingEmbers(0.32f, 4);
            spawnRibbon(0.25f);
        }
        if (!level().isClientSide && dynamicLightRadius > 0f && (tickCount & 3) == 0) {
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                SpellLighting.emitTravelLight(serverLevel, this, dynamicLightColor, dynamicLightRadius * 0.85f, dynamicLightDurationMs);
            }
        }
        if (chargeTicks > 0) {
            chargeTicks--;
        }
        if (chargeTicks <= 0) {
            beginFlight();
        }
    }

    private void tickFlying() {
        Vec3 motion = getDeltaMovement().add(0, -0.01, 0);
        setDeltaMovement(motion);
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);

        if (!level().isClientSide) {
            if (--lifetime <= 0) {
                beginImpact(position());
                return;
            }
            HitResult hit = level().clip(new ClipContext(
                    position(), position().add(motion.scale(1.2)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) {
                beginImpact(hit.getLocation());
                return;
            }
            if (dynamicLightRadius > 0f && ++dynamicLightTicker >= dynamicLightInterval) {
                dynamicLightTicker = 0;
                if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    SpellLighting.emitTravelLight(serverLevel, this, dynamicLightColor, dynamicLightRadius, dynamicLightDurationMs);
                }
            }
        } else {
            spawnOrbitingEmbers(0.38f, 5);
            spawnRibbon(0.4f);
        }
    }

    private void tickImpact() {
        if (impactTicks == 0 && !level().isClientSide) {
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                SpellLighting.emitTravelLight(serverLevel, this, dynamicLightColor, dynamicLightRadius * 1.5f, dynamicLightDurationMs);
            }
        }
        if (level().isClientSide) {
            spawnImpactColumn(impactTicks);
            spawnScorchDecal();
        }
        impactTicks++;
        if (!level().isClientSide && impactTicks > impactDuration) {
            discard();
        }
    }

    private void beginFlight() {
        if (entityData.get(DATA_STATE) == STATE_FLYING) {
            return;
        }
        setDeltaMovement(queuedLaunch);
        entityData.set(DATA_STATE, STATE_FLYING);
        if (!level().isClientSide && launchSound != null && tickCount > 0) {
            level().playSound(null, getX(), getY(), getZ(), launchSound, SoundSource.PLAYERS, 0.8f, 1.05f + random.nextFloat() * 0.1f);
        } else {
            spawnOrbitingEmbers(0.32f, 6);
        }
    }

    private void beginImpact(Vec3 hitPos) {
        if (entityData.get(DATA_STATE) == STATE_IMPACT) {
            return;
        }
        setPos(hitPos.x, hitPos.y, hitPos.z);
        setDeltaMovement(Vec3.ZERO);
        entityData.set(DATA_STATE, STATE_IMPACT);
        impactTicks = 0;
        if (!level().isClientSide) {
            if (impactSound != null) {
                level().playSound(null, hitPos.x, hitPos.y, hitPos.z, impactSound, SoundSource.PLAYERS, 1f, 0.95f + random.nextFloat() * 0.1f);
            }
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                SpellLighting.emitTravelLight(serverLevel, this, dynamicLightColor, dynamicLightRadius * 1.2f, dynamicLightDurationMs);
            }
        } else {
            spawnImpactBurst();
        }
    }

    private void spawnOrbitingEmbers(float radius, int rings) {
        double base = tickCount * 0.25;
        for (int i = 0; i < rings; i++) {
            double angle = base + i * (Math.PI * 2 / rings);
            double height = Math.sin(base * 0.35 + i * 0.6) * 0.1;
            double px = getX() + Math.cos(angle) * radius;
            double py = getY() + 0.1 + height;
            double pz = getZ() + Math.sin(angle) * radius;
            double sx = -Math.sin(angle) * 0.02;
            double sy = 0.015 + random.nextDouble() * 0.01;
            double sz = Math.cos(angle) * 0.02;
            level().addParticle(ParticleRegistry.EMBER.get(), px, py, pz, sx, sy, sz);
        }
    }

    private void spawnRibbon(float length) {
        Vec3 backwards = getDeltaMovement().normalize().scale(-length);
        if (Double.isNaN(backwards.x)) {
            backwards = new Vec3(0, -0.01, 0);
        }
        for (int i = 0; i < 6; i++) {
            double t = i / 6.0;
            double px = getX() + backwards.x * t;
            double py = getY() + backwards.y * t + (random.nextDouble() - 0.5) * 0.04;
            double pz = getZ() + backwards.z * t;
            double sx = (random.nextDouble() - 0.5) * 0.015;
            double sy = (random.nextDouble() - 0.15) * 0.03;
            double sz = (random.nextDouble() - 0.5) * 0.015;
            level().addParticle(ParticleRegistry.EMBER.get(), px, py, pz, sx, sy, sz);
        }
    }

    private void spawnImpactColumn(int age) {
        double height = Math.min(1.6, age * 0.12);
        for (int i = 0; i < 16; i++) {
            double angle = i * (Math.PI * 2 / 16.0);
            double radius = 0.25 + Math.sin(age * 0.5 + i) * 0.05;
            double px = getX() + Math.cos(angle) * radius;
            double pz = getZ() + Math.sin(angle) * radius;
            double py = getY() + 0.1 + height * 0.6;
            double sx = Math.cos(angle) * 0.02;
            double sz = Math.sin(angle) * 0.02;
            double sy = 0.08 + random.nextDouble() * 0.05;
            level().addParticle(ParticleTypes.FLAME, px, py, pz, sx, sy, sz);
        }
        for (int i = 0; i < 8; i++) {
            double px = getX() + (random.nextDouble() - 0.5) * 0.45;
            double py = getY() + 0.02;
            double pz = getZ() + (random.nextDouble() - 0.5) * 0.45;
            level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 0, 0.02 + random.nextDouble() * 0.02, 0);
        }
    }

    private void spawnScorchDecal() {
        for (int i = 0; i < 6; i++) {
            double angle = i * (Math.PI * 2 / 6.0) + tickCount * 0.12;
            double radius = 0.35 + Math.sin(tickCount * 0.2 + i) * 0.05;
            double px = getX() + Math.cos(angle) * radius;
            double pz = getZ() + Math.sin(angle) * radius;
            level().addParticle(ParticleTypes.ASH, px, getY() + 0.02, pz, 0, 0.002, 0);
        }
    }

    private void spawnImpactBurst() {
        burst(ParticleRegistry.EMBER.get(), 28, 0.32);
        burst(ParticleTypes.SMALL_FLAME, 18, 0.24);
    }

    private void burst(SimpleParticleType type, int count, double strength) {
        for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2;
            double b = random.nextDouble() * Math.PI;
            double r = strength + random.nextDouble() * strength * 0.65;
            double sx = Math.cos(a) * Math.sin(b) * r;
            double sy = Math.cos(b) * r;
            double sz = Math.sin(a) * Math.sin(b) * r;
            level().addParticle(type, getX(), getY(), getZ(), sx, sy, sz);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_LAUNCH_VECTOR.equals(key)) {
            Vector3f vec = entityData.get(DATA_LAUNCH_VECTOR);
            this.queuedLaunch = new Vec3(vec.x, vec.y, vec.z);
        } else if (DATA_STATE.equals(key)) {
            switch (entityData.get(DATA_STATE)) {
                case STATE_FLYING -> beginFlight();
                case STATE_IMPACT -> {
                    impactTicks = 0;
                    if (level().isClientSide) {
                        spawnImpactBurst();
                    }
                }
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("life", lifetime);
        tag.putInt("charge", chargeTicks);
        tag.putInt("state", entityData.get(DATA_STATE));
        tag.putDouble("launchX", queuedLaunch.x);
        tag.putDouble("launchY", queuedLaunch.y);
        tag.putDouble("launchZ", queuedLaunch.z);
        tag.putInt("lightColor", dynamicLightColor);
        tag.putFloat("lightRadius", dynamicLightRadius);
        tag.putInt("lightDuration", dynamicLightDurationMs);
        tag.putInt("lightInterval", dynamicLightInterval);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        lifetime = tag.getInt("life");
        chargeTicks = tag.getInt("charge");
        int state = tag.getInt("state");
        queuedLaunch = new Vec3(tag.getDouble("launchX"), tag.getDouble("launchY"), tag.getDouble("launchZ"));
        dynamicLightColor = tag.getInt("lightColor");
        dynamicLightRadius = tag.getFloat("lightRadius");
        dynamicLightDurationMs = tag.getInt("lightDuration");
        dynamicLightInterval = Math.max(1, tag.getInt("lightInterval"));
        entityData.set(DATA_LAUNCH_VECTOR, queuedLaunch.toVector3f());
        entityData.set(DATA_STATE, state);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public void pulseGlow(int ticks) {
        glowTicks = Math.max(glowTicks, ticks);
        setGlowingTag(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> {
            if (isImpactState()) {
                return PlayState.STOP;
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
        controllers.add(new AnimationController<>(this, "impact", 0, state -> {
            if (isImpactState()) {
                return state.setAndContinue(IMPACT_ANIM);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        if (geckoCache == null) {
            geckoCache = GeckoLibUtil.createInstanceCache(this);
        }
        return geckoCache;
    }
}
