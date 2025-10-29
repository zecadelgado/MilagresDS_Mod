package com.stefani.MilagresDSMod.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncManaS2CPacket {
    private final int current;
    private final int max;

    public SyncManaS2CPacket(int current, int max) {
        this.current = current;
        this.max = max;
    }

    public static void encode(SyncManaS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.current);
        buf.writeVarInt(msg.max);
    }

    public static SyncManaS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncManaS2CPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(SyncManaS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getCapability(com.stefani.MilagresDSMod.capability.playermanaprovider.PLAYER_MANA)
                    .ifPresent(m -> {
                        m.setMaxMana(msg.max);
                        m.setMana(msg.current);
                    });
                try {
                    com.stefani.MilagresDSMod.client.MagicStats.setClientMana(msg.current, msg.max);
                } catch (Throwable ignored) {}
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
