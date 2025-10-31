package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.client.data.RunesClientCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBloodstainS2CPacket {
    private final boolean hasStain;
    private final ResourceLocation dimensionId;
    private final BlockPos position;

    public SyncBloodstainS2CPacket() {
        this.hasStain = false;
        this.dimensionId = null;
        this.position = BlockPos.ZERO;
    }

    public SyncBloodstainS2CPacket(ResourceLocation dimensionId, BlockPos position) {
        this.hasStain = true;
        this.dimensionId = dimensionId;
        this.position = position;
    }

    public static void encode(SyncBloodstainS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.hasStain);
        if (packet.hasStain) {
            buffer.writeResourceLocation(packet.dimensionId);
            buffer.writeBlockPos(packet.position);
        }
    }

    public static SyncBloodstainS2CPacket decode(FriendlyByteBuf buffer) {
        boolean hasStain = buffer.readBoolean();
        if (!hasStain) {
            return new SyncBloodstainS2CPacket();
        }
        ResourceLocation dimension = buffer.readResourceLocation();
        BlockPos pos = buffer.readBlockPos();
        return new SyncBloodstainS2CPacket(dimension, pos);
    }

    public static void handle(SyncBloodstainS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (!packet.hasStain || packet.dimensionId == null) {
                RunesClientCache.updateBloodstain(null);
                return;
            }
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, packet.dimensionId);
            GlobalPos globalPos = GlobalPos.of(dimension, packet.position);
            RunesClientCache.updateBloodstain(globalPos);
        });
        context.setPacketHandled(true);
    }
}
