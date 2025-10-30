package com.stefani.MilagresDSMod.magic;

import net.minecraft.network.chat.Component;

/**
 * Categories used to group spells. Shared between logical and client layers.
 */
public enum SpellCategory {
    OFFENSIVE("ui.memorize.category.offensive"),
    DEFENSIVE("ui.memorize.category.defensive"),
    SUPPORT("ui.memorize.category.support"),
    UTILITY("ui.memorize.category.utility"),
    RITUAL("ui.memorize.category.ritual");

    private final String translationKey;

    SpellCategory(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public String translationKey() {
        return translationKey;
    }
}
