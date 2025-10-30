package com.stefani.MilagresDSMod.client.data;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.SpellProperties;
import com.stefani.MilagresDSMod.magic.SpellRequirements;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client-side adapter that mirrors the logical spell registry so UIs can render real spell data.
 */
public final class SpellRegistryClient {
    private static final ResourceLocation FALLBACK_ICON = ResourceLocation.fromNamespaceAndPath(
            MilagresDSMod.MODID, "textures/spells/fallback.png");

    private SpellRegistryClient() {
    }

    public static List<Spell> getAll() {
        return snapshot().spells();
    }

    public static Optional<Spell> get(ResourceLocation id) {
        return Optional.ofNullable(snapshot().lookup().get(id));
    }

    public static ResourceLocation fallbackIcon() {
        return FALLBACK_ICON;
    }

    private static SpellSnapshot snapshot() {
        IForgeRegistry<spell> registry = spellregistry.REGISTRY.get();
        if (registry == null) {
            return SpellSnapshot.EMPTY;
        }

        List<spell> registryValues = new ArrayList<>(registry.getValues());
        registryValues.sort((a, b) -> keyString(registry, a).compareTo(keyString(registry, b)));

        List<Spell> spells = new ArrayList<>();
        Map<ResourceLocation, Spell> byId = new LinkedHashMap<>();
        for (spell value : registryValues) {
            ResourceLocation id = registry.getKey(value);
            if (id == null) {
                continue;
            }
            Spell clientSpell = toClientSpell(id, value);
            spells.add(clientSpell);
            byId.put(id, clientSpell);
        }

        return new SpellSnapshot(List.copyOf(spells), Collections.unmodifiableMap(byId));
    }

    private static String keyString(IForgeRegistry<spell> registry, spell value) {
        ResourceLocation key = registry.getKey(value);
        return key != null ? key.toString() : "";
    }

    private static Spell toClientSpell(ResourceLocation id, spell value) {
        SpellProperties properties = value.getProperties();
        SpellRequirements requirements = properties.getRequirements();
        Requirements clientRequirements = new Requirements(
                requirements.requiredLevel(),
                requirements.intelligence(),
                requirements.faith(),
                requirements.arcane(),
                requirements.additionalNotes());
        Component description = properties.getDescription().orElse(Component.empty());
        ResourceLocation icon = properties.getIcon() != null ? properties.getIcon() : FALLBACK_ICON;
        return new Spell(id,
                value.getDisplayName(),
                Category.fromSpellCategory(properties.getCategory()),
                properties.getManaCost(),
                clientRequirements,
                description,
                icon);
    }

    private record SpellSnapshot(List<Spell> spells, Map<ResourceLocation, Spell> lookup) {
        private static final SpellSnapshot EMPTY = new SpellSnapshot(List.of(), Map.of());
    }
}
