package com.stefani.MilagresDSMod;

import com.mojang.logging.LogUtils;
import com.stefani.MilagresDSMod.block.ModBlocks;
import com.stefani.MilagresDSMod.commands.VisualTestCommands;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.item.ModItems;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.BlockRegistry;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import com.stefani.MilagresDSMod.registry.ParticleRegistry;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MilagresDSMod.MODID)
public class MilagresDSMod {
    public static final String MODID = "milagresdsmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MilagresDSMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registries comuns aqui (Items, Blocks, Entities, Menus, Creative Tabs via DeferredRegister etc.)
        spellregistry.SPELLS.register(modBus);
        BlockRegistry.register(modBus);
        ModBlocks.register(modBus);
        ModItems.ITEMS.register(modBus);
        EntityRegistry.REGISTRY.register(modBus);
        ParticleRegistry.REGISTRY.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);

        // GeckoLib é safe no common
        software.bernie.geckolib.GeckoLib.initialize();

        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Nada de net.minecraft.client.* aqui
        modpackets.register();
    }

    private void onRegisterCommands(RegisterCommandsEvent evt) {
        VisualTestCommands.register(evt.getDispatcher());
    }
}
