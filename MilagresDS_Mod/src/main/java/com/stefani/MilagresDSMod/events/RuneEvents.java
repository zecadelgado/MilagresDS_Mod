package com.stefani.MilagresDSMod.events;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.util.RuneRewardCalculator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID)
public final class RuneEvents {
    private RuneEvents() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel)) {
            return;
        }

        DamageSource source = event.getSource();
        if (source.getEntity() instanceof ServerPlayer killer && killer != victim) {
            long reward = RuneRewardCalculator.rewardFor(victim);
            if (reward > 0L) {
                killer.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
                    attributes.addXp(reward);
                    modpackets.sendAttributesSync(killer, attributes);
                });
            }
        }
    }
}
