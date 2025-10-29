package com.stefani.MilagresDSMod.capability;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class playerspellsprovider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = new ResourceLocation(MilagresDSMod.MODID, "player_spells");
    public static final Capability<playerspells> PLAYER_SPELLS = CapabilityManager.get(new CapabilityToken<>() {});

    private playerspells spells = null;
    private final LazyOptional<playerspells> optional = LazyOptional.of(this::createPlayerSpells);

    private playerspells createPlayerSpells() {
        if (this.spells == null) {
            this.spells = new playerspells();
        }
        return this.spells;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_SPELLS) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        createPlayerSpells();
        return this.spells.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerSpells();
        this.spells.deserializeNBT(nbt);
    }
}
