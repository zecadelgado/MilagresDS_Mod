package com.stefani.MilagresDSMod.client.keybindings;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.gui.SpellMemorizeScreen;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.network.packets.castspellpackets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class keyinputhandle {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        // Abrir menu de memorização de magias (somente cliente)
        if (ModKeyBindings.OPEN_SPELL_MENU.consumeClick()) {
            mc.setScreen(new SpellMemorizeScreen());
        }

        // Lançar magia
        if (ModKeyBindings.CAST_SPELL.consumeClick()) {
            modpackets.sendToServer(new castspellpackets());
        }
    }
}
