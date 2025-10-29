package com.stefani.MilagresDSMod.capability;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class modcapabilities {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(com.stefani.MilagresDSMod.capability.playermana.class);
        event.register(com.stefani.MilagresDSMod.capability.playerspells.class);
    }
}
