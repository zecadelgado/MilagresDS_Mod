package com.stefani.MilagresDSMod.item;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Central registry for all custom items defined by the mod.  The vanilla game
 * requires items to be registered at a specific time during mod loading.  The
 * {@link DeferredRegister} mechanism provided by Forge helps to coordinate
 * registration with the event bus.  Classes elsewhere in the mod reference
 * the fields in this class when they need access to a particular item.
 *
 * <p>The main mod class calls {@link #register(IEventBus)} during
 * construction to hook the deferred register into the mod event bus
 * {@link com.stefani.MilagresDSMod.MilagresDSMod#MilagresDSMod()} so that
 * registration occurs automatically.</p>
 */
public final class ModItems {
    private ModItems() {
    }

    /**
     * The deferred register instance used to register items.  Items added to
     * this register will be created with the mod's namespace and inserted into
     * the global item registry when the event bus fires the appropriate event.
     */
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MilagresDSMod.MODID);

    /**
     * Registry entry for the lightning spear item.  This item is used by
     * {@link com.stefani.MilagresDSMod.client.renderer.LightningSpearRenderer} to
     * display a spear icon when the {@link com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity}
     * is rendered in the world.  It has a stack size of one and uncommon
     * rarity to reflect its special nature.
     */
    public static final RegistryObject<Item> LIGHTNING_SPEAR_ITEM =
            ITEMS.register("lightning_spear_item", () -> new LightningSpearItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    /**
     * Registers the deferred register with the mod event bus.  This method
     * should be called during mod construction so that all items defined in
     * this class are registered at the correct time.
     *
     * @param eventBus the Forge mod event bus provided to the mod constructor
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}