package com.stefani.MilagresDSMod.magic.visual.backend.lighting;

import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.network.packets.LightningSpearLightS2CPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

public final class DynamicLightDispatcher {
    private DynamicLightDispatcher() {}

    public static void emit(ServerLevel level, Entity focus, @Nullable LivingEntity caster, int rgb, float radius, int durationTicks) {
        if (durationTicks <= 0) {
            return;
        }
        int casterId = caster != null ? caster.getId() : -1;
        LightningSpearLightS2CPacket packet = new LightningSpearLightS2CPacket(focus.getId(), casterId, rgb, radius, durationTicks);
        modpackets.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> focus), packet);
    }
}
