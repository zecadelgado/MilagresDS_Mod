package com.stefani.MilagresDSMod.entity;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MilagresDSMod.MODID);

    public static final RegistryObject<EntityType<LightningSpearEntity>> LIGHTNING_SPEAR =
            ENTITIES.register("lightning_spear", () ->
                    EntityType.Builder.<LightningSpearEntity>of(LightningSpearEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(new ResourceLocation(MilagresDSMod.MODID, "lightning_spear").toString()));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
