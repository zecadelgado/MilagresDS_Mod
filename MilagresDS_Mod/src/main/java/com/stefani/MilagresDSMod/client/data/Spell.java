package com.stefani.MilagresDSMod.client.data;

import com.stefani.MilagresDSMod.magic.SpellScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.List;

/**
 * Immutable client-side representation of a spell definition that is safe to cache on the UI layer.
 */
public record Spell(ResourceLocation id,
                    Component name,
                    Category category,
                    int manaCost,
                    Requirements requirements,
                    Component description,
                    ResourceLocation icon,
                    List<SpellScaling> scaling) {

    public Spell {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(requirements, "requirements");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(icon, "icon");
        this.scaling = List.copyOf(scaling);
    }
}
