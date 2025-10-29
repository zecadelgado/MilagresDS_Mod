package com.stefani.MilagresDSMod.network;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.network.packets.castspellpackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class modpackets {

    private static final String PROTOCOL_VERSION = "1";
    private static final ResourceLocation CHANNEL_NAME =
            ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "main");
    private static SimpleChannel CHANNEL;
    private static int packetId = 0;

    private modpackets() {}

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        if (CHANNEL != null) {
            return;
        }

        CHANNEL = NetworkRegistry.ChannelBuilder
                .named(CHANNEL_NAME)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        addNetworkMessage(
                castspellpackets.class,
                castspellpackets::toBytes,
                castspellpackets::new,
                (message, supplier) -> {
                    message.handle(supplier);
                }
        );
    }

    private static <MSG> void addNetworkMessage(
            Class<MSG> messageType,
            BiConsumer<MSG, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, MSG> decoder,
            BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer
    ) {
        CHANNEL.messageBuilder(messageType, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(encoder)
                .decoder(decoder)
                .consumerMainThread(messageConsumer)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        if (CHANNEL == null) {
            throw new IllegalStateException("Network channel not registered");
        }
        CHANNEL.sendToServer(message);
    }
}
