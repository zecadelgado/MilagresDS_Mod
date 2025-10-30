package com.stefani.MilagresDSMod.registry;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.magic.spells.fireballspell;
import com.stefani.MilagresDSMod.magic.spells.healingburstspell;
import com.stefani.MilagresDSMod.magic.spells.lightningspear;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class spellregistry {
    public static final ResourceKey<Registry<spell>> SPELL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "spells"));

    public static final DeferredRegister<spell> SPELLS = DeferredRegister.create(SPELL_REGISTRY_KEY.location(), MilagresDSMod.MODID);

    public static final Supplier<IForgeRegistry<spell>> REGISTRY = SPELLS.makeRegistry(() ->
            new RegistryBuilder<spell>()
                    .setName(SPELL_REGISTRY_KEY.location())
                    .setDefaultKey(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "lightningspear"))
                    .setMaxID(Integer.MAX_VALUE - 1));

    public static final RegistryObject<spell> LIGHTNINGSPEAR = SPELLS.register("lightningspear", lightningspear::new);
    public static final RegistryObject<spell> FIREBALL = SPELLS.register("fireball", fireballspell::new);
    public static final RegistryObject<spell> HEALINGBURST = SPELLS.register("healingburst", healingburstspell::new);

}
