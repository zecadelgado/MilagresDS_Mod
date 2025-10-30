package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.client.MagicStats;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncManaS2CPacket {
    private final int mana;
    private final int maxMana;

    public SyncManaS2CPacket(int mana, int maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public static void encode(SyncManaS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.mana);
        buffer.writeVarInt(packet.maxMana);
    }

    public static SyncManaS2CPacket decode(FriendlyByteBuf buffer) {
        int mana = buffer.readVarInt();
        int maxMana = buffer.readVarInt();
        return new SyncManaS2CPacket(mana, maxMana);
    }

    public static void handle(SyncManaS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return;
            }
            minecraft.player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana -> {
                mana.setMaxMana(packet.maxMana);
                mana.setMana(packet.mana);
            });
            MagicStats.setClientMana(packet.mana, packet.maxMana);
        });
        context.setPacketHandled(true);
    }
}
