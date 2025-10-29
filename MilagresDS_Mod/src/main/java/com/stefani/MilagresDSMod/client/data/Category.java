package com.stefani.MilagresDSMod.client.data;

import net.minecraft.network.chat.Component;

/**
 * Spell categories used for grouping on the memorize screen.
 */
public enum Category {
    OFFENSIVE("ui.memorize.category.offensive"),
    DEFENSIVE("ui.memorize.category.defensive"),
    SUPPORT("ui.memorize.category.support"),
    UTILITY("ui.memorize.category.utility"),
    RITUAL("ui.memorize.category.ritual");

    private final String translationKey;

    Category(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public String translationKey() {
        return translationKey;
    }
}
