package com.stefani.MilagresDSMod.attribute;

import net.minecraft.nbt.CompoundTag;

public interface IPlayerAttributes {
    int getLevel();

    void setLevel(int value);

    long getXp();

    void setXp(long value);

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

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);
}
