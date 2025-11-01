package com.stefani.MilagresDSMod.magic.spells;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.SpellActions;
import com.stefani.MilagresDSMod.magic.SpellCategory;
import com.stefani.MilagresDSMod.magic.SpellProperties;
import com.stefani.MilagresDSMod.magic.SpellScalingAttribute;
import com.stefani.MilagresDSMod.magic.SpellScalingGrade;
import com.stefani.MilagresDSMod.magic.spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.LargeFireball;

public class fireballspell extends spell {
    public fireballspell() {
        super(
                "fireball",
                SpellProperties.builder()
                        .manaCost(35)
                        .cooldown(120)
                        .category(SpellCategory.OFFENSIVE)
                        .icon(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/spells/fireball.png"))
                        .castSound(SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F)
                        .castParticles(() -> ParticleTypes.FLAME, 24, 0.2D, 0.2D, 0.2D, 0.02D)
                        .baseDamage(14.0F)
                        .scaling(SpellScalingAttribute.INTELLIGENCE, SpellScalingGrade.A)
                        .scaling(SpellScalingAttribute.ARCANE, SpellScalingGrade.C)
                        .requirements(14, 16, 0, 0)
                        .description(Component.translatable("spell.milagresdsmod.fireball.desc"))
                        .effectSummary(Component.translatable("spell.milagresdsmod.fireball.effect"))
                        .build(),
                SpellActions.spawnProjectile(context -> {
                    LargeFireball fireball = new LargeFireball(
                            context.level(),
                            context.player(),
                            context.direction().x,
                            context.direction().y,
                            context.direction().z,
                            2
                    );
                    fireball.setPos(context.origin().x, context.origin().y, context.origin().z);
                    fireball.shoot(context.direction().x, context.direction().y, context.direction().z, 1.5F, 0.0F);
                    return fireball;
                })
        );
    }
}
