package com.stefani.MilagresDSMod.magic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class spell {
    private final String name;
    private final SpellProperties properties;
    private final List<SpellAction> actions;

    protected spell(String name, SpellProperties properties, SpellAction... actions) {
        this.name = name;
        this.properties = properties;
        this.actions = actions.length == 0 ? Collections.emptyList() : List.copyOf(Arrays.asList(actions));
    }

    public void cast(Player player, Level level) {
        SpellContext context = SpellContext.create(this, player, level);
        playCastSound(context);
        spawnCastParticles(context);
        for (SpellAction action : actions) {
            action.execute(context);
        }
        onCast(context);
    }

    protected void onCast(SpellContext context) {
    }

    protected void playCastSound(SpellContext context) {
        properties.getCastSound().ifPresent(sound -> sound.play(context.level(), context.player()));
    }

    protected void spawnCastParticles(SpellContext context) {
        properties.getCastParticles().ifPresent(particles -> particles.spawn(context.level(), context.origin()));
    }

    public String getName() {
        return this.name;
    }

    public Component getDisplayName() {
        return Component.translatable("spell.milagresdsmod." + this.name);
    }

    public int getManaCost() {
        return this.properties.getManaCost();
    }

    public int getCooldownTicks() {
        return this.properties.getCooldownTicks();
    }

    public ResourceLocation getIcon() {
        return this.properties.getIcon();
    }

    public SpellProperties getProperties() {
        return this.properties;
    }

    public SpellCategory getCategory() {
        return this.properties.getCategory();
    }

    public SpellRequirements getRequirements() {
        return this.properties.getRequirements();
    }

    public Optional<Float> getBaseDamage() {
        return this.properties.getBaseDamage();
    }

    public Optional<Float> getHealingAmount() {
        return this.properties.getHealingAmount();
    }

    public Optional<Component> getDescription() {
        return this.properties.getDescription();
    }

    public Optional<Component> getEffectSummary() {
        return this.properties.getEffectSummary();
    }
}
