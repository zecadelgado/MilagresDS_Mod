package com.stefani;
import com.Stefani.milagresds.registry.SpellRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingConext;
@Mod(MilagresDSMod.MODID)
public class MilagresDSMod {
    public static final String MODID = "milagresdsmod";

    public MilagresDSMod() {
        IEventBus eventBus = FMLJavaModLoadingConext.get().getModEventBus();

        SpellRegistry.SPELLS.registrer(mod.EventBus);
    }
}
{
}
