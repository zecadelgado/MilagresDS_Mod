package com.stefani.MilagresDSMod.magic.visual.lightning.client;

import org.joml.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public final class LightningSpearLightClientHandler {
    private static final Map<Integer, LightInstance> ACTIVE = new ConcurrentHashMap<>();
    private static boolean registered;

    private LightningSpearLightClientHandler() {}

    public static void init() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.addListener(LightningSpearLightClientHandler::onClientTick);
    }

    public static void schedule(int entityId, int casterId, int rgb, float radius, int durationTicks) {
        ACTIVE.put(entityId, new LightInstance(entityId, casterId, rgb, radius, durationTicks));
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            ACTIVE.clear();
            return;
        }
        Iterator<Map.Entry<Integer, LightInstance>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            LightInstance instance = iterator.next().getValue();
            if (--instance.ticksRemaining <= 0) {
                iterator.remove();
                continue;
            }
            tickLight(level, instance);
        }
    }

    private static void tickLight(ClientLevel level, LightInstance instance) {
        Entity focus = level.getEntity(instance.entityId);
        if (focus != null) {
            emitAura(level, focus.position().add(0, focus.getBbHeight() * 0.5, 0), instance);
        }
        if (instance.casterId >= 0) {
            Entity caster = level.getEntity(instance.casterId);
            if (caster != null) {
                emitAura(level, caster.position().add(0, caster.getBbHeight() * 0.4, 0), instance);
            }
        }
    }

    private static void emitAura(ClientLevel level, Vec3 center, LightInstance instance) {
        float[] baseColor = unpackColor(instance.rgb);
        DustColorTransitionOptions dust = new DustColorTransitionOptions(new Vector3f(baseColor[0], baseColor[1], baseColor[2]), new Vector3f(1.0F, 0.94F, 0.6F), 1.25F);
        for (int i = 0; i < 6; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double radial = instance.radius * (0.3 + level.random.nextDouble() * 0.35);
            double height = (level.random.nextDouble() - 0.5) * 0.4;
            double x = center.x + Math.cos(angle) * radial;
            double y = center.y + height;
            double z = center.z + Math.sin(angle) * radial;
            level.addParticle(dust, x, y, z, 0.0, 0.01, 0.0);
            level.addParticle(ParticleTypes.GLOW, x, y, z, 0.0, 0.015, 0.0);
        }
    }

    private static float[] unpackColor(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        return new float[]{r, g, b};
    }

    private static final class LightInstance {
        final int entityId;
        final int casterId;
        final int rgb;
        final float radius;
        int ticksRemaining;

        private LightInstance(int entityId, int casterId, int rgb, float radius, int ticksRemaining) {
            this.entityId = entityId;
            this.casterId = casterId;
            this.rgb = rgb;
            this.radius = radius;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
