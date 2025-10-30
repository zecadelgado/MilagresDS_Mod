package com.stefani.MilagresDSMod.magic.spells;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.SpellActions;
import com.stefani.MilagresDSMod.magic.SpellCategory;
import com.stefani.MilagresDSMod.magic.SpellProperties;
import com.stefani.MilagresDSMod.magic.spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.DragonFireball;

public class lightningspear extends spell {
    public lightningspear() {
        super(
                "lightningspear",
                SpellProperties.builder()
                        .manaCost(20)
                        .cooldown(100)
                        .category(SpellCategory.OFFENSIVE)
                        .icon(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/gui/spells/fireball.png"))
                        .castSound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 0.8F)
                        .castParticles(() -> ParticleTypes.ELECTRIC_SPARK, 20, 0.2D, 0.2D, 0.2D, 0.01D)
                        .baseDamage(12.0F)
                        .requirements(10, 12, 0, 0)
                        .description(Component.translatable("spell.milagresdsmod.lightningspear.desc"))
                        .effectSummary(Component.translatable("spell.milagresdsmod.lightningspear.effect"))
                        .build(),
                SpellActions.spawnProjectile(context -> {
                    DragonFireball spear = new DragonFireball(
                            context.level(),
                            context.player(),
                            context.direction().x,
                            context.direction().y,
                            context.direction().z
                    );
                    spear.setPos(context.origin().x, context.origin().y, context.origin().z);
                    spear.shoot(context.direction().x, context.direction().y, context.direction().z, 1.6F, 0.0F);
                    return spear;
                })
        );
    }
}
