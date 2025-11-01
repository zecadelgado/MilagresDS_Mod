package com.stefani.MilagresDSMod;

import com.mojang.logging.LogUtils;
import com.stefani.MilagresDSMod.block.ModBlocks;
import com.stefani.MilagresDSMod.client.ClientSetup;
import com.stefani.MilagresDSMod.client.keybindings.ModKeyBindings;
import com.stefani.MilagresDSMod.commands.VisualTestCommands;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.item.ModItems;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.BlockRegistry;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;
import org.slf4j.Logger;

@Mod(MilagresDSMod.MODID)
public class MilagresDSMod {
    public static final String MODID = "milagresdsmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MilagresDSMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        spellregistry.SPELLS.register(modEventBus);
        BlockRegistry.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        EntityRegistry.REGISTRY.register(modEventBus);
        ParticleRegistry.REGISTRY.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);

        if (ModList.get().isLoaded("geckolib")) {
            GeckoLib.initialize();
        }

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        if (Dist.CLIENT.isClient()) {
            ClientSetup.init(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        modpackets.register();
    }

    private void onRegisterCommands(RegisterCommandsEvent evt) {
        VisualTestCommands.register(evt.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.OPEN_SPELL_MENU);
            event.register(ModKeyBindings.CAST_SPELL);
            event.register(ModKeyBindings.OPEN_ATTRIBUTES);
        }
    }
}
