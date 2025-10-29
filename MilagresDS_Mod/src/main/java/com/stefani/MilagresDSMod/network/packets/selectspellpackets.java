package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

            spell selectedSpell = null;
            if (spellId != null) {
                selectedSpell = spellregistry.REGISTRY.get().getValue(spellId);
                if (selectedSpell == null) {
                    return;
                }
            }

            spell finalSelectedSpell = selectedSpell;
            player.getCapability(playerspellsprovider.PLAYER_SPELLS)
                    .ifPresent(spells -> spells.setEquippedSpell(finalSelectedSpell));
        });
        context.setPacketHandled(true);
        return true;
    }
}
