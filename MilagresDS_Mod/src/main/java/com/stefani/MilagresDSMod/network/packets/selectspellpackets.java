package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class selectspellpackets {
    @Nullable
    private final ResourceLocation spellId;

    public selectspellpackets(@Nullable ResourceLocation spellId) {
        this.spellId = spellId;
    }

    public selectspellpackets(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            this.spellId = buf.readResourceLocation();
        } else {
            this.spellId = null;
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (spellId != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(spellId);
        } else {
            buf.writeBoolean(false);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            player.getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(spells -> {
                if (spellId == null) {
                    spells.setEquippedSpell(null);
                    modpackets.sendSpellSnapshot(player);
                    return;
                }

                if (!spells.isUnlocked(spellId)) {
                    return;
                }

                var registry = spellregistry.REGISTRY.get();
                if (registry == null) {
                    return;
                }
                spell selectedSpell = registry.getValue(spellId);
                if (selectedSpell == null) {
                    return;
                }

                var attributesOptional = player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES);
                if (!attributesOptional.isPresent()) {
                    return;
                }
                var attributes = attributesOptional.orElseThrow(() -> new IllegalStateException("Missing attributes"));
                var requirements = selectedSpell.getRequirements();
                if (attributes.getLevel() < requirements.requiredLevel()
                        || attributes.getIntelligence() < requirements.intelligence()
                        || attributes.getFaith() < requirements.faith()
                        || attributes.getArcane() < requirements.arcane()) {
                    player.displayClientMessage(Component.translatable("msg.milagresdsmod.requirements_not_met"), true);
                    return;
                }

                spells.setEquippedSpell(selectedSpell);
                modpackets.sendSpellSnapshot(player);
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
