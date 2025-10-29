package com.stefani.MilagresDSMod.events;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    private static final ResourceLocation PLAYER_MANA_ID = new ResourceLocation(MilagresDSMod.MODID, "player_mana");
    private static final ResourceLocation PLAYER_SPELLS_ID = new ResourceLocation(MilagresDSMod.MODID, "player_spells");

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(playermanaprovider.PLAYER_MANA).isPresent()) {
                event.addCapability(PLAYER_MANA_ID, new playermanaprovider());
            }
            if (!event.getObject().getCapability(playerspellsprovider.PLAYER_SPELLS).isPresent()) {
                event.addCapability(PLAYER_SPELLS_ID, new playerspellsprovider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(playermanaprovider.PLAYER_MANA).ifPresent(oldMana ->
                event.getEntity().getCapability(playermanaprovider.PLAYER_MANA).ifPresent(newMana ->
                        newMana.setMana(oldMana.getMana())));

        event.getOriginal().getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(oldSpells ->
                event.getEntity().getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(newSpells ->
                        newSpells.setEquippedSpell(oldSpells.getEquippedSpell())));

        event.getOriginal().invalidateCaps();
    }
}
