package com.stefani.MilagresDSMod.attribute;

import net.minecraft.nbt.CompoundTag;

public interface IPlayerAttributes {
    int getLevel();

    void setLevel(int value);

    long getXp();

    void setXp(long value);

    int getPoints();

    void setPoints(int value);

    int getIntelligence();

    void setIntelligence(int value);

    int getFaith();

    void setFaith(int value);

    int getArcane();

    void setArcane(int value);

    void addXp(long amount);

    long xpToNextLevel();

    void addPoints(int delta);

    void resetAllAttributes();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);
}
