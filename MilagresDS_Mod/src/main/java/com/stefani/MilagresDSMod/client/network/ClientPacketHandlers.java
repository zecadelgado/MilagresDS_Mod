package com.stefani.MilagresDSMod.client.network;

import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.client.MagicStats;
import com.stefani.MilagresDSMod.client.gui.SpellMemorizeScreen;
import com.stefani.MilagresDSMod.client.lighting.DynamicLightClient;
import com.stefani.MilagresDSMod.client.renderer.LightningSpearRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public final class ClientPacketHandlers {
    private static LightningSpearRenderer LightningSpearRenderer;

    private ClientPacketHandlers() {}

    public static void applyMemorizedSpells(final int slots, final List<ResourceLocation> memorised) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        MagicStats.get().syncFromServer(slots, memorised);
        minecraft.player.getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(spells -> {
            spells.setSlotCount(slots);
            spells.setMemorizedSlots(memorised);
        });
    }

    public static void applyManaSync(final int mana, final int maxMana) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        minecraft.player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(storage -> {
            storage.setMaxMana(maxMana);
            storage.setMana(mana);
        });
        MagicStats.setClientMana(mana, maxMana);
    }

    public static void applySpellSelectionResult(final boolean success, @Nullable final ResourceLocation equippedSpell) {
        MagicStats magicStats = MagicStats.get();
        if (equippedSpell != null) {
            magicStats.equipSpell(0, equippedSpell);
        } else {
            magicStats.clearSlot(0);
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof SpellMemorizeScreen screen) {
            screen.onSpellSelectionResult(success, equippedSpell);
        }
    }

    public static void addSpellLight(final int entityId, final int rgb, final float radius, final int durationTicks) {
        DynamicLightClient.addLight(entityId, rgb, radius, durationTicks);
    }

    public static void scheduleLightningSpearLight(final int entityId, final int casterId, final int rgb,
                                                   final float radius, final int durationTicks) {
        LightningSpearRenderer.schedule(entityId, casterId, rgb, radius, durationTicks);
    }
}
