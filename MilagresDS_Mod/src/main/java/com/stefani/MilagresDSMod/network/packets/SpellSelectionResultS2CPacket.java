package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.client.MagicStats;
import com.stefani.MilagresDSMod.client.gui.SpellMemorizeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SpellSelectionResultS2CPacket {
    private final boolean success;
    @Nullable
    private final ResourceLocation equippedSpell;

    public SpellSelectionResultS2CPacket(boolean success, @Nullable ResourceLocation equippedSpell) {
        this.success = success;
        this.equippedSpell = equippedSpell;
    }

    public static void encode(SpellSelectionResultS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.success);
        if (packet.equippedSpell != null) {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(packet.equippedSpell);
        } else {
            buffer.writeBoolean(false);
        }
    }

    public static SpellSelectionResultS2CPacket decode(FriendlyByteBuf buffer) {
        boolean success = buffer.readBoolean();
        ResourceLocation equipped = null;
        if (buffer.readBoolean()) {
            equipped = buffer.readResourceLocation();
        }
        return new SpellSelectionResultS2CPacket(success, equipped);
    }

    public static void handle(SpellSelectionResultS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            MagicStats magicStats = MagicStats.get();
            if (packet.equippedSpell != null) {
                magicStats.equipSpell(0, packet.equippedSpell);
            } else {
                magicStats.clearSlot(0);
            }

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof SpellMemorizeScreen screen) {
                screen.onSpellSelectionResult(packet.success, packet.equippedSpell);
            }
        });
        context.setPacketHandled(true);
    }
}
