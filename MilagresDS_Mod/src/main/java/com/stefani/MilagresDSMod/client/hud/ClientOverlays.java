package com.stefani.MilagresDSMod.client.hud;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientOverlays {
    private ClientOverlays() {
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("mana", ManaOverlay.HUD);
        event.registerAbove("mana", "runes", RunesOverlay.HUD);
    }
}
