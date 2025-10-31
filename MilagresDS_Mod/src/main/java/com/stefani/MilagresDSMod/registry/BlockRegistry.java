package com.stefani.MilagresDSMod.registry;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.block.BloodstainBlock;
import com.stefani.MilagresDSMod.block.entity.BloodstainBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockRegistry {
    private BlockRegistry() {
    }

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MilagresDSMod.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MilagresDSMod.MODID);

    public static final RegistryObject<Block> BLOODSTAIN_BLOCK = BLOCKS.register("bloodstain",
            () -> new BloodstainBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(-1.0F, 3600000.0F)
                    .noOcclusion()
                    .noCollission()
                    .noLootTable()));

    public static final RegistryObject<BlockEntityType<BloodstainBlockEntity>> BLOODSTAIN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("bloodstain",
                    () -> BlockEntityType.Builder.of(BloodstainBlockEntity::new, BLOODSTAIN_BLOCK.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}
