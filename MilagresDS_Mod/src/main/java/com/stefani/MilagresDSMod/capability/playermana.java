package com.stefani.MilagresDSMod.capability;

public class playermana {
    private int mana;
    private int maxMana;

    public playermana() {
        this.mana = 100;
        this.maxMana = 100;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(1, maxMana);
        this.mana = Math.min(this.mana, this.maxMana);
    }

    public void setMana(int mana) {
        this.mana = Math.max(0, Math.min(mana, maxMana));
    }

    public void consumeMana(int amount) {
        this.mana = Math.max(0, this.mana - amount);
    }

    public void regenMana(int amount) {
        this.mana = Math.min(this.maxMana, this.mana + amount);
    }

    public boolean hasMana(int amount) {
        return this.mana >= amount;
    }
}
