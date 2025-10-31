package com.stefani.MilagresDSMod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.stefani.MilagresDSMod.util.SpellVisuals;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public final class VisualTestCommands {
    private VisualTestCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vis_test")
                .then(Commands.literal("lightning").executes(ctx -> {
                    Player player = ctx.getSource().getPlayerOrException();
                    SpellVisuals.showLightningSpear(player.level(), player, player.getLookAngle());
                    return 1;
                }))
                .then(Commands.literal("flame").executes(ctx -> {
                    Player player = ctx.getSource().getPlayerOrException();
                    SpellVisuals.showFlameSling(player.level(), player, player.getLookAngle());
                    return 1;
                }))
                .then(Commands.literal("heal").executes(ctx -> {
                    Player player = ctx.getSource().getPlayerOrException();
                    SpellVisuals.showHeal(player.level(), player);
                    return 1;
                }))
        );
    }
}
