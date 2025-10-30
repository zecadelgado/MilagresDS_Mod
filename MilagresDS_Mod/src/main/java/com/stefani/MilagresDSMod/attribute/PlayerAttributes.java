package com.stefani.MilagresDSMod.attribute;

import com.stefani.MilagresDSMod.config.ModCommonConfig;
import net.minecraft.nbt.CompoundTag;

public class PlayerAttributes implements IPlayerAttributes {
    private static final String KEY_LEVEL = "Level";
    private static final String KEY_XP = "Xp";
    private static final String KEY_POINTS = "Points";
    private static final String KEY_INTELLIGENCE = "Intelligence";
    private static final String KEY_FAITH = "Faith";
    private static final String KEY_ARCANE = "Arcane";

    private int level;
    private long xp;
    private int points;
    private int intelligence;
    private int faith;
    private int arcane;

    public PlayerAttributes() {
        this.level = Math.max(1, ModCommonConfig.STARTING_LEVEL.get());
        this.xp = 0L;
        this.points = Math.max(0, ModCommonConfig.STARTING_POINTS.get());
        this.intelligence = 0;
        this.faith = 0;
        this.arcane = 0;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int value) {
        this.level = Math.max(1, value);
    }

    @Override
    public long getXp() {
        return xp;
    }

    @Override
    public void setXp(long value) {
        this.xp = Math.max(0L, value);
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void setPoints(int value) {
        this.points = Math.max(0, value);
    }

    @Override
    public int getIntelligence() {
        return intelligence;
    }

    @Override
    public void setIntelligence(int value) {
        this.intelligence = Math.max(0, value);
    }

    @Override
    public int getFaith() {
        return faith;
    }

    @Override
    public void setFaith(int value) {
        this.faith = Math.max(0, value);
    }

    @Override
    public int getArcane() {
        return arcane;
    }

    @Override
    public void setArcane(int value) {
        this.arcane = Math.max(0, value);
    }

    @Override
    public void addXp(long amount) {
        if (amount <= 0L) {
            return;
        }
        this.xp = Math.min(Long.MAX_VALUE, this.xp + amount);
        boolean leveledUp = true;
        while (leveledUp) {
            leveledUp = false;
            long cost = xpToNextLevel();
            if (cost <= 0L) {
                break;
            }
            if (this.xp >= cost) {
                this.xp -= cost;
                this.level = Math.max(1, this.level + 1);
                this.points = Math.min(Integer.MAX_VALUE,
                        this.points + Math.max(0, ModCommonConfig.POINTS_PER_LEVEL.get()));
                leveledUp = true;
            }
        }
    }

    @Override
    public long xpToNextLevel() {
        long base = Math.max(1L, ModCommonConfig.BASE_XP_TO_LEVEL_2.get());
        double multiplier = Math.max(0.0D, ModCommonConfig.XP_GROWTH_MULTIPLIER.get());
        if (multiplier <= 0.0D) {
            return base;
        }
        int currentLevel = Math.max(1, this.level);
        double exponent = currentLevel - 1;
        double value = base * Math.pow(multiplier, exponent);
        long rounded = (long) Math.ceil(value);
        return Math.max(1L, rounded);
    }

    @Override
    public void addPoints(int delta) {
        if (delta == 0) {
            return;
        }
        long result = (long) this.points + delta;
        this.points = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, result));
    }

    @Override
    public void resetAllAttributes() {
        int refunded = this.intelligence + this.faith + this.arcane;
        this.intelligence = 0;
        this.faith = 0;
        this.arcane = 0;
        addPoints(refunded);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_LEVEL, this.level);
        tag.putLong(KEY_XP, this.xp);
        tag.putInt(KEY_POINTS, this.points);
        tag.putInt(KEY_INTELLIGENCE, this.intelligence);
        tag.putInt(KEY_FAITH, this.faith);
        tag.putInt(KEY_ARCANE, this.arcane);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.level = Math.max(1, tag.getInt(KEY_LEVEL));
        this.xp = Math.max(0L, tag.getLong(KEY_XP));
        this.points = Math.max(0, tag.getInt(KEY_POINTS));
        this.intelligence = Math.max(0, tag.getInt(KEY_INTELLIGENCE));
        this.faith = Math.max(0, tag.getInt(KEY_FAITH));
        this.arcane = Math.max(0, tag.getInt(KEY_ARCANE));
    }
}
