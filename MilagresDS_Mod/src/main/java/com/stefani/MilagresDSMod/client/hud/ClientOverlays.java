package com.stefani.MilagresDSMod.client.hud;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientOverlays {
    private ClientOverlays() {}

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "mana", ManaOverlay.HUD);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "runes", RunesOverlay.HUD);
    }
}
