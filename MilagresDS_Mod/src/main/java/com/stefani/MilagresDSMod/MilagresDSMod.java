package com.stefani.MilagresDSMod;

import com.stefani.MilagresDSMod.client.keybindings.ModKeyBindings;
import com.stefani.MilagresDSMod.config.ModConfig;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MilagresDSMod.MODID)
public class MilagresDSMod {
    public static final String MODID = "milagresdsmod";

    public MilagresDSMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        spellregistry.SPELLS.register(modEventBus);
        ModConfig.register();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        modpackets.register();
    }

    @Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.OPEN_SPELL_MENU);
            event.register(ModKeyBindings.CAST_SPELL);
        }
    }
}
