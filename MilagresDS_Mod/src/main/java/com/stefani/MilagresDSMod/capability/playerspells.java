package com.stefani.MilagresDSMod.capability;

import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class playerspells implements INBTSerializable<CompoundTag> {
    private ResourceLocation equippedSpellId;

    @Nullable
    public spell getEquippedSpell() {
        if (equippedSpellId == null) {
            return null;
        }
        return spellregistry.REGISTRY.get().getValue(equippedSpellId);
    }

    @Nullable
    public ResourceLocation getEquippedSpellId() {
        return equippedSpellId;
    }

    public void setEquippedSpell(@Nullable spell spell) {
        if (spell == null) {
            this.equippedSpellId = null;
            return;
        }
        ResourceLocation id = spellregistry.REGISTRY.get().getKey(spell);
        if (id != null) {
            this.equippedSpellId = id;
        } else {
            this.equippedSpellId = null;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (equippedSpellId != null) {
            tag.putString("EquippedSpell", equippedSpellId.toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("EquippedSpell")) {
            this.equippedSpellId = ResourceLocation.tryParse(nbt.getString("EquippedSpell"));
        } else {
            this.equippedSpellId = null;
        }
    }
}
