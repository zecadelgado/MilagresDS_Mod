package com.stefani.MilagresDSMod.util;

import com.stefani.MilagresDSMod.block.entity.BloodstainBlockEntity;
import com.stefani.MilagresDSMod.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public final class BloodstainHelper {
    private BloodstainHelper() {
    }

    public static Optional<BlockPos> place(ServerLevel level, BlockPos suggested, UUID owner, long runes) {
        BlockPos target = findPlacement(level, suggested);
        if (target == null) {
            return Optional.empty();
        }
        BlockState state = BlockRegistry.BLOODSTAIN_BLOCK.get().defaultBlockState();
        level.setBlock(target, state, Block.UPDATE_ALL);
        if (level.getBlockEntity(target) instanceof BloodstainBlockEntity entity) {
            entity.initialise(owner, runes);
        }
        return Optional.of(target);
    }

    public static void remove(MinecraftServer server, @Nullable GlobalPos location) {
        if (location == null) {
            return;
        }
        ServerLevel level = server.getLevel(location.dimension());
        if (level == null) {
            return;
        }
        if (level.getBlockState(location.pos()).is(BlockRegistry.BLOODSTAIN_BLOCK.get())) {
            level.removeBlock(location.pos(), false);
        }
    }

    @Nullable
    private static BlockPos findPlacement(LevelAccessor level, BlockPos suggested) {
        BlockPos pos = suggested;
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return pos;
        }
        BlockPos above = pos.above();
        if (level.getBlockState(above).isAir()) {
            return above;
        }
        if (level instanceof ServerLevel serverLevel) {
            BlockPos top = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            if (serverLevel.getBlockState(top).isAir()) {
                return top;
            }
        }
        return null;
    }
}
