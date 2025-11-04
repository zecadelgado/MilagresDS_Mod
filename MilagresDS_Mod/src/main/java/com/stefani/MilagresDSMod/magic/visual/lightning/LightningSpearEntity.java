package com.stefani.MilagresDSMod.magic.visual.lightning;

import com.stefani.MilagresDSMod.client.LightningSpearClientAccess;
import com.stefani.MilagresDSMod.magic.visual.backend.playeranim.PlayerAnimatorCompat;
import com.stefani.MilagresDSMod.registry.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;\r\nimport net.minecraft.world.entity.EntityType;\r\nimport net.minecraft.world.entity.projectile.AbstractHurtingProjectile;\r\nimport net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;\r\nimport net.minecraft.world.phys.HitResult;\r\nimport net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;\r\nimport net.minecraftforge.network.NetworkHooks;\r\nimport software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class LightningSpearEntity extends AbstractHurtingProjectile implements GeoAnimatable {
    public static final int MAX_FLIGHT_TICKS = 40;
    public static final int IMPACT_LINGER_TICKS = 35;
    private static final double FLIGHT_SPEED = 1.85;

    private final AnimatableInstanceCache geckoCache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CHARGE_DURATION = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STATE_TIME = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DIR_X = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_Y = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_Z = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_CASTER = SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int serverStateTicks;
    private boolean impactSpawned;
    @Nullable
    private LivingEntity cachedCaster;

    public LightningSpearEntity(EntityType<? extends LightningSpearEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void configure(LivingEntity caster, Vec3 direction, int chargeTicks) {
        setCaster(caster);
        setLaunchDirection(direction);
        setChargeDuration(chargeTicks);
        setState(SpearState.CHARGING);
        this.serverStateTicks = 0;
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        SpearState state = getState();
        if (!level().isClientSide) {
            switch (state) {
                case CHARGING -> tickChargingServer();
                case FLIGHT -> tickFlightServer();
                case IMPACT -> tickImpactServer();
            }
        } else {
            spawnClientEffects(state);
        }
        updateRotation();
    }

    private void tickChargingServer() {
        LivingEntity caster = getCaster();
        serverStateTicks++;
        entityData.set(DATA_STATE_TIME, serverStateTicks);
        if (caster != null) {
            Vec3 origin = caster.position().add(0, caster.getEyeHeight() * 0.7, 0);
            Vec3 offset = getLaunchDirection().normalize().scale(0.6);
            setPos(origin.x + offset.x, origin.y + offset.y, origin.z + offset.z);
        }
        if (serverStateTicks >= getChargeDuration()) {
            beginFlight();
        }
    }

    private void beginFlight() {
        setState(SpearState.FLIGHT);
        Vec3 dir = getLaunchDirection().normalize();
        Vec3 velocity = dir.scale(FLIGHT_SPEED);
        setDeltaMovement(velocity);
        this.noPhysics = false;
        LivingEntity caster = getCaster();
        if (caster != null) {
            PlayerAnimatorCompat.playClip(caster, "LightningSpearRelease");
        }
    }

    private void tickFlightServer() {
        serverStateTicks++;
        entityData.set(DATA_STATE_TIME, serverStateTicks);
        if (serverStateTicks >= MAX_FLIGHT_TICKS) {
            triggerImpact();
        }
    }

    private void triggerImpact() {
        if (getState() == SpearState.IMPACT) {
            return;
        }
        setState(SpearState.IMPACT);
        setDeltaMovement(Vec3.ZERO);
        this.noPhysics = true;
        LivingEntity caster = getCaster();
        if (caster != null) {
            PlayerAnimatorCompat.playClip(caster, "LightningSpearImpact");
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        triggerImpact();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        triggerImpact();
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    private void tickImpactServer() {
        if (!impactSpawned) {
            impactSpawned = true;
            spawnImpactBurst();
        }
        serverStateTicks++;
        entityData.set(DATA_STATE_TIME, serverStateTicks);
        if (serverStateTicks >= IMPACT_LINGER_TICKS) {
            discard();
        }
    }

    private void spawnImpactBurst() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = position();
        for (int i = 0; i < 32; i++) {
            double angle = (Math.PI * 2 * i) / 32.0;
            double speed = 0.6 + random.nextDouble() * 0.3;
            double sx = Math.cos(angle) * speed;
            double sz = Math.sin(angle) * speed;
            serverLevel.sendParticles(ModParticles.LIGHTNING_SPARK.get(), pos.x, pos.y, pos.z, 2, sx * 0.15, 0.05, sz * 0.15, 0.0);
        }
        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        spawnGroundDecal(serverLevel);
    }

    private void spawnGroundDecal(ServerLevel serverLevel) {
        AreaEffectCloud cloud = new AreaEffectCloud(serverLevel, getX(), getY(), getZ());
        cloud.setWaitTime(0);
        cloud.setDuration(IMPACT_LINGER_TICKS);
        cloud.setRadius(3.0F);
        cloud.setRadiusPerTick(-0.08F);
        cloud.setFixedColor(0xF7E27A);
        cloud.setParticle(ParticleTypes.GLOW);
        cloud.setNoGravity(true);
        LivingEntity caster = getCaster();
        if (caster != null) {
            cloud.setOwner(caster);
        }
        serverLevel.addFreshEntity(cloud);
    }

    private void spawnClientEffects(SpearState state) {
        switch (state) {
            case CHARGING -> spawnChargeEffects();
            case FLIGHT -> spawnFlightTrail();
            case IMPACT -> spawnImpactResidue();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargeEffects() {
        int ticks = getVisualStateTime();
        int duration = Math.max(1, getChargeDuration());
        double progress = Math.min(1.0, ticks / (double) duration);
        int rings = 3;
        for (int ring = 0; ring < rings; ring++) {
            double radius = 0.25 + progress * 0.35 + ring * 0.1;
            double speed = (ring % 2 == 0 ? 1 : -1) * 0.4;
            for (int segment = 0; segment < 8; segment++) {
                double offset = (segment / 8.0) * Math.PI * 2;
                double time = (tickCount * 0.2) + offset + speed * ticks * 0.05;
                double x = getX() + Math.cos(time) * radius;
                double z = getZ() + Math.sin(time) * radius;
                double y = getY() + 0.15 * ring - 0.1;
                level().addParticle(ModParticles.LIGHTNING_SPARK.get(), x, y, z, 0.0, 0.02, 0.0);
                level().addParticle(ParticleTypes.GLOW, x, y, z, 0.0, 0.01, 0.0);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnFlightTrail() {
        spawnTrail(ModParticles.LIGHTNING_SPARK.get(), 4);
        spawnTrail(ParticleTypes.GLOW, 2);
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnImpactResidue() {
        double radius = 0.4 + random.nextDouble() * 0.2;
        for (int i = 0; i < 4; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            double y = getY() + 0.05;
            level().addParticle(ParticleTypes.GLOW, x, y, z, 0.0, 0.005, 0.0);
        }
    }

    private void spawnTrail(SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double sx = (random.nextDouble() - 0.5) * 0.1;
            double sy = (random.nextDouble() - 0.5) * 0.1;
            double sz = (random.nextDouble() - 0.5) * 0.1;
            level().addParticle(type, getX(), getY(), getZ(), sx, sy, sz);
        }
    }

    private void updateRotation() {
        Vec3 dir = getLaunchDirection();
        if (dir.lengthSqr() < 1.0E-4) {
            return;
        }
        Vec3 normalized = dir.normalize();
        double horiz = Math.sqrt(normalized.x * normalized.x + normalized.z * normalized.z);
        float yaw = (float) (Mth.atan2(normalized.z, normalized.x) * (180F / Math.PI)) - 90F;
        float pitch = (float) (-(Mth.atan2(normalized.y, horiz) * (180F / Math.PI)));
        setYRot(yaw);
        setXRot(pitch);
        yRotO = yaw;
        xRotO = pitch;
    }

    @Override
    protected void defineSynchedData() {\r\n        super.defineSynchedData();\r\n        entityData.define(DATA_STATE, SpearState.CHARGING.ordinal());
        entityData.define(DATA_CHARGE_DURATION, 20);
        entityData.define(DATA_STATE_TIME, 0);
        entityData.define(DATA_DIR_X, 0f);
        entityData.define(DATA_DIR_Y, 0f);
        entityData.define(DATA_DIR_Z, 0f);
        entityData.define(DATA_CASTER, Optional.empty());
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("state", getState().ordinal());
        tag.putInt("charge", getChargeDuration());
        tag.putInt("stateTicks", getVisualStateTime());
        Vec3 dir = getLaunchDirection();
        tag.putDouble("dirX", dir.x);
        tag.putDouble("dirY", dir.y);
        tag.putDouble("dirZ", dir.z);
        entityData.get(DATA_CASTER).ifPresent(uuid -> tag.putUUID("caster", uuid));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        int stateId = Mth.clamp(tag.getInt("state"), 0, SpearState.values().length - 1);
        setState(SpearState.values()[stateId]);
        setChargeDuration(tag.getInt("charge"));
        Vec3 dir = new Vec3(tag.getDouble("dirX"), tag.getDouble("dirY"), tag.getDouble("dirZ"));
        setLaunchDirection(dir);
        serverStateTicks = tag.getInt("stateTicks");
        entityData.set(DATA_STATE_TIME, serverStateTicks);
        if (tag.hasUUID("caster")) {
            entityData.set(DATA_CASTER, Optional.of(tag.getUUID("caster")));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setChargeDuration(int ticks) {
        entityData.set(DATA_CHARGE_DURATION, Math.max(1, ticks));
    }

    public int getChargeDuration() {
        return entityData.get(DATA_CHARGE_DURATION);
    }

    public void setLaunchDirection(Vec3 direction) {
        Vec3 normalized = direction.lengthSqr() > 0.001 ? direction.normalize() : new Vec3(0, 0, 1);
        entityData.set(DATA_DIR_X, (float) normalized.x);
        entityData.set(DATA_DIR_Y, (float) normalized.y);
        entityData.set(DATA_DIR_Z, (float) normalized.z);
    }

    public Vec3 getLaunchDirection() {
        return new Vec3(entityData.get(DATA_DIR_X), entityData.get(DATA_DIR_Y), entityData.get(DATA_DIR_Z));
    }

    public void setCaster(LivingEntity caster) {
        cachedCaster = caster;
        entityData.set(DATA_CASTER, Optional.of(caster.getUUID()));
    }

    @Nullable
    public LivingEntity getCaster() {
        if (cachedCaster != null && !cachedCaster.isRemoved()) {
            return cachedCaster;
        }
        Optional<UUID> uuid = entityData.get(DATA_CASTER);
        if (uuid.isEmpty()) {
            return null;
        }
        LivingEntity living = null;
        if (level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid.get());
            if (entity instanceof LivingEntity serverLiving) {
                living = serverLiving;
            }
        } else if (FMLEnvironment.dist.isClient()) {
            UUID casterId = uuid.get();
            LivingEntity[] holder = new LivingEntity[1];
            DistExecutor.safeRunWhenOn(Dist.CLIENT,
                    () -> () -> holder[0] = LightningSpearClientAccess.resolveCaster(casterId));
            living = holder[0];
        }
        if (living != null) {
            cachedCaster = living;
            return living;
        }
        return null;
    }

    private int getVisualStateTime() {
        return level().isClientSide ? entityData.get(DATA_STATE_TIME) : serverStateTicks;
    }

    private void setState(SpearState state) {
        serverStateTicks = 0;
        impactSpawned = false;
        entityData.set(DATA_STATE, state.ordinal());
        entityData.set(DATA_STATE_TIME, 0);
    }

    public SpearState getState() {
        int idx = Mth.clamp(entityData.get(DATA_STATE), 0, SpearState.values().length - 1);
        return SpearState.values()[idx];
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_STATE.equals(key)) {
            impactSpawned = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lightning_spear", 0, state -> software.bernie.geckolib.core.object.PlayState.CONTINUE.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geckoCache;
    }

    @Override
    public double getTick(Object object) {
        return tickCount;
    }

    private enum SpearState {
        CHARGING,
        FLIGHT,
        IMPACT
    }
}

