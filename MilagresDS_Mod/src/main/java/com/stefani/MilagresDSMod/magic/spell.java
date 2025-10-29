package com.stefani.MilagresDSMod.magic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class spell {
private final String name;
private final int manaCost;
private final int cooldownTicks;
private final ResourceLocation icon;

public spell(String name, int manaCost, int cooldownTicks, ResourceLocation icon){
    this.name = name;
    this.manaCost = manaCost;
    this.cooldownTicks = cooldownTicks;
    this.icon = icon;
}
public abstract void cast(Player player, Level level);

public String getName() {
    return this.name;
}
public Component getDisplayName() {
    return Component.translatable("spell.milagresdsmod." + this.name);
}
public int getManaCost() {
    return this.manaCost;
}
public int getCooldownTicks() {
    return this.cooldownTicks;
}
public ResourceLocation getIcon(){
    return this.icon;
}
}
