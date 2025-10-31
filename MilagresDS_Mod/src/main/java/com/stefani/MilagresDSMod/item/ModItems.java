package com.stefani.MilagresDSMod.item;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
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

    public static final RegistryObject<Item> GRACE_SITE_ITEM = ITEMS.register("grace_site",
            () -> new BlockItem(ModBlocks.GRACE_SITE.get(), new Item.Properties()));

    private ModItems() {}
}
