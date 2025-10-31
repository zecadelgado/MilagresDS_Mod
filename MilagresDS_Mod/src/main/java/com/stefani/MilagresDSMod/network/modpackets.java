package com.stefani.MilagresDSMod.network;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.attribute.IPlayerAttributes;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.capability.playermana;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.network.packets.AllocateAttributeC2SPacket;
import com.stefani.MilagresDSMod.network.packets.LightningSpearLightS2CPacket;
import com.stefani.MilagresDSMod.network.packets.ResetAttributesC2SPacket;
import com.stefani.MilagresDSMod.network.packets.SyncAttributesS2CPacket;
import com.stefani.MilagresDSMod.network.packets.SyncManaS2CPacket;
import com.stefani.MilagresDSMod.network.packets.castspellpackets;
import com.stefani.MilagresDSMod.network.packets.selectspellpackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

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
    private static boolean registered = false;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        CHANNEL.messageBuilder(castspellpackets.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(castspellpackets::toBytes)
                .decoder(castspellpackets::new)
                .consumerMainThread((packet, supplier) -> packet.handle(supplier))
                .add();

        CHANNEL.messageBuilder(selectspellpackets.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(selectspellpackets::toBytes)
                .decoder(selectspellpackets::new)
                .consumerMainThread((packet, supplier) -> packet.handle(supplier))
                .add();

        CHANNEL.messageBuilder(AllocateAttributeC2SPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(AllocateAttributeC2SPacket::encode)
                .decoder(AllocateAttributeC2SPacket::new)
                .consumerMainThread((packet, supplier) -> packet.handle(supplier))
                .add();

        CHANNEL.messageBuilder(ResetAttributesC2SPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ResetAttributesC2SPacket::encode)
                .decoder(ResetAttributesC2SPacket::new)
                .consumerMainThread((packet, supplier) -> packet.handle(supplier))
                .add();

        CHANNEL.messageBuilder(SyncManaS2CPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncManaS2CPacket::encode)
                .decoder(SyncManaS2CPacket::decode)
                .consumerMainThread(SyncManaS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncAttributesS2CPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncAttributesS2CPacket::encode)
                .decoder(SyncAttributesS2CPacket::decode)
                .consumerMainThread(SyncAttributesS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(LightningSpearLightS2CPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(LightningSpearLightS2CPacket::encode)
                .decoder(LightningSpearLightS2CPacket::new)
                .consumerMainThread(LightningSpearLightS2CPacket::handle)
                .add();
    }

    public static void sendToServer(castspellpackets packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToServer() {
        sendToServer(new castspellpackets());
    }

    public static void sendSpellSelection(@Nullable ResourceLocation spellId) {
        CHANNEL.sendToServer(new selectspellpackets(spellId));
    }

    public static void sendAllocateAttribute(String attributeKey, int points) {
        CHANNEL.sendToServer(new AllocateAttributeC2SPacket(attributeKey, points));
    }

    public static void sendResetAttributes() {
        CHANNEL.sendToServer(new ResetAttributesC2SPacket());
    }

    public static void sendManaSync(ServerPlayer player) {
        player.getCapability(playermanaprovider.PLAYER_MANA).ifPresent(mana ->
                sendManaSync(player, mana.getMana(), mana.getMaxMana()));
    }

    public static void sendManaSync(ServerPlayer player, int mana, int maxMana) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncManaS2CPacket(mana, maxMana));
    }

    public static void sendManaSync(ServerPlayer player, playermana mana) {
        sendManaSync(player, mana.getMana(), mana.getMaxMana());
    }

    public static void sendAttributesSync(ServerPlayer player) {
        player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES)
                .ifPresent(attributes -> sendAttributesSync(player, attributes));
    }

    public static void sendAttributesSync(ServerPlayer player, IPlayerAttributes attributes) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncAttributesS2CPacket(
                attributes.getLevel(),
                attributes.getXp(),
                attributes.getPoints(),
                attributes.getIntelligence(),
                attributes.getFaith(),
                attributes.getArcane(),
                attributes.getStrength(),
                attributes.getDexterity(),
                attributes.getConstitution()));
    }
}
