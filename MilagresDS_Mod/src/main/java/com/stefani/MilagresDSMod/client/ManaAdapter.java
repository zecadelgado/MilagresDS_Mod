package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.capability.playermana;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import net.minecraft.world.entity.player.Player;

/**
 * Adaptador que integra com o sistema de mana existente do projeto.
 * Delega todas as operações para a Capability de mana do jogador.
 */
public class ManaAdapter {

    /**
     * Obtém a quantidade atual de mana do jogador.
     * @param player O jogador
     * @return Mana atual, ou 0 se não disponível
     */
    public static int getCurrent(Player player) {
        if (player == null) {
            return 0;
        }
        return player.getCapability(playermanaprovider.PLAYER_MANA)
                .map(playermana::getMana)
                .orElse(0);
    }

    /**
     * Obtém a quantidade máxima de mana do jogador.
     * @param player O jogador
     * @return Mana máxima, ou 0 se não disponível
     */
    public static int getMax(Player player) {
        if (player == null) {
            return 0;
        }
        return player.getCapability(playermanaprovider.PLAYER_MANA)
                .map(playermana::getMaxMana)
                .orElse(0);
    }

    /**
     * Verifica se o jogador tem a quantidade especificada de mana disponível.
     * @param player O jogador
     * @param amount Quantidade a verificar
     * @return true se o jogador tem mana suficiente
     */
    public static boolean has(Player player, int amount) {
        if (player == null) {
            return false;
        }
        return player.getCapability(playermanaprovider.PLAYER_MANA)
                .map(mana -> mana.hasMana(amount))
                .orElse(false);
    }

    /**
     * Gasta a quantidade especificada de mana do jogador.
     * NOTA: Equipar feitiços NÃO gasta mana, apenas o lançamento dos feitiços.
     * Este método existe para uso futuro no sistema de lançamento.
     * @param player O jogador
     * @param amount Quantidade a gastar
     * @return true se a mana foi gasta com sucesso
     */
    public static boolean spend(Player player, int amount) {
        if (player == null || !has(player, amount)) {
            return false;
        }
        player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana -> {
            mana.consumeMana(amount);
        });
        return true;
    }
}
