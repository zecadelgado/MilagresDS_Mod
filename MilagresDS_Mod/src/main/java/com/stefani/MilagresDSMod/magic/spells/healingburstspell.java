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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class healingburstspell extends spell {
    public healingburstspell() {
        super(
                "healingburst",
                SpellProperties.builder()
                        .manaCost(30)
                        .cooldown(160)
                        .category(SpellCategory.SUPPORT)
                        .icon(ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/gui/spells/healingburst.png"))
                        .castSound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.2F)
                        // Use our custom heal glow particle instead of the vanilla heart particles
                        .castParticles(() -> ModParticles.HEAL_GLOW.get(), 18, 0.35D, 0.5D, 0.35D, 0.02D)
                        .healingAmount(8.0F)
                        .scaling(SpellScalingAttribute.FAITH, SpellScalingGrade.A)
                        .requirements(8, 0, 12, 0)
                        .description(Component.translatable("spell.milagresdsmod.healingburst.desc"))
                        .effectSummary(Component.translatable("spell.milagresdsmod.healingburst.effect"))
                        .build(),
                SpellActions.runOnServer(context ->
                        SpellVisuals.showHeal(context.level(), context.player())),
                SpellActions.healPlayer(8.0F),
                SpellActions.applyEffect(() -> new MobEffectInstance(MobEffects.REGENERATION, 120, 0))
        );
    }
}
