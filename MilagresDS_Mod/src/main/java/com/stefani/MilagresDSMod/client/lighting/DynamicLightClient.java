package com.stefani.MilagresDSMod.client.lighting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class DynamicLightClient {
    private static final Map<Integer, ActiveLight> ACTIVE = new HashMap<>();

    private DynamicLightClient() {}

    public static void init() {}

    public static void addLight(int entityId, int rgb, float radius, int durationTicks) {
        ACTIVE.compute(entityId, (id, existing) -> {
            if (existing == null) {
                return new ActiveLight(entityId, radius, durationTicks);
            }
            existing.refresh(radius, durationTicks);
            return existing;
        });
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            clearLights(null);
            return;
        }
        Iterator<Map.Entry<Integer, ActiveLight>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ActiveLight> entry = it.next();
            ActiveLight light = entry.getValue();
            if (!light.tick(level)) {
                light.clear(level);
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level) {
            clearLights(level);
        }
    }

    private static void clearLights(ClientLevel level) {
        if (level != null) {
            for (ActiveLight light : ACTIVE.values()) {
                light.clear(level);
            }
        }
        ACTIVE.clear();
    }

    private static final class ActiveLight {
        private final int entityId;
        private float radius;
        private int duration;
        private final Set<BlockPos> placed = new HashSet<>();

        private ActiveLight(int entityId, float radius, int duration) {
            this.entityId = entityId;
            this.radius = radius;
            this.duration = Math.max(duration, 20);
        }

        private void refresh(float radius, int duration) {
            this.radius = Math.max(this.radius, radius);
            this.duration = Math.max(this.duration, duration);
        }

        private boolean tick(ClientLevel level) {
            Entity entity = level.getEntity(entityId);
            if (entity == null || !entity.isAlive()) {
                duration--;
                return duration > 0;
            }
            duration = Math.max(duration, 10);
            placeLight(level, entity);
            return true;
        }

        private void placeLight(ClientLevel level, Entity entity) {
            Set<BlockPos> updated = new HashSet<>();
            double height = entity.getY() + entity.getBbHeight() * 0.5;
            BlockPos center = BlockPos.containing(entity.getX(), height, entity.getZ());
            int lightLevel = Math.max(10, Math.min(15, Math.round(radius * 2.4f)));
            if (applyLight(level, center, lightLevel)) {
                updated.add(center.immutable());
            }
            int samples = Math.max(6, Math.round(radius * 2.5f));
            double dist = Math.max(1.5, radius * 0.65);
            for (int i = 0; i < samples; i++) {
                double ang = (Math.PI * 2.0 / samples) * i;
                double x = entity.getX() + Math.cos(ang) * dist;
                double z = entity.getZ() + Math.sin(ang) * dist;
                BlockPos pos = BlockPos.containing(x, entity.getY() + 0.5, z);
                if (applyLight(level, pos, lightLevel)) {
                    updated.add(pos.immutable());
                }
                BlockPos above = pos.above();
                if (applyLight(level, above, Math.max(8, lightLevel - 2))) {
                    updated.add(above.immutable());
                }
            }
            for (BlockPos previous : new HashSet<>(placed)) {
                if (!updated.contains(previous)) {
                    clearBlock(level, previous);
                    placed.remove(previous);
                }
            }
            placed.addAll(updated);
        }

        private boolean applyLight(ClientLevel level, BlockPos pos, int levelValue) {
            if (!level.hasChunkAt(pos)) {
                return false;
            }
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(Blocks.LIGHT)) {
                return false;
            }
            BlockState newState = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, levelValue);
            level.setBlock(pos, newState, 3);
            return true;
        }

        private void clearBlock(ClientLevel level, BlockPos pos) {
            if (!level.hasChunkAt(pos)) {
                return;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.LIGHT)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        private void clear(ClientLevel level) {
            for (BlockPos pos : placed) {
                clearBlock(level, pos);
            }
            placed.clear();
        }
    }
}
