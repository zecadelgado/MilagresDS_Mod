package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.client.data.AttributesClientCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAttributesS2CPacket {
    private final int level;
    private final long xp;
    private final int points;
    private final int intelligence;
    private final int faith;
    private final int arcane;

    public SyncAttributesS2CPacket(int level, long xp, int points, int intelligence, int faith, int arcane) {
        this.level = level;
        this.xp = xp;
        this.points = points;
        this.intelligence = intelligence;
        this.faith = faith;
        this.arcane = arcane;
    }

    public static void encode(SyncAttributesS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.level);
        buffer.writeVarLong(packet.xp);
        buffer.writeVarInt(packet.points);
        buffer.writeVarInt(packet.intelligence);
        buffer.writeVarInt(packet.faith);
        buffer.writeVarInt(packet.arcane);
    }

    public static SyncAttributesS2CPacket decode(FriendlyByteBuf buffer) {
        int level = buffer.readVarInt();
        long xp = buffer.readVarLong();
        int points = buffer.readVarInt();
        int intelligence = buffer.readVarInt();
        int faith = buffer.readVarInt();
        int arcane = buffer.readVarInt();
        return new SyncAttributesS2CPacket(level, xp, points, intelligence, faith, arcane);
    }

    public static void handle(SyncAttributesS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> AttributesClientCache.update(packet.level, packet.xp, packet.points,
                packet.intelligence, packet.faith, packet.arcane));
        context.setPacketHandled(true);
    }
}
