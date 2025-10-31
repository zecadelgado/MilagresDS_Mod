package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.client.MagicStats;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SyncMemorizedSpellsS2CPacket {
    private final int slots;
    private final List<ResourceLocation> memorised;

    public SyncMemorizedSpellsS2CPacket(int slots, List<ResourceLocation> memorised) {
        this.slots = Math.max(1, slots);
        List<ResourceLocation> copy = new ArrayList<>(Collections.nCopies(this.slots, null));
        for (int i = 0; i < this.slots; i++) {
            ResourceLocation id = i < memorised.size() ? memorised.get(i) : null;
            copy.set(i, id);
        }
        this.memorised = copy;
    }

    public static void encode(SyncMemorizedSpellsS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.slots);
        buffer.writeVarInt(packet.memorised.size());
        for (ResourceLocation id : packet.memorised) {
            if (id != null) {
                buffer.writeBoolean(true);
                buffer.writeResourceLocation(id);
            } else {
                buffer.writeBoolean(false);
            }
        }
    }

    public static SyncMemorizedSpellsS2CPacket decode(FriendlyByteBuf buffer) {
        int slots = buffer.readVarInt();
        int size = buffer.readVarInt();
        List<ResourceLocation> memorised = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (buffer.readBoolean()) {
                memorised.add(buffer.readResourceLocation());
            } else {
                memorised.add(null);
            }
        }
        return new SyncMemorizedSpellsS2CPacket(slots, memorised);
    }

    public static void handle(SyncMemorizedSpellsS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return;
            }
            MagicStats.get().syncFromServer(packet.slots, packet.memorised);
            minecraft.player.getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(spells -> {
                spells.setSlotCount(packet.slots);
                spells.setMemorizedSlots(packet.memorised);
            });
        });
        context.setPacketHandled(true);
    }
}
