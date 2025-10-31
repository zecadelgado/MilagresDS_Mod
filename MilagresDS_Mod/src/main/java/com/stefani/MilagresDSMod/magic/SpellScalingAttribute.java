package com.stefani.MilagresDSMod.magic;

import net.minecraft.network.chat.Component;

public enum SpellScalingAttribute {
    INTELLIGENCE("intelligence"),
    FAITH("faith"),
    ARCANE("arcane");

    private final String key;

    SpellScalingAttribute(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public Component displayName() {
        return Component.translatable("ui.attributes." + key);
    }
}
