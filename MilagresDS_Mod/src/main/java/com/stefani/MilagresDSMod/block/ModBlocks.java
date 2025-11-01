package com.stefani.MilagresDSMod.block;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MilagresDSMod.MODID);

    public static final RegistryObject<Block> GRACE_SITE = BLOCKS.register("grace_site",
            () -> new GraceSiteBlock(BlockBehaviour.Properties.copy(Blocks.SMOOTH_QUARTZ)
                    .lightLevel(state -> 10)
                    .strength(1.5F)
                    .noOcclusion()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    private ModBlocks() {
    }
}
