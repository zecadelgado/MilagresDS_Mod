package com.stefani.MilagresDSMod.attribute;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IPlayerAttributes {
    int getLevel();

    void setLevel(int value);

    long getStoredRunes();

    void setStoredRunes(long value);

    default long getXp() {
        return getStoredRunes();
    }

    default void setXp(long value) {
        setStoredRunes(value);
    }

    int getPoints();

    void setPoints(int value);

    int getStrength();

    void setStrength(int value);

    int getDexterity();

    void setDexterity(int value);

    int getConstitution();

    void setConstitution(int value);

    int getIntelligence();

    void setIntelligence(int value);

    int getFaith();

    void setFaith(int value);

    int getArcane();

    void setArcane(int value);

    void addXp(long amount);

    long xpToNextLevel();

    void addPoints(int delta);

    default int getTotalAllocatedPoints() {
        return Math.max(0, getStrength())
                + Math.max(0, getDexterity())
                + Math.max(0, getConstitution())
                + Math.max(0, getIntelligence())
                + Math.max(0, getFaith())
                + Math.max(0, getArcane());
    }

    void resetAllAttributes();

    long getLostRunes();

    void setLostRunes(long value);

    default void clearLostRunes() {
        setLostRunes(0L);
    }

    Optional<GlobalPos> getBloodstainLocation();

    void setBloodstainLocation(@Nullable GlobalPos pos);

    default void clearBloodstain() {
        setBloodstainLocation(null);
    }

    default boolean hasBloodstain() {
        return getBloodstainLocation().isPresent();
    }

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);
}
