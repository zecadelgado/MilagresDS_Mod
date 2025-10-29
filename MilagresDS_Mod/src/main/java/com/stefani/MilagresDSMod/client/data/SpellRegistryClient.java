package com.stefani.MilagresDSMod.client.data;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Static client-side registry with showcase spells used exclusively for UI previews and local testing.
 */
public final class SpellRegistryClient {
    private static final ResourceLocation FALLBACK_ICON = ResourceLocation.fromNamespaceAndPath(
            MilagresDSMod.MODID, "textures/spells/fallback.png");

    private static final List<Spell> SPELLS;
    private static final Map<ResourceLocation, Spell> SPELLS_BY_ID;

    static {
        List<Spell> spells = new ArrayList<>();
        spells.add(build("lightning_spear", Category.OFFENSIVE, 24,
                new Requirements(12, 8, 16, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 1))),
                Component.translatable("spell.milagresdsmod.lightning_spear.desc"),
                icon("lightning_spear")));
        spells.add(build("arcane_barrage", Category.OFFENSIVE, 32,
                new Requirements(18, 20, 6, 12, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.arcane_barrage.desc"),
                icon("arcane_barrage")));
        spells.add(build("ember_comet", Category.OFFENSIVE, 40,
                new Requirements(24, 15, 10, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.ember_comet.desc"),
                icon("ember_comet")));
        spells.add(build("crystalline_shield", Category.DEFENSIVE, 18,
                new Requirements(10, 12, 6, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 1))),
                Component.translatable("spell.milagresdsmod.crystalline_shield.desc"),
                icon("crystalline_shield")));
        spells.add(build("healing_burst", Category.SUPPORT, 26,
                new Requirements(14, 0, 18, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.healing_burst.desc"),
                icon("healing_burst")));
        spells.add(build("sanctuary_barrier", Category.SUPPORT, 30,
                new Requirements(20, 10, 22, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.sanctuary_barrier.desc"),
                icon("sanctuary_barrier")));
        spells.add(build("wind_step", Category.UTILITY, 12,
                new Requirements(8, 8, 0, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 1))),
                Component.translatable("spell.milagresdsmod.wind_step.desc"),
                icon("wind_step")));
        spells.add(build("gravity_well", Category.RITUAL, 44,
                new Requirements(28, 24, 12, 14, List.of(Component.translatable("ui.memorize.requirement.slots", 3))),
                Component.translatable("spell.milagresdsmod.gravity_well.desc"),
                icon("gravity_well")));
        spells.add(build("soul_chain", Category.RITUAL, 36,
                new Requirements(26, 18, 18, 8, List.of(Component.translatable("ui.memorize.requirement.slots", 3))),
                Component.translatable("spell.milagresdsmod.soul_chain.desc"),
                icon("soul_chain")));
        spells.add(build("toxic_mist", Category.OFFENSIVE, 22,
                new Requirements(12, 14, 4, 6, List.of(Component.translatable("ui.memorize.requirement.slots", 1))),
                Component.translatable("spell.milagresdsmod.toxic_mist.desc"),
                icon("toxic_mist")));
        spells.add(build("earthen_bulwark", Category.DEFENSIVE, 28,
                new Requirements(22, 0, 16, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.earthen_bulwark.desc"),
                icon("earthen_bulwark")));
        spells.add(build("lunar_ray", Category.OFFENSIVE, 34,
                new Requirements(25, 20, 10, 12, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.lunar_ray.desc"),
                icon("lunar_ray")));
        spells.add(build("blood_oath", Category.RITUAL, 50,
                new Requirements(30, 12, 24, 18, List.of(Component.translatable("ui.memorize.requirement.slots", 3))),
                Component.translatable("spell.milagresdsmod.blood_oath.desc"),
                icon("blood_oath")));
        spells.add(build("void_prison", Category.OFFENSIVE, 38,
                new Requirements(26, 22, 8, 16, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.void_prison.desc"),
                icon("void_prison")));
        spells.add(build("divine_wrath", Category.OFFENSIVE, 42,
                new Requirements(28, 10, 28, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 3))),
                Component.translatable("spell.milagresdsmod.divine_wrath.desc"),
                icon("divine_wrath")));
        spells.add(build("astral_blessing", Category.SUPPORT, 20,
                new Requirements(16, 8, 20, 6, List.of(Component.translatable("ui.memorize.requirement.slots", 1))),
                Component.translatable("spell.milagresdsmod.astral_blessing.desc"),
                icon("astral_blessing")));
        spells.add(build("flame_torrent", Category.OFFENSIVE, 28,
                new Requirements(18, 18, 8, 0, List.of(Component.translatable("ui.memorize.requirement.slots", 2))),
                Component.translatable("spell.milagresdsmod.flame_torrent.desc"),
                icon("flame_torrent")));
        spells.add(build("ethereal_veil", Category.UTILITY, 16,
                new Requirements(14, 10, 6, 8, List.of(Component.translatable("ui.memorize.requirement.slots", 1))),
                Component.translatable("spell.milagresdsmod.ethereal_veil.desc"),
                icon("ethereal_veil")));

        List<Spell> immutable = List.copyOf(spells);
        Map<ResourceLocation, Spell> map = new LinkedHashMap<>();
        for (Spell spell : immutable) {
            map.put(spell.id(), spell);
        }
        SPELLS = immutable;
        SPELLS_BY_ID = Collections.unmodifiableMap(map);
    }

    private SpellRegistryClient() {
    }

    private static Spell build(String name, Category category, int manaCost, Requirements requirements,
                               Component description, ResourceLocation icon) {
        ResourceLocation id = new ResourceLocation(MilagresDSMod.MODID, name);
        return new Spell(id,
                Component.translatable("spell.milagresdsmod." + name),
                category,
                manaCost,
                requirements,
                description,
                icon);
    }

    private static ResourceLocation icon(String name) {
        return ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/spells/" + name + ".png");
    }

    public static List<Spell> getAll() {
        return SPELLS;
    }

    public static Optional<Spell> get(ResourceLocation id) {
        return Optional.ofNullable(SPELLS_BY_ID.get(id));
    }

    public static ResourceLocation fallbackIcon() {
        return FALLBACK_ICON;
    }
}
