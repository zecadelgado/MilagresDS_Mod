package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.client.network.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellLightS2CPacket {
    private final int entityId;
    private final int rgb;
    private final float radius;
    private final int durationTicks;

    public SpellLightS2CPacket(int entityId, int rgb, float radius, int durationTicks) {
        this.entityId = entityId;
        this.rgb = rgb;
        this.radius = radius;
        this.durationTicks = durationTicks;
    }

    public SpellLightS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.rgb = buf.readInt();
        this.radius = buf.readFloat();
        this.durationTicks = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeInt(rgb);
        buf.writeFloat(radius);
        buf.writeVarInt(durationTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> ClientPacketHandlers.addSpellLight(entityId, rgb, radius, durationTicks));
        ctx.setPacketHandled(true);
    }
}
