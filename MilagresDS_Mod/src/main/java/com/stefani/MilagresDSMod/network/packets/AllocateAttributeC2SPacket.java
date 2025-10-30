package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.attribute.IPlayerAttributes;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.server.stats.ConstitutionApplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Locale;
import java.util.function.Supplier;

public class AllocateAttributeC2SPacket {
    private final String attributeKey;
    private final int points;

    public AllocateAttributeC2SPacket(String attributeKey, int points) {
        this.attributeKey = attributeKey == null ? "" : attributeKey.trim();
        this.points = Math.max(0, points);
    }

    public AllocateAttributeC2SPacket(FriendlyByteBuf buffer) {
        this.attributeKey = buffer.readUtf(32);
        this.points = buffer.readVarInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(attributeKey, 32);
        buffer.writeVarInt(points);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || points <= 0) {
                return;
            }

            player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
                if (attributes.getPoints() < points) {
                    player.displayClientMessage(Component.translatable("msg.milagresdsmod.no_points"), true);
                    return;
                }

                if (!applyAttribute(attributeKey, attributes, points)) {
                    return;
                }

                attributes.setPoints(Math.max(0, attributes.getPoints() - points));
                if ("constitution".equalsIgnoreCase(attributeKey)) {
                    ConstitutionApplier.apply(player, attributes);
                }
                modpackets.sendAttributesSync(player, attributes);
                player.displayClientMessage(Component.translatable("msg.milagresdsmod.attrs_applied"), true);
            });
        });
        context.setPacketHandled(true);
    }

    private static boolean applyAttribute(String key, IPlayerAttributes attributes, int points) {
        String normalized = key.toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "strength":
                attributes.setStrength(Math.max(0, attributes.getStrength() + points));
                return true;
            case "dexterity":
                attributes.setDexterity(Math.max(0, attributes.getDexterity() + points));
                return true;
            case "constitution":
                attributes.setConstitution(Math.max(0, attributes.getConstitution() + points));
                return true;
            case "intelligence":
                attributes.setIntelligence(Math.max(0, attributes.getIntelligence() + points));
                return true;
            case "faith":
                attributes.setFaith(Math.max(0, attributes.getFaith() + points));
                return true;
            case "arcane":
                attributes.setArcane(Math.max(0, attributes.getArcane() + points));
                return true;
            default:
                return false;
        }
    }
}
