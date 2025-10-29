package com.stefani.MilagresDSMod.capability;

import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class playerspells implements INBTSerializable<CompoundTag> {
    private static final String COOLDOWNS_TAG = "Cooldowns";
    private ResourceLocation equippedSpellId;
    private final Map<ResourceLocation, Long> spellCooldowns = new HashMap<>();

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

    public boolean isOnCooldown(spell spell, Level level) {
        ResourceLocation id = spellregistry.REGISTRY.get().getKey(spell);
        if (id == null) {
            return false;
        }
        cleanupExpired(level.getGameTime());
        long readyTick = spellCooldowns.getOrDefault(id, 0L);
        return level.getGameTime() < readyTick;
    }

    public void setCooldown(spell spell, Level level) {
        ResourceLocation id = spellregistry.REGISTRY.get().getKey(spell);
        if (id != null) {
            spellCooldowns.put(id, level.getGameTime() + spell.getCooldownTicks());
        }
    }

    private void cleanupExpired(long gameTime) {
        spellCooldowns.entrySet().removeIf(entry -> entry.getValue() <= gameTime);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (equippedSpellId != null) {
            tag.putString("EquippedSpell", equippedSpellId.toString());
        }
        if (!spellCooldowns.isEmpty()) {
            CompoundTag cooldowns = new CompoundTag();
            spellCooldowns.forEach((id, readyTick) -> cooldowns.putLong(id.toString(), readyTick));
            tag.put(COOLDOWNS_TAG, cooldowns);
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
        spellCooldowns.clear();
        if (nbt.contains(COOLDOWNS_TAG)) {
            CompoundTag cooldowns = nbt.getCompound(COOLDOWNS_TAG);
            for (String key : cooldowns.getAllKeys()) {
                ResourceLocation id = ResourceLocation.tryParse(key);
                if (id != null) {
                    spellCooldowns.put(id, cooldowns.getLong(key));
                }
            }
        }
    }
}
