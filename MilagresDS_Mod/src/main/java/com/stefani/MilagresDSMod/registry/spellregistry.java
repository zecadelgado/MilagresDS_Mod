package com.stefani.MilagresDSMod.registry;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.magic.spells.lightningspear;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class spellregistry {
    public static final DeferredRegister<spell> SPELLS = DeferredRegister.<spell>create(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "spells"), MilagresDSMod.MODID);

    public static final Supplier<IForgeRegistry<spell>> REGISTRY = SPELLS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<spell> LIGHTNINGSPEAR = SPELLS.register("lightningspear", lightningspear::new);

}
