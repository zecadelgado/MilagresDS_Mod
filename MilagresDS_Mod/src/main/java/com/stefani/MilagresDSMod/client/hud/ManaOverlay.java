package com.stefani.MilagresDSMod.client.hud;

import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class ManaOverlay {
    private static final int BAR_WIDTH = 102;
    private static final int BAR_HEIGHT = 10;

    private ManaOverlay() {
    }

    public static final IGuiOverlay HUD = (gui, graphics, partialTick, width, height) -> {
        if (!ModCommonConfig.SHOW_HUD.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        minecraft.player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana -> {
            int max = mana.getMaxMana();
            if (max <= 0) {
                return;
            }

            int current = Math.max(0, mana.getMana());
            int x = 10;
            int y = height - 54;

            int innerWidth = BAR_WIDTH - 2;
            int filled = Math.max(0, Math.min(innerWidth, (int) Math.round((current / (double) max) * innerWidth)));

            graphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xAA000000);
            graphics.fill(x + 1, y + 1, x + 1 + innerWidth, y + BAR_HEIGHT - 1, 0xFF1B2840);
            if (filled > 0) {
                graphics.fill(x + 1, y + 1, x + 1 + filled, y + BAR_HEIGHT - 1, 0xFF1F6FEB);
            }

            Component label = Component.translatable("hud.milagresdsmod.mana", current, max);
            graphics.drawString(minecraft.font, label, x, y - 9, 0x66C2FF, true);
        });
    };
}
