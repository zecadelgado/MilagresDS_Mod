package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.magic.visual.lightning.client.LightningSpearLightClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LightningSpearLightS2CPacket {
    private final int entityId;
    private final int casterId;
    private final int rgb;
    private final float radius;
    private final int durationTicks;

    public LightningSpearLightS2CPacket(int entityId, int casterId, int rgb, float radius, int durationTicks) {
        this.entityId = entityId;
        this.casterId = casterId;
        this.rgb = rgb;
        this.radius = radius;
        this.durationTicks = durationTicks;
    }

    public LightningSpearLightS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.casterId = buf.readVarInt();
        this.rgb = buf.readInt();
        this.radius = buf.readFloat();
        this.durationTicks = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(casterId);
        buf.writeInt(rgb);
        buf.writeFloat(radius);
        buf.writeVarInt(durationTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> LightningSpearLightClientHandler.schedule(entityId, casterId, rgb, radius, durationTicks));
        ctx.setPacketHandled(true);
    }
}
