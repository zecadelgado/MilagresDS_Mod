package com.stefani.MilagresDSMod.block.entity;

import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BloodstainBlockEntity extends BlockEntity {
    private static final String KEY_OWNER = "Owner";
    private static final String KEY_RUNES = "Runes";

    @Nullable
    private UUID owner;
    private long storedRunes;

    public BloodstainBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.BLOODSTAIN_BLOCK_ENTITY.get(), pos, state);
    }

    public void initialise(@Nullable UUID owner, long runes) {
        this.owner = owner;
        this.storedRunes = Math.max(0L, runes);
        setChanged();
    }

    public void ensureAttached(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!serverLevel.getBlockState(pos).is(BlockRegistry.BLOODSTAIN_BLOCK.get())) {
            onBroken();
        }
    }

    public InteractionResult onInteract(ServerPlayer player) {
        if (this.owner == null || !this.owner.equals(player.getUUID())) {
            return InteractionResult.PASS;
        }
        return player.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            long toRestore = Math.max(this.storedRunes, attributes.getLostRunes());
            if (toRestore <= 0L) {
                attributes.clearBloodstain();
                attributes.clearLostRunes();
                removeStain();
                modpackets.sendBloodstainSync(player, attributes);
                modpackets.sendRunesSync(player, attributes);
                return InteractionResult.CONSUME;
            }
            attributes.clearBloodstain();
            attributes.clearLostRunes();
            attributes.addXp(toRestore);
            removeStain();
            modpackets.sendAttributesSync(player, attributes);
            modpackets.sendBloodstainSync(player, attributes);
            return InteractionResult.CONSUME;
        }).orElse(InteractionResult.PASS);
    }

    public void onBroken() {
        this.owner = null;
        this.storedRunes = 0L;
        setChanged();
    }

    private void removeStain() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.removeBlock(worldPosition, false);
        }
        this.owner = null;
        this.storedRunes = 0L;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.owner != null) {
            tag.putUUID(KEY_OWNER, this.owner);
        }
        tag.putLong(KEY_RUNES, this.storedRunes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.owner = tag.hasUUID(KEY_OWNER) ? tag.getUUID(KEY_OWNER) : null;
        this.storedRunes = Math.max(0L, tag.getLong(KEY_RUNES));
    }
}
