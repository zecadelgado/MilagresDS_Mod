package com.stefani.MilagresDSMod.network.packets;

import com.stefani.MilagresDSMod.attribute.PlayerAttributes;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.ModBlocks;
import com.stefani.MilagresDSMod.server.stats.ConstitutionApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LevelUpAtGraceC2SPacket {
    private static final double MAX_DISTANCE = 6.0D;

    private final BlockPos gracePos;

    public LevelUpAtGraceC2SPacket(BlockPos gracePos) {
        this.gracePos = gracePos;
    }

    public static void encode(LevelUpAtGraceC2SPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.gracePos);
    }

    public static LevelUpAtGraceC2SPacket decode(FriendlyByteBuf buffer) {
        return new LevelUpAtGraceC2SPacket(buffer.readBlockPos());
    }

    public static void handle(LevelUpAtGraceC2SPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            Level level = player.level();
            BlockPos pos = packet.gracePos;
            if (!level.hasChunkAt(pos)) {
                return;
            }

            if (!level.getBlockState(pos).is(ModBlocks.GRACE_SITE.get())) {
                return;
            }

            if (player.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_DISTANCE * MAX_DISTANCE) {
                player.displayClientMessage(Component.translatable("msg.milagresdsmod.grace_too_far"), true);
                return;
            }

            player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
                long cost = PlayerAttributes.xpToNextLevel(attributes.getLevel());
                if (cost <= 0L) {
                    return;
                }

                long storedRunes = attributes.getStoredRunes();
                if (storedRunes < cost) {
                    player.displayClientMessage(Component.translatable("msg.milagresdsmod.not_enough_runes", cost), true);
                    return;
                }

                attributes.addXp(-cost);
                attributes.setLevel(attributes.getLevel() + 1);
                attributes.addPoints(Math.max(0, ModCommonConfig.POINTS_PER_LEVEL.get()));
                ConstitutionApplier.apply(player, attributes);
                modpackets.sendAttributesSync(player, attributes);
                player.displayClientMessage(Component.translatable("msg.milagresdsmod.level_up_grace_success", attributes.getLevel()), true);
            });
        });
        context.setPacketHandled(true);
    }
}
