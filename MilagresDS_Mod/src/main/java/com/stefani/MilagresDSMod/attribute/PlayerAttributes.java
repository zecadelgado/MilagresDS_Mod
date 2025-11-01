package com.stefani.MilagresDSMod.attribute;

import com.stefani.MilagresDSMod.config.ModCommonConfig;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlayerAttributes implements IPlayerAttributes {
    private static final String KEY_LEVEL = "Level";
    private static final String KEY_STORED_RUNES = "StoredRunes";
    private static final String LEGACY_KEY_XP = "Xp";
    private static final String KEY_POINTS = "Points";
    private static final String KEY_INTELLIGENCE = "Intelligence";
    private static final String KEY_FAITH = "Faith";
    private static final String KEY_ARCANE = "Arcane";
    private static final String KEY_STRENGTH = "Strength";
    private static final String KEY_DEXTERITY = "Dexterity";
    private static final String KEY_CONSTITUTION = "Constitution";
    private static final String KEY_LOST_RUNES = "LostRunes";
    private static final String KEY_BLOODSTAIN = "Bloodstain";

    private int level;
    private long storedRunes;
    private int points;
    private int intelligence;
    private int faith;
    private int arcane;
    private int strength;
    private int dexterity;
    private int constitution;
    private long lostRunes;
    @Nullable
    private GlobalPos bloodstainLocation;

    public PlayerAttributes() {
        this.level = Math.max(1, ModCommonConfig.STARTING_LEVEL.get());
        this.storedRunes = 0L;
        this.points = Math.max(0, ModCommonConfig.STARTING_POINTS.get());
        this.intelligence = 0;
        this.faith = 0;
        this.arcane = 0;
        this.strength = 0;
        this.dexterity = 0;
        this.constitution = 0;
        this.lostRunes = 0L;
        this.bloodstainLocation = null;
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
    public long getStoredRunes() {
        return storedRunes;
    }

    @Override
    public void setStoredRunes(long value) {
        this.storedRunes = Math.max(0L, value);
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
    public int getStrength() {
        return strength;
    }

    @Override
    public void setStrength(int value) {
        this.strength = Math.max(0, value);
    }

    @Override
    public int getDexterity() {
        return dexterity;
    }

    @Override
    public void setDexterity(int value) {
        this.dexterity = Math.max(0, value);
    }

    @Override
    public int getConstitution() {
        return constitution;
    }

    @Override
    public void setConstitution(int value) {
        this.constitution = Math.max(0, value);
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
        if (amount == 0L) {
            return;
        }
        if (amount > 0L) {
            long space = Long.MAX_VALUE - this.storedRunes;
            if (amount > space) {
                this.storedRunes = Long.MAX_VALUE;
            } else {
                this.storedRunes += amount;
            }
        } else {
            long newValue = this.storedRunes + amount;
            if (newValue < 0L) {
                this.storedRunes = 0L;
            } else {
                this.storedRunes = newValue;
            }
        }
    }

    @Override
    public long xpToNextLevel() {
        return xpToNextLevel(this.level);
    }

    public static long xpToNextLevel(int level) {
        long base = Math.max(1L, ModCommonConfig.BASE_XP_TO_LEVEL_2.get());
        double multiplier = Math.max(0.0D, ModCommonConfig.XP_GROWTH_MULTIPLIER.get());
        if (multiplier <= 0.0D) {
            return base;
        }
        int currentLevel = Math.max(1, level);
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
        int refunded = this.intelligence + this.faith + this.arcane + this.strength + this.dexterity + this.constitution;
        this.intelligence = 0;
        this.faith = 0;
        this.arcane = 0;
        this.strength = 0;
        this.dexterity = 0;
        this.constitution = 0;
        addPoints(refunded);
    }

    @Override
    public long getLostRunes() {
        return lostRunes;
    }

    @Override
    public void setLostRunes(long value) {
        this.lostRunes = Math.max(0L, value);
    }

    @Override
    public Optional<GlobalPos> getBloodstainLocation() {
        return Optional.ofNullable(this.bloodstainLocation);
    }

    @Override
    public void setBloodstainLocation(@Nullable GlobalPos pos) {
        this.bloodstainLocation = pos;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_LEVEL, this.level);
        tag.putLong(KEY_STORED_RUNES, this.storedRunes);
        tag.putLong(LEGACY_KEY_XP, this.storedRunes);
        tag.putInt(KEY_POINTS, this.points);
        tag.putInt(KEY_INTELLIGENCE, this.intelligence);
        tag.putInt(KEY_FAITH, this.faith);
        tag.putInt(KEY_ARCANE, this.arcane);
        tag.putInt(KEY_STRENGTH, this.strength);
        tag.putInt(KEY_DEXTERITY, this.dexterity);
        tag.putInt(KEY_CONSTITUTION, this.constitution);
        tag.putLong(KEY_LOST_RUNES, this.lostRunes);
        if (this.bloodstainLocation != null) {
            tag.put(KEY_BLOODSTAIN, GlobalPos.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.bloodstainLocation).result().orElse(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.level = Math.max(1, tag.getInt(KEY_LEVEL));
        if (tag.contains(KEY_STORED_RUNES)) {
            this.storedRunes = Math.max(0L, tag.getLong(KEY_STORED_RUNES));
        } else {
            this.storedRunes = Math.max(0L, tag.getLong(LEGACY_KEY_XP));
        }
        this.points = Math.max(0, tag.getInt(KEY_POINTS));
        this.intelligence = Math.max(0, tag.getInt(KEY_INTELLIGENCE));
        this.faith = Math.max(0, tag.getInt(KEY_FAITH));
        this.arcane = Math.max(0, tag.getInt(KEY_ARCANE));
        this.strength = Math.max(0, tag.getInt(KEY_STRENGTH));
        this.dexterity = Math.max(0, tag.getInt(KEY_DEXTERITY));
        this.constitution = Math.max(0, tag.getInt(KEY_CONSTITUTION));
        this.lostRunes = Math.max(0L, tag.getLong(KEY_LOST_RUNES));
        if (tag.contains(KEY_BLOODSTAIN, Tag.TAG_COMPOUND)) {
            this.bloodstainLocation = GlobalPos.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.getCompound(KEY_BLOODSTAIN)).result().orElse(null);
        } else {
            this.bloodstainLocation = null;
        }
    }
}
