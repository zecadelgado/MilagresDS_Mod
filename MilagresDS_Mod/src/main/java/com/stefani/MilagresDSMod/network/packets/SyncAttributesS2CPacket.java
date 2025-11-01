package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.client.data.AttributesClientCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAttributesS2CPacket {
    private final int level;
    private final long storedRunes;
    private final int points;
    private final int intelligence;
    private final int faith;
    private final int arcane;
    private final int strength;
    private final int dexterity;
    private final int constitution;

    public SyncAttributesS2CPacket(int level, long storedRunes, int points, int intelligence, int faith, int arcane,
                                   int strength, int dexterity, int constitution) {
        this.level = level;
        this.storedRunes = storedRunes;
        this.points = points;
        this.intelligence = intelligence;
        this.faith = faith;
        this.arcane = arcane;
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
    }

    public static void encode(SyncAttributesS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.level);
        buffer.writeVarLong(packet.storedRunes);
        buffer.writeVarInt(packet.points);
        buffer.writeVarInt(packet.intelligence);
        buffer.writeVarInt(packet.faith);
        buffer.writeVarInt(packet.arcane);
        buffer.writeVarInt(packet.strength);
        buffer.writeVarInt(packet.dexterity);
        buffer.writeVarInt(packet.constitution);
    }

    public static SyncAttributesS2CPacket decode(FriendlyByteBuf buffer) {
        int level = buffer.readVarInt();
        long storedRunes = buffer.readVarLong();
        int points = buffer.readVarInt();
        int intelligence = buffer.readVarInt();
        int faith = buffer.readVarInt();
        int arcane = buffer.readVarInt();
        int strength = buffer.readVarInt();
        int dexterity = buffer.readVarInt();
        int constitution = buffer.readVarInt();
        return new SyncAttributesS2CPacket(level, storedRunes, points, intelligence, faith, arcane, strength, dexterity, constitution);
    }

    public static void handle(SyncAttributesS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> AttributesClientCache.update(packet.level, packet.storedRunes, packet.points,
                packet.intelligence, packet.faith, packet.arcane,
                packet.strength, packet.dexterity, packet.constitution));
        context.setPacketHandled(true);
    }
}
