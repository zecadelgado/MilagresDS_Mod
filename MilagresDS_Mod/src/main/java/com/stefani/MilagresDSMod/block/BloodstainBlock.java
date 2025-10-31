package com.stefani.MilagresDSMod.block;

import com.stefani.MilagresDSMod.block.entity.BloodstainBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BloodstainBlock extends Block implements EntityBlock {
    public BloodstainBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) != 0) {
            return;
        }
        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.1D;
        double centerZ = pos.getZ() + 0.5D;
        level.addParticle(ParticleTypes.DAMAGE_INDICATOR, centerX, centerY, centerZ,
                (random.nextDouble() - 0.5D) * 0.2D, 0.02D, (random.nextDouble() - 0.5D) * 0.2D);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BloodstainBlockEntity bloodstain) {
                bloodstain.onBroken();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BloodstainBlockEntity bloodstain) {
                bloodstain.onBroken();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodstainBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof BloodstainBlockEntity bloodstain)) {
            return InteractionResult.PASS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        return bloodstain.onInteract(serverPlayer);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
        return false;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        if (!level.isClientSide() && level instanceof Level worldLevel) {
            BlockEntity entity = worldLevel.getBlockEntity(pos);
            if (entity instanceof BloodstainBlockEntity bloodstain) {
                bloodstain.ensureAttached(worldLevel, pos);
            }
        }
        return state;
    }
}
