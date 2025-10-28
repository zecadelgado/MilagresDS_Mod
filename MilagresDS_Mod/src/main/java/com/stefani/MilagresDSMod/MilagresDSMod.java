package com.stefani.MilagresDSMod;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
@Mod(MilagresDSMod.MODID)
public class MilagresDSMod {
    public static final String MODID = "milagresdsmod";

    public MilagresDSMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.register(spellregistry.SPELLS);
    }
}
