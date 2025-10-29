package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.capability.playermana;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import net.minecraft.client.player.LocalPlayer;

/**
 * Simple helper used by client screens to query the player's mana capability
 * without exposing capability plumbing everywhere.
 */
public class ManaAdapter {
    public int getCurrent(LocalPlayer player) {
        if (player == null) {
            return 0;
        }
        return player.getCapability(playermanaprovider.PLAYER_MANA)
                .map(playermana::getMana)
                .orElse(0);
    }

    public int getMax(LocalPlayer player) {
        if (player == null) {
            return 0;
        }
        return player.getCapability(playermanaprovider.PLAYER_MANA)
                .map(playermana::getMaxMana)
                .orElse(0);
    }

    public boolean has(LocalPlayer player, int amount) {
        if (player == null) {
            return false;
        }
        return player.getCapability(playermanaprovider.PLAYER_MANA)
                .map(mana -> mana.hasMana(amount))
                .orElse(false);
    }

    public void spend(LocalPlayer player, int amount) {
        if (player == null || amount <= 0) {
            return;
        }
        player.getCapability(playermanaprovider.PLAYER_MANA)
                .ifPresent(mana -> mana.consumeMana(amount));
    }
}
