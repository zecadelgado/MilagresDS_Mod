package com.stefani.MilagresDSMod.client.hud;

import com.stefani.MilagresDSMod.client.data.RunesClientCache;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public final class RunesOverlay {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.ROOT);

    private RunesOverlay() {
    }

    public static final IGuiOverlay HUD = (gui, graphics, partialTick, width, height) -> {
        if (!ModCommonConfig.SHOW_HUD.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        long active = RunesClientCache.activeRunes();
        long lost = RunesClientCache.lostRunes();
        int x = 10;
        int y = height - 72;

        Component activeLabel = Component.translatable("hud.milagresdsmod.runes.active", format(active));
        graphics.drawString(minecraft.font, activeLabel, x, y, 0xFFE2A412, true);
        y += 10;

        if (lost > 0L) {
            Component lostLabel = Component.translatable("hud.milagresdsmod.runes.lost", format(lost));
            graphics.drawString(minecraft.font, lostLabel, x, y, 0xFFAA3333, true);
            y += 10;

            Optional<GlobalPos> stain = RunesClientCache.bloodstain();
            if (stain.isPresent()) {
                Component detail = stainDetail(minecraft, stain.get());
                graphics.drawString(minecraft.font, detail, x, y, 0xFF777777, true);
            }
        }
    };

    private static Component stainDetail(Minecraft minecraft, GlobalPos pos) {
        Level level = minecraft.level;
        if (level != null && level.dimension() == pos.dimension() && minecraft.player != null) {
            Vec3 playerPos = minecraft.player.position();
            Vec3 stainCenter = Vec3.atCenterOf(pos.pos());
            double distance = Math.sqrt(stainCenter.distanceToSqr(playerPos));
            String formatted = String.format(Locale.ROOT, "%.1f", distance);
            return Component.translatable("hud.milagresdsmod.runes.stain.distance", formatted);
        }
        String dimensionName = pos.dimension().location().toString();
        return Component.translatable("hud.milagresdsmod.runes.stain.dimension", dimensionName);
    }

    private static String format(long value) {
        return NUMBER_FORMAT.format(Math.max(0L, value));
    }
}
