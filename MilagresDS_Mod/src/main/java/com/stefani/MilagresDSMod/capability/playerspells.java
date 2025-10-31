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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class playerspells implements INBTSerializable<CompoundTag> {
    private static final String COOLDOWNS_TAG = "Cooldowns";
    private static final String UNLOCKED_TAG = "Unlocked";
    private static final String LAST_CAST_TAG = "LastCast";
    private static final String SLOT_COUNT_TAG = "SlotCount";
    private static final String MEMORISED_TAG = "Memorised";
    private static final String MEM_SLOT_KEY = "Slot";
    private static final String MEM_SPELL_KEY = "Id";
    private static final int DEFAULT_SLOT_COUNT = 4;

    private ResourceLocation equippedSpellId;
    private final Map<ResourceLocation, Long> spellCooldowns = new HashMap<>();
    private final Set<ResourceLocation> unlocked = new HashSet<>();
    private final List<ResourceLocation> memorizedSlots = new ArrayList<>();
    private int slotCount = DEFAULT_SLOT_COUNT;
    private long lastCastTick;

    public playerspells() {
        unlockAllRegistered();
        ensureSlotCapacity();
    }

    @Nullable
    public spell getEquippedSpell() {
        ResourceLocation id = getEquippedSpellId();
        if (id == null) {
            return null;
        }
        var registry = spellregistry.REGISTRY.get();
        if (registry == null) {
            return null;
        }
        return registry.getValue(id);
    }

    @Nullable
    public ResourceLocation getEquippedSpellId() {
        return equippedSpellId != null && isUnlocked(equippedSpellId) ? equippedSpellId : null;
    }

    public void setEquippedSpell(@Nullable spell spell) {
        if (spell == null) {
            setEquippedSpellId(null);
            return;
        }
        ResourceLocation id = spellregistry.REGISTRY.get().getKey(spell);
        setEquippedSpellId(id);
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
        removeFromAllSlots(id);
        updateEquippedFromSlots();
    }

    public Set<ResourceLocation> getUnlocked() {
        return Collections.unmodifiableSet(unlocked);
    }

    public List<ResourceLocation> getMemorizedSlots() {
        return Collections.unmodifiableList(new ArrayList<>(memorizedSlots));
    }

    public int getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(int slotCount) {
        this.slotCount = Math.max(1, slotCount);
        ensureSlotCapacity();
        updateEquippedFromSlots();
    }

    public void setMemorizedSlots(List<ResourceLocation> slots) {
        ensureSlotCapacity();
        for (int i = 0; i < memorizedSlots.size(); i++) {
            memorizedSlots.set(i, null);
        }

        Set<ResourceLocation> used = new HashSet<>();
        var registry = spellregistry.REGISTRY.get();
        for (int i = 0; i < slotCount; i++) {
            ResourceLocation id = i < slots.size() ? slots.get(i) : null;
            if (id == null) {
                continue;
            }
            if (used.contains(id)) {
                continue;
            }
            if (!isUnlocked(id)) {
                continue;
            }
            if (registry == null || !registry.containsKey(id)) {
                continue;
            }
            memorizedSlots.set(i, id);
            used.add(id);
        }
        updateEquippedFromSlots();
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

    private void ensureSlotCapacity() {
        while (memorizedSlots.size() < slotCount) {
            memorizedSlots.add(null);
        }
        if (memorizedSlots.size() > slotCount) {
            memorizedSlots.subList(slotCount, memorizedSlots.size()).clear();
        }
    }

    private void removeFromAllSlots(ResourceLocation id) {
        for (int i = 0; i < memorizedSlots.size(); i++) {
            if (id.equals(memorizedSlots.get(i))) {
                memorizedSlots.set(i, null);
            }
        }
    }

    private void removeFromOtherSlots(ResourceLocation id, int skipIndex) {
        for (int i = 0; i < memorizedSlots.size(); i++) {
            if (i == skipIndex) {
                continue;
            }
            if (id.equals(memorizedSlots.get(i))) {
                memorizedSlots.set(i, null);
            }
        }
    }

    private void setEquippedSpellId(@Nullable ResourceLocation id) {
        if (id != null && !isUnlocked(id)) {
            id = null;
        }
        var registry = spellregistry.REGISTRY.get();
        if (id != null && (registry == null || !registry.containsKey(id))) {
            id = null;
        }
        ensureSlotCapacity();
        if (id == null) {
            this.equippedSpellId = null;
            if (!memorizedSlots.isEmpty()) {
                memorizedSlots.set(0, null);
            }
            updateEquippedFromSlots();
            return;
        }
        this.equippedSpellId = id;
        if (!memorizedSlots.isEmpty()) {
            removeFromOtherSlots(id, 0);
            memorizedSlots.set(0, id);
        }
    }

    private void updateEquippedFromSlots() {
        this.equippedSpellId = null;
        for (ResourceLocation id : memorizedSlots) {
            if (id != null && isUnlocked(id)) {
                var registry = spellregistry.REGISTRY.get();
                if (registry != null && registry.containsKey(id)) {
                    this.equippedSpellId = id;
                    break;
                }
            }
        }
        if (this.equippedSpellId != null) {
            ensureSlotCapacity();
            if (!memorizedSlots.isEmpty() && !this.equippedSpellId.equals(memorizedSlots.get(0))) {
                removeFromOtherSlots(this.equippedSpellId, 0);
                memorizedSlots.set(0, this.equippedSpellId);
            }
        }
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
        tag.putInt(SLOT_COUNT_TAG, slotCount);
        ListTag memorisedList = new ListTag();
        for (int i = 0; i < memorizedSlots.size(); i++) {
            CompoundTag entry = new CompoundTag();
            entry.putInt(MEM_SLOT_KEY, i);
            ResourceLocation id = memorizedSlots.get(i);
            if (id != null) {
                entry.putString(MEM_SPELL_KEY, id.toString());
            }
            memorisedList.add(entry);
        }
        tag.put(MEMORISED_TAG, memorisedList);
        if (lastCastTick > 0L) {
            tag.putLong(LAST_CAST_TAG, lastCastTick);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ResourceLocation equippedFromTag = nbt.contains("EquippedSpell")
                ? ResourceLocation.tryParse(nbt.getString("EquippedSpell"))
                : null;
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
        if (nbt.contains(SLOT_COUNT_TAG)) {
            int count = Math.max(1, nbt.getInt(SLOT_COUNT_TAG));
            this.slotCount = count;
        } else {
            this.slotCount = DEFAULT_SLOT_COUNT;
        }
        ensureSlotCapacity();
        List<ResourceLocation> loadedSlots = new ArrayList<>(Collections.nCopies(slotCount, null));
        if (nbt.contains(MEMORISED_TAG, Tag.TAG_LIST)) {
            ListTag list = nbt.getList(MEMORISED_TAG, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                int slot = entry.getInt(MEM_SLOT_KEY);
                if (slot < 0 || slot >= loadedSlots.size()) {
                    continue;
                }
                if (entry.contains(MEM_SPELL_KEY, Tag.TAG_STRING)) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getString(MEM_SPELL_KEY));
                    loadedSlots.set(slot, id);
                }
            }
        }
        setMemorizedSlots(loadedSlots);
        lastCastTick = nbt.contains(LAST_CAST_TAG) ? nbt.getLong(LAST_CAST_TAG) : 0L;
        if (equippedFromTag != null) {
            setEquippedSpellId(equippedFromTag);
        }
        if (equippedSpellId != null && !isUnlocked(equippedSpellId)) {
            setEquippedSpellId(null);
            updateEquippedFromSlots();
        }
    }
}
