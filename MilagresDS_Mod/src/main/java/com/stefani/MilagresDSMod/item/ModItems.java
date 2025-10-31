package com.stefani.MilagresDSMod.item;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MilagresDSMod.MODID);

    public static final RegistryObject<Item> LIGHTNING_SPEAR_ITEM = ITEMS.register("lightning_spear",
            () -> new LightningSpearItem(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)
                    .setNoRepair()));

    private ModItems() {}
}
