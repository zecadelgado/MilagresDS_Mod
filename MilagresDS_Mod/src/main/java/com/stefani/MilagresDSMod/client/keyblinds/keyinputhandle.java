package com.stefani.MilagresDSMod.client.keyblinds;
import com.stefani.MilagresDSMod.client.gui.spellselection;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.network.packets.castspellpackets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class keyinputhandle {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // Abrir menu de magias
            if (modkeybindings.OPEN_SPELL_MENU.consumeClick()) {
                mc.setScreen(new spellselection());
            }

            // Lançar magia
            if (modkeybindings.CAST_SPELL.consumeClick()) {
                modpackets.sendToServer(new castspellpackets());
            }
        }
    }
