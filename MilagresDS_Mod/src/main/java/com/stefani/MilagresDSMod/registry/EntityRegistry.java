package com.stefani.MilagresDSMod.registry;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import com.stefani.MilagresDSMod.magic.visual.heal.HealAreaEntity;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MilagresDSMod.MODID);

    public static final RegistryObject<EntityType<LightningSpearEntity>> LIGHTNING_SPEAR =
            REGISTRY.register("lightning_spear", () ->
                    EntityType.Builder.<LightningSpearEntity>of(LightningSpearEntity::new, MobCategory.MISC)
                            .sized(0.3f, 0.3f).setTrackingRange(64).setUpdateInterval(1)
                            .build(new ResourceLocation(MilagresDSMod.MODID, "lightning_spear").toString()));

    public static final RegistryObject<EntityType<FlameSlingEntity>> FLAME_SLING =
            REGISTRY.register("flame_sling", () ->
                    EntityType.Builder.<FlameSlingEntity>of(FlameSlingEntity::new, MobCategory.MISC)
                            .sized(0.4f, 0.4f).setTrackingRange(64).setUpdateInterval(2)
                            .build(new ResourceLocation(MilagresDSMod.MODID, "flame_sling").toString()));

    public static final RegistryObject<EntityType<HealAreaEntity>> HEAL_AREA =
            REGISTRY.register("heal_area", () ->
                    EntityType.Builder.<HealAreaEntity>of(HealAreaEntity::new, MobCategory.MISC)
                            .sized(0.1f, 0.1f).setTrackingRange(64).setUpdateInterval(10)
                            .build(new ResourceLocation(MilagresDSMod.MODID, "heal_area").toString()));

    private EntityRegistry() {}
}
