package com.stefani.MilagresDSMod.network;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.network.packets.castspellpackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public class modpackets {
    private static final String PROTOCOL_VERSION = "1";
    private static final ResourceLocation CHANNEL_ID = new ResourceLocation(MilagresDSMod.MODID, "main");
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_ID,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.messageBuilder(castspellpackets.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(castspellpackets::toBytes)
                .decoder(castspellpackets::new)
                .consumerMainThread((packet, supplier) -> packet.handle(supplier))
                .add();
    }

    public static void sendToServer(castspellpackets packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToServer() {
        sendToServer(new castspellpackets());
    }
}
