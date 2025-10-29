package com.stefani.MilagresDSMod.client;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks the client-side snapshot of memorised spells and available slots. The values are manipulated solely on the
 * client UI and synchronised with the server through dedicated packets when the player confirms a change.
 */
public final class MagicStats {
    private static final MagicStats INSTANCE = new MagicStats();

    private int slotsMax = 4;
    private final List<ResourceLocation> equippedSpells = new ArrayList<>();

    private MagicStats() {
        ensureCapacity();
    }

    public static MagicStats get() {
        return INSTANCE;
    }

    public int getSlotsMax() {
        return slotsMax;
    }

    public List<ResourceLocation> getEquippedSpells() {
        return Collections.unmodifiableList(equippedSpells);
    }

    public void addSlot() {
        slotsMax++;
        ensureCapacity();
    }

    public void removeSlot() {
        if (slotsMax <= 1) {
            return;
        }
        slotsMax--;
        trimToSlots();
    }

    public void setSlots(int slots) {
        slotsMax = Math.max(1, slots);
        ensureCapacity();
        trimToSlots();
    }

    public void equipSpell(int slotIndex, @Nullable ResourceLocation id) {
        ensureCapacity();
        if (slotIndex < 0 || slotIndex >= slotsMax) {
            return;
        }
        if (id != null) {
            // Guarantee uniqueness so a spell occupies only one slot at a time.
            for (int i = 0; i < equippedSpells.size(); i++) {
                if (id.equals(equippedSpells.get(i))) {
                    equippedSpells.set(i, null);
                }
            }
        }
        equippedSpells.set(slotIndex, id);
    }

    public void clearSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slotsMax) {
            return;
        }
        equippedSpells.set(slotIndex, null);
    }

    @Nullable
    public ResourceLocation getSpellInSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slotsMax) {
            return null;
        }
        return equippedSpells.get(slotIndex);
    }

    public boolean isEquipped(@Nullable ResourceLocation spellId) {
        return spellId != null && equippedSpells.contains(spellId);
    }

    public void syncFromServer(int slots, List<ResourceLocation> memorised) {
        slotsMax = Math.max(1, slots);
        equippedSpells.clear();
        equippedSpells.addAll(memorised);
        ensureCapacity();
        trimToSlots();
    }

    private void ensureCapacity() {
        while (equippedSpells.size() < slotsMax) {
            equippedSpells.add(null);
        }
    }

    private void trimToSlots() {
        if (equippedSpells.size() > slotsMax) {
            equippedSpells.subList(slotsMax, equippedSpells.size()).clear();
        }
    }
}
