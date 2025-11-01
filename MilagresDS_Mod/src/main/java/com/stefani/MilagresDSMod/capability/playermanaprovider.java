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

public class playermanaprovider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "player_mana");
    public static final Capability<playermana> PLAYER_MANA = CapabilityManager.get(new CapabilityToken<>(){});

    private playermana mana = null;
    private final LazyOptional<playermana> optional = LazyOptional.of(this::createPlayerMana);

    private playermana createPlayerMana() {
        if (this.mana == null) {
            this.mana = new playermana();
        }
        return this.mana;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_MANA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerMana();
        nbt.putInt("mana", mana.getMana());
        nbt.putInt("maxMana", mana.getMaxMana());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerMana();
        mana.setMaxMana(nbt.contains("maxMana") ? nbt.getInt("maxMana") : mana.getMaxMana());
        mana.setMana(nbt.getInt("mana"));
    }
}
