package com.stefani.milagresds_mod;
import com.stefani.milagresds_mod.registry.spellregistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
@Mod(MilagresDSMod.MODID)
public class MilagresDSMod {
    public static final String MODID = "milagresdsmod";

    public MilagresDSMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        spellregistry.SPELLS.register(modEventBus);
    }
}
