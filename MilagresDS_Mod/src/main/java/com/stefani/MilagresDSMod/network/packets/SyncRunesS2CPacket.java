package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.client.data.RunesClientCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncRunesS2CPacket {
    private final long activeRunes;
    private final long lostRunes;

    public SyncRunesS2CPacket(long activeRunes, long lostRunes) {
        this.activeRunes = activeRunes;
        this.lostRunes = lostRunes;
    }

    public static void encode(SyncRunesS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarLong(packet.activeRunes);
        buffer.writeVarLong(packet.lostRunes);
    }

    public static SyncRunesS2CPacket decode(FriendlyByteBuf buffer) {
        long active = buffer.readVarLong();
        long lost = buffer.readVarLong();
        return new SyncRunesS2CPacket(active, lost);
    }

    public static void handle(SyncRunesS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> RunesClientCache.updateRunes(packet.activeRunes, packet.lostRunes));
        context.setPacketHandled(true);
    }
}
