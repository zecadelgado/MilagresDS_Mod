package com.stefani.MilagresDSMod.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.capability.playermanaprovider;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MilagresCommands {
    private MilagresCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("milagres")
                .requires(source -> source.hasPermission(2));

        root.then(Commands.literal("mana")
                .then(Commands.literal("set")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(ctx -> setMana(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "value"))))));

        root.then(Commands.literal("spell")
                .then(Commands.literal("unlock")
                        .then(Commands.argument("spell", ResourceLocationArgument.id())
                                .executes(ctx -> changeUnlock(ctx.getSource(),
                                        ResourceLocationArgument.getId(ctx, "spell"), true))))
                .then(Commands.literal("lock")
                        .then(Commands.argument("spell", ResourceLocationArgument.id())
                                .executes(ctx -> changeUnlock(ctx.getSource(),
                                        ResourceLocationArgument.getId(ctx, "spell"), false)))));

        AttributesCommands.register(event.getDispatcher(), root);

        event.getDispatcher().register(root);
    }

    private static int setMana(CommandSourceStack source, int value) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return player.getCapability(playermanaprovider.PLAYER_MANA).map(mana -> {
            mana.setMana(Math.min(value, mana.getMaxMana()));
            modpackets.sendManaSync(player, mana.getMana(), mana.getMaxMana());
            source.sendSuccess(() -> Component.literal("Set mana to " + mana.getMana()), false);
            return mana.getMana();
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing mana capability"));
            return 0;
        });
    }

    private static int changeUnlock(CommandSourceStack source, ResourceLocation spellId, boolean unlock)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        var registry = spellregistry.REGISTRY.get();
        if (registry == null || !registry.containsKey(spellId)) {
            source.sendFailure(Component.literal("Unknown spell: " + spellId));
            return 0;
        }

        boolean[] applied = {false};
        player.getCapability(playerspellsprovider.PLAYER_SPELLS).ifPresent(spells -> {
            if (unlock) {
                spells.unlock(spellId);
                source.sendSuccess(() -> Component.literal("Unlocked spell " + spellId), false);
            } else {
                spells.lock(spellId);
                source.sendSuccess(() -> Component.literal("Locked spell " + spellId), false);
            }
            applied[0] = true;
        });
        if (!applied[0]) {
            source.sendFailure(Component.literal("Player is missing spell capability"));
            return 0;
        }
        return 1;
    }
}
