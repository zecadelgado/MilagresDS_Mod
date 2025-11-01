package com.stefani.MilagresDSMod.attribute;

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

public class playerattributesprovider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "player_attributes");
    public static final Capability<IPlayerAttributes> PLAYER_ATTRIBUTES = CapabilityManager.get(new CapabilityToken<>() {});

    private IPlayerAttributes attributes;
    private final LazyOptional<IPlayerAttributes> optional = LazyOptional.of(this::createAttributes);

    private IPlayerAttributes createAttributes() {
        if (this.attributes == null) {
            this.attributes = new PlayerAttributes();
        }
        return this.attributes;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_ATTRIBUTES) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createAttributes();
        tag.merge(this.attributes.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createAttributes();
        this.attributes.deserializeNBT(nbt);
    }
}
