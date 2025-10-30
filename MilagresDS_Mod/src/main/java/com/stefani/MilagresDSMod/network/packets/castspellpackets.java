package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.network.modpackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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

                var attributesOptional = player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES);
                if (!attributesOptional.isPresent()) {
                    return;
                }
                var attributes = attributesOptional.orElseThrow(() -> new IllegalStateException("Missing attributes"));
                var requirements = equippedSpell.getRequirements();
                if (attributes.getLevel() < requirements.requiredLevel()
                        || attributes.getIntelligence() < requirements.intelligence()
                        || attributes.getFaith() < requirements.faith()
                        || attributes.getArcane() < requirements.arcane()) {
                    player.displayClientMessage(Component.translatable("msg.milagresdsmod.requirements_not_met"), true);
                    return;
                }

                long now = player.level().getGameTime();
                int minTicks = ModCommonConfig.MIN_TICKS_BETWEEN_CASTS.get();
                if (minTicks > 0 && now - spells.getLastCastTick() < minTicks) {
                    return;
                }

                player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana -> {
                    int manaCost = equippedSpell.getManaCost();
                    if (mana.hasMana(manaCost)) {
                        mana.consumeMana(manaCost);
                        equippedSpell.cast(player, player.level());
                        spells.setCooldown(equippedSpell, player.level());
                        spells.setLastCastTick(now);
                        modpackets.sendManaSync(player, mana.getMana(), mana.getMaxMana());
                    }
                });
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
