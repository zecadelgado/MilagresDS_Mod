package com.stefani.MilagresDSMod;
import com.stefani.MilagresDSMod.registry.spellregistry;
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
    @Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
    public static class ClientModEvents{
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(modkeybindings.OPEN_SPELL_MENU);
            event.register(modkeybindings.CAST_SPELL);
        }
        }
    }
