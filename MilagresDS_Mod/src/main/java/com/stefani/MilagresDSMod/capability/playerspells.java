package com.stefani.MilagresDSMod.capability;

import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class playerspells implements INBTSerializable<CompoundTag> {
    private static final String COOLDOWNS_TAG = "Cooldowns";
    private static final String UNLOCKED_TAG = "Unlocked";
    private static final String LAST_CAST_TAG = "LastCast";
    private ResourceLocation equippedSpellId;
    private final Map<ResourceLocation, Long> spellCooldowns = new HashMap<>();
    private final Set<ResourceLocation> unlocked = new HashSet<>();
    private long lastCastTick;

    public playerspells() {
        unlockAllRegistered();
    }

    @Nullable
    public spell getEquippedSpell() {
        if (equippedSpellId == null) {
            return null;
        }
        if (!isUnlocked(equippedSpellId)) {
            return null;
        }
        return spellregistry.REGISTRY.get().getValue(equippedSpellId);
    }

    @Nullable
    public ResourceLocation getEquippedSpellId() {
        return equippedSpellId != null && isUnlocked(equippedSpellId) ? equippedSpellId : null;
    }

    public void setEquippedSpell(@Nullable spell spell) {
        if (spell == null) {
            this.equippedSpellId = null;
            return;
        }
        ResourceLocation id = spellregistry.REGISTRY.get().getKey(spell);
        if (id != null && isUnlocked(id)) {
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

    public boolean isUnlocked(ResourceLocation id) {
        return unlocked.contains(id);
    }

    public void unlock(ResourceLocation id) {
        if (id == null) {
            return;
        }
        if (spellregistry.REGISTRY.get() != null && !spellregistry.REGISTRY.get().containsKey(id)) {
            return;
        }
        unlocked.add(id);
    }

    public void lock(ResourceLocation id) {
        if (id == null) {
            return;
        }
        unlocked.remove(id);
        spellCooldowns.remove(id);
        if (id.equals(equippedSpellId)) {
            this.equippedSpellId = null;
        }
    }

    public Set<ResourceLocation> getUnlocked() {
        return Collections.unmodifiableSet(unlocked);
    }

    public long getLastCastTick() {
        return lastCastTick;
    }

    public void setLastCastTick(long lastCastTick) {
        this.lastCastTick = Math.max(0L, lastCastTick);
    }

    private void unlockAllRegistered() {
        var registry = spellregistry.REGISTRY.get();
        if (registry != null) {
            registry.getKeys().forEach(unlocked::add);
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
        ListTag unlockedList = new ListTag();
        unlocked.forEach(id -> unlockedList.add(net.minecraft.nbt.StringTag.valueOf(id.toString())));
        tag.put(UNLOCKED_TAG, unlockedList);
        if (lastCastTick > 0L) {
            tag.putLong(LAST_CAST_TAG, lastCastTick);
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
        unlocked.clear();
        if (nbt.contains(UNLOCKED_TAG, Tag.TAG_LIST)) {
            ListTag list = nbt.getList(UNLOCKED_TAG, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
                if (id != null) {
                    unlocked.add(id);
                }
            }
        } else {
            unlockAllRegistered();
        }
        lastCastTick = nbt.contains(LAST_CAST_TAG) ? nbt.getLong(LAST_CAST_TAG) : 0L;
        if (equippedSpellId != null && !isUnlocked(equippedSpellId)) {
            equippedSpellId = null;
        }
    }
}
