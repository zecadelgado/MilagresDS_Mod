package com.stefani.MilagresDSMod.client.data;

import com.stefani.MilagresDSMod.attribute.PlayerAttributes;

public final class AttributesClientCache {
    private static int level;
    private static int points;
    private static int intelligence;
    private static int faith;
    private static int arcane;
    private static int strength;
    private static int dexterity;
    private static int constitution;
    private static long storedRunes;
    private static long nextLevelCost;

    private AttributesClientCache() {
    }

    public static void update(int lvl, long runes, int availablePoints, int intel, int fai, int arc,
                              int str, int dex, int con) {
        level = Math.max(0, lvl);
        storedRunes = Math.max(0L, runes);
        nextLevelCost = PlayerAttributes.xpToNextLevel(Math.max(1, lvl));
        points = Math.max(0, availablePoints);
        intelligence = Math.max(0, intel);
        faith = Math.max(0, fai);
        arcane = Math.max(0, arc);
        strength = Math.max(0, str);
        dexterity = Math.max(0, dex);
        constitution = Math.max(0, con);
    }

    public static int level() {
        return level;
    }

    public static long storedRunes() {
        return storedRunes;
    }

    public static long nextLevelCost() {
        return nextLevelCost;
    }

    public static int points() {
        return points;
    }

    public static int intelligence() {
        return intelligence;
    }

    public static int faith() {
        return faith;
    }

    public static int arcane() {
        return arcane;
    }

    public static int strength() {
        return strength;
    }

    public static int dexterity() {
        return dexterity;
    }

    public static int constitution() {
        return constitution;
    }
}
