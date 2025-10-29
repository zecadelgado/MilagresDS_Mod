package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.magic.spell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class castspellpackets {

    public castspellpackets() {}

    public castspellpackets(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Executar no thread do servidor
            ServerPlayer player = context.getSender();
            if (player == null) return;

            // Obter a magia equipada do jogador
            player.getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(spells -> {
                spell equippedSpell = spells.getEquippedSpell();
                if (equippedSpell == null || spells.isOnCooldown(equippedSpell, player.level())) {
                    return;
                }

                player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana -> {
                    if (mana.hasMana(equippedSpell.getManaCost())) {
                        mana.consumeMana(equippedSpell.getManaCost());
                        equippedSpell.cast(player, player.level());
                        spells.setCooldown(equippedSpell, player.level());
                    }
                });
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
