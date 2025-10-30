package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.server.stats.ConstitutionApplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ResetAttributesC2SPacket {
    public ResetAttributesC2SPacket() {
    }

    public ResetAttributesC2SPacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
                attributes.resetAllAttributes();
                ConstitutionApplier.apply(player, attributes);
                modpackets.sendAttributesSync(player, attributes);
                player.displayClientMessage(Component.translatable("msg.milagresdsmod.attrs_applied"), true);
            });
        });
        context.setPacketHandled(true);
    }
}
