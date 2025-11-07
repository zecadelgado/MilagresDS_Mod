package com.stefani.MilagresDSMod.magic.spells;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.SpellActions;
import com.stefani.MilagresDSMod.magic.SpellCategory;
import com.stefani.MilagresDSMod.magic.SpellProperties;
import com.stefani.MilagresDSMod.magic.SpellScalingAttribute;
import com.stefani.MilagresDSMod.magic.SpellScalingGrade;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.registry.ModParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class lightningspear extends spell {

    public lightningspear() {
        super(
                "lightningspear",
                SpellProperties.builder()
                        .manaCost(20)
                        .cooldown(100)
                        .category(SpellCategory.OFFENSIVE)
                        .icon(new ResourceLocation(MilagresDSMod.MODID, "textures/gui/spells/lightningspear.png"))
                        .castSound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 0.8F)
                        .castParticles(ModParticles.LIGHTNING_SPARK, 20, 0.2D, 0.2D, 0.2D, 0.01D)
                        .baseDamage(12.0F)
                        .scaling(SpellScalingAttribute.FAITH, SpellScalingGrade.S)
                        .scaling(SpellScalingAttribute.ARCANE, SpellScalingGrade.E)
                        .requirements(10, 12, 0, 0)
                        .description(Component.translatable("spell.milagresdsmod.lightningspear.desc"))
                        .effectSummary(Component.translatable("spell.milagresdsmod.lightningspear.effect"))
                        .build(),

                // Ação correta como lambda:
                SpellActions.RunOnServer(context -> {
                    Level level = context.level();
                    Player player = context.player();
                    Vec3 direction = context.direction();

                    if (level != null && !level.isClientSide) {
                        com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity spear =
                                new com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity(
                                        com.stefani.MilagresDSMod.registry.ModEntities.LIGHTNING_SPEAR.get(), level);

                        if (player != null) {
                            spear.setOwner(player);
                        }

                        Vec3 dir = (direction != null)
                                ? direction
                                : (player != null ? player.getLookAngle() : new Vec3(0, 0, 1));

                        spear.configure(player, dir, 8);
                        level.addFreshEntity(spear);
                    }
                })
        );
    }
}
