package com.stefani.MilagresDSMod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class GraceSiteClientHooks {
    private GraceSiteClientHooks() {
    }

    public static void openGraceScreen(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            minecraft.setScreen(new GraceAttributesScreen(pos));
        }
    }
}
