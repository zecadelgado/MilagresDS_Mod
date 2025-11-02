package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.ClientSetup;
import com.stefani.MilagresDSMod.client.keybindings.ModKeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MilagresDSModClientEvents {

    private MilagresDSModClientEvents() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // TODO: coloque aqui tudo que era CLIENT no constructor:
        // - Entity renderers (EntityRenderers.register(...))
        // - Item/Block render layers
        // - Screens (MenuScreens.register(...))
        // - KeyMappings, etc.
        //
        // IMPORTANTE: só use classes do lado cliente aqui.
        ClientSetup.init(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onKeyRegister(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.OPEN_SPELL_MENU);
        event.register(ModKeyBindings.CAST_SPELL);
        event.register(ModKeyBindings.OPEN_ATTRIBUTES);
    }
}
