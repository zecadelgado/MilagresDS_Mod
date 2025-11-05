package com.stefani.MilagresDSMod.magic.spells;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.SpellActions;
import com.stefani.MilagresDSMod.magic.SpellCategory;
import com.stefani.MilagresDSMod.magic.SpellProperties;
import com.stefani.MilagresDSMod.magic.SpellScalingAttribute;
import com.stefani.MilagresDSMod.magic.SpellScalingGrade;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.util.SpellVisuals;
import com.stefani.MilagresDSMod.registry.ModParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class fireballspell extends spell {
    public fireballspell() {
        super(
                "fireball",
                SpellProperties.builder()
                        .manaCost(35)
                        .cooldown(120)
                        .category(SpellCategory.OFFENSIVE)
                        .icon(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/gui/spells/fireball.png"))
                        .castSound(SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F)
                        // Use our custom ember particle for the casting effect instead of the vanilla flame
                        .castParticles(() -> ModParticles.EMBER.get(), 24, 0.2D, 0.2D, 0.2D, 0.02D)
                        .baseDamage(14.0F)
                        .scaling(SpellScalingAttribute.INTELLIGENCE, SpellScalingGrade.A)
                        .scaling(SpellScalingAttribute.ARCANE, SpellScalingGrade.C)
                        .requirements(14, 16, 0, 0)
                        .description(Component.translatable("spell.milagresdsmod.fireball.desc"))
                        .effectSummary(Component.translatable("spell.milagresdsmod.fireball.effect"))
                        .build(),
                SpellActions.RunOnServer(context ->
                        SpellVisuals.showFlameSling(context.level(), context.player(), context.direction()))
        );
    }
}
