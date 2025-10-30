package com.stefani.MilagresDSMod.events;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(playermanaprovider.ID, new playermanaprovider());
            event.addCapability(playerspellsprovider.ID, new playerspellsprovider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            modpackets.sendManaSync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        boolean wasDeath = event.isWasDeath();

        if (wasDeath) {
            event.getOriginal().reviveCaps();
        }

        event.getEntity().getCapability(playermanaprovider.PLAYER_MANA).ifPresent(newMana ->
                event.getOriginal().getCapability(playermanaprovider.PLAYER_MANA).ifPresent(oldMana ->
                        newMana.deserializeNBT(oldMana.serializeNBT())));

        event.getEntity().getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(newSpells ->
                event.getOriginal().getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(oldSpells ->
                        newSpells.deserializeNBT(oldSpells.serializeNBT())));

        if (wasDeath) {
            event.getOriginal().invalidateCaps();
        }

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            modpackets.sendManaSync(serverPlayer);
        }
    }
}
