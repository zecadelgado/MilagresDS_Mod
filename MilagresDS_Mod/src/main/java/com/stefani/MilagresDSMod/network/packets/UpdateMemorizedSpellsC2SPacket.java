package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdateMemorizedSpellsC2SPacket {
    private final List<ResourceLocation> slots;

    public UpdateMemorizedSpellsC2SPacket(List<ResourceLocation> slots) {
        this.slots = new ArrayList<>(slots);
    }

    public UpdateMemorizedSpellsC2SPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<ResourceLocation> loaded = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (buf.readBoolean()) {
                loaded.add(buf.readResourceLocation());
            } else {
                loaded.add(null);
            }
        }
        this.slots = loaded;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(slots.size());
        for (ResourceLocation id : slots) {
            if (id != null) {
                buf.writeBoolean(true);
                buf.writeResourceLocation(id);
            } else {
                buf.writeBoolean(false);
            }
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
                spells.setMemorizedSlots(slots);
                modpackets.sendSpellSnapshot(player, spells);
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
