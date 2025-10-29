package com.stefani.MilagresDSMod.client.data;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable data structure describing attribute requirements to memorize a spell.
 */
public final class Requirements {
    private final int requiredLevel;
    private final int intelligence;
    private final int faith;
    private final int arcane;
    private final List<Component> additionalNotes;

    public Requirements(int requiredLevel, int intelligence, int faith, int arcane, List<Component> additionalNotes) {
        this.requiredLevel = Math.max(0, requiredLevel);
        this.intelligence = Math.max(0, intelligence);
        this.faith = Math.max(0, faith);
        this.arcane = Math.max(0, arcane);
        Objects.requireNonNull(additionalNotes, "additionalNotes");
        this.additionalNotes = Collections.unmodifiableList(new ArrayList<>(additionalNotes));
    }

    public static Requirements of(int requiredLevel, int intelligence, int faith, int arcane) {
        return new Requirements(requiredLevel, intelligence, faith, arcane, List.of());
    }

    public int requiredLevel() {
        return requiredLevel;
    }

    public int intelligence() {
        return intelligence;
    }

    public int faith() {
        return faith;
    }

    public int arcane() {
        return arcane;
    }

    public List<Component> additionalNotes() {
        return additionalNotes;
    }
}
