package com.stefani.MilagresDSMod.server.tick;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.network.modpackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ManaTickHandler {
    private ManaTickHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana -> {
            int oldMana = mana.getMana();
            int oldMax = mana.getMaxMana();

            int configuredMax = ModCommonConfig.MAX_MANA.get();
            if (oldMax != configuredMax) {
                mana.setMaxMana(configuredMax);
            }

            int regen = ModCommonConfig.MANA_REGEN_PER_TICK.get();
            if (regen > 0 && mana.getMana() < mana.getMaxMana()) {
                mana.setMana(Math.min(mana.getMaxMana(), mana.getMana() + regen));
            }

            int newMana = mana.getMana();
            int newMax = mana.getMaxMana();
            boolean changed = newMana != oldMana || newMax != oldMax;
            long gameTime = serverPlayer.level().getGameTime();
            if (changed || gameTime % 10L == 0L) {
                modpackets.sendManaSync(serverPlayer, newMana, newMax);
            }
        });
    }
}
