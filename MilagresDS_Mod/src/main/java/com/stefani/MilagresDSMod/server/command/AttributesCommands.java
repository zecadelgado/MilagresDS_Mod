package com.stefani.MilagresDSMod.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.stefani.MilagresDSMod.attribute.IPlayerAttributes;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.config.ModCommonConfig;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.server.stats.ConstitutionApplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.function.BiConsumer;

public final class AttributesCommands {
    private AttributesCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                LiteralArgumentBuilder<CommandSourceStack> root) {
        dispatcher.getRoot();
        LiteralArgumentBuilder<CommandSourceStack> attrRoot = LiteralArgumentBuilder.literal("attr");
        attrRoot.then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                .executes(ctx -> showAttributes(ctx.getSource(), requirePlayer(ctx.getSource())))
                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> showAttributes(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        LiteralArgumentBuilder<CommandSourceStack> attrSet = LiteralArgumentBuilder.literal("set");
        appendAttributeModification(attrSet, false, false, (attributes, value) -> {
            attributes.setIntelligence(value.intValue());
        }, "int");
        appendAttributeModification(attrSet, false, false, (attributes, value) -> {
            attributes.setFaith(value.intValue());
        }, "faith");
        appendAttributeModification(attrSet, false, false, (attributes, value) -> {
            attributes.setArcane(value.intValue());
        }, "arcane");
        appendAttributeModification(attrSet, false, false, (attributes, value) -> {
            attributes.setStrength(value.intValue());
        }, "strength");
        appendAttributeModification(attrSet, false, false, (attributes, value) -> {
            attributes.setDexterity(value.intValue());
        }, "dexterity");
        appendAttributeModification(attrSet, false, false, (attributes, value) -> {
            attributes.setConstitution(value.intValue());
        }, "constitution");

        LiteralArgumentBuilder<CommandSourceStack> attrAdd = LiteralArgumentBuilder.literal("add");
        appendAttributeModification(attrAdd, true, true, (attributes, value) -> {
            attributes.setIntelligence(Math.max(0, attributes.getIntelligence() + value.intValue()));
        }, "int");
        appendAttributeModification(attrAdd, true, true, (attributes, value) -> {
            attributes.setFaith(Math.max(0, attributes.getFaith() + value.intValue()));
        }, "faith");
        appendAttributeModification(attrAdd, true, true, (attributes, value) -> {
            attributes.setArcane(Math.max(0, attributes.getArcane() + value.intValue()));
        }, "arcane");
        appendAttributeModification(attrAdd, true, true, (attributes, value) -> {
            attributes.setStrength(Math.max(0, attributes.getStrength() + value.intValue()));
        }, "strength");
        appendAttributeModification(attrAdd, true, true, (attributes, value) -> {
            attributes.setDexterity(Math.max(0, attributes.getDexterity() + value.intValue()));
        }, "dexterity");
        appendAttributeModification(attrAdd, true, true, (attributes, value) -> {
            attributes.setConstitution(Math.max(0, attributes.getConstitution() + value.intValue()));
        }, "constitution");

        attrRoot.then(attrSet);
        attrRoot.then(attrAdd);
        attrRoot.then(LiteralArgumentBuilder.<CommandSourceStack>literal("reset")
                .executes(ctx -> resetAttributes(ctx.getSource(), requirePlayer(ctx.getSource())))
                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> resetAttributes(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        root.then(attrRoot);

        LiteralArgumentBuilder<CommandSourceStack> levelRoot = LiteralArgumentBuilder.literal("level");
        levelRoot.then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                        .then(net.minecraft.commands.Commands.argument("level", IntegerArgumentType.integer(1))
                                .executes(ctx -> setLevel(ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"),
                                        IntegerArgumentType.getInteger(ctx, "level")))))
                .then(net.minecraft.commands.Commands.argument("level", IntegerArgumentType.integer(1))
                        .executes(ctx -> setLevel(ctx.getSource(), requirePlayer(ctx.getSource()),
                                IntegerArgumentType.getInteger(ctx, "level")))));
        root.then(levelRoot);

        LiteralArgumentBuilder<CommandSourceStack> xpRoot = LiteralArgumentBuilder.literal("xp");
        xpRoot.then(LiteralArgumentBuilder.<CommandSourceStack>literal("add")
                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                        .then(net.minecraft.commands.Commands.argument("amount", LongArgumentType.longArg(0))
                                .executes(ctx -> addXp(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
                                        LongArgumentType.getLong(ctx, "amount")))))
                .then(net.minecraft.commands.Commands.argument("amount", LongArgumentType.longArg(0))
                        .executes(ctx -> addXp(ctx.getSource(), requirePlayer(ctx.getSource()),
                                LongArgumentType.getLong(ctx, "amount")))));
        root.then(xpRoot);

        LiteralArgumentBuilder<CommandSourceStack> pointsRoot = LiteralArgumentBuilder.literal("points");
        pointsRoot.then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                        .then(net.minecraft.commands.Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(ctx -> setPoints(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
                                        IntegerArgumentType.getInteger(ctx, "value")))))
                .then(net.minecraft.commands.Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> setPoints(ctx.getSource(), requirePlayer(ctx.getSource()),
                                IntegerArgumentType.getInteger(ctx, "value")))));
        pointsRoot.then(LiteralArgumentBuilder.<CommandSourceStack>literal("add")
                .then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                        .then(net.minecraft.commands.Commands.argument("delta", IntegerArgumentType.integer(-1_000_000, 1_000_000))
                                .executes(ctx -> addPoints(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
                                        IntegerArgumentType.getInteger(ctx, "delta")))))
                .then(net.minecraft.commands.Commands.argument("delta", IntegerArgumentType.integer(-1_000_000, 1_000_000))
                        .executes(ctx -> addPoints(ctx.getSource(), requirePlayer(ctx.getSource()),
                                IntegerArgumentType.getInteger(ctx, "delta")))));
        root.then(pointsRoot);

        LiteralArgumentBuilder<CommandSourceStack> presetsRoot = LiteralArgumentBuilder.literal("set");
        presetsRoot.then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("atributos")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("max")
                                .executes(ctx -> applyMaxPreset(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))))));
        root.then(presetsRoot);
    }

    private static void appendAttributeModification(LiteralArgumentBuilder<CommandSourceStack> parent, boolean allowNegative,
                                                     boolean addition, BiConsumer<IPlayerAttributes, Number> applier,
                                                     String attributeKey) {
        int min = allowNegative ? -1_000_000 : 0;
        int max = 1_000_000;
        parent.then(net.minecraft.commands.Commands.argument("player", EntityArgument.player())
                .then(net.minecraft.commands.Commands.literal(attributeKey)
                        .then(net.minecraft.commands.Commands.argument("value", IntegerArgumentType.integer(min, max))
                                .executes(ctx -> executeAttributeChange(ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"),
                                        attributeKey, applier,
                                        IntegerArgumentType.getInteger(ctx, "value"), addition)))));
        parent.then(net.minecraft.commands.Commands.literal(attributeKey)
                .then(net.minecraft.commands.Commands.argument("value", IntegerArgumentType.integer(min, max))
                        .executes(ctx -> executeAttributeChange(ctx.getSource(),
                                requirePlayer(ctx.getSource()),
                                attributeKey, applier,
                                IntegerArgumentType.getInteger(ctx, "value"), addition))));
    }

    private static int resetAttributes(CommandSourceStack source, ServerPlayer target) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            attributes.resetAllAttributes();
            ConstitutionApplier.apply(target, attributes);
            modpackets.sendAttributesSync(target, attributes);
            source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.attrs_applied"), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int showAttributes(CommandSourceStack source, ServerPlayer target) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            long xpToNext = attributes.xpToNextLevel();
            source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT,
                    "Level: %d | XP: %d | Lost: %d | XP->Next: %d | Points: %d | STR: %d | DEX: %d | CON: %d | INT: %d | FAITH: %d | ARCANE: %d",
                    attributes.getLevel(), attributes.getXp(), attributes.getLostRunes(), xpToNext, attributes.getPoints(),
                    attributes.getStrength(), attributes.getDexterity(), attributes.getConstitution(),
                    attributes.getIntelligence(), attributes.getFaith(), attributes.getArcane())), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int setLevel(CommandSourceStack source, ServerPlayer target, int level) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            attributes.setLevel(level);
            modpackets.sendAttributesSync(target, attributes);
            source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.level_set", level), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int addXp(CommandSourceStack source, ServerPlayer target, long amount) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            attributes.addXp(amount);
            modpackets.sendAttributesSync(target, attributes);
            source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.xp_added", amount), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int setPoints(CommandSourceStack source, ServerPlayer target, int value) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            attributes.setPoints(value);
            modpackets.sendAttributesSync(target, attributes);
            source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.points_set", attributes.getPoints()), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int addPoints(CommandSourceStack source, ServerPlayer target, int delta) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            attributes.addPoints(delta);
            modpackets.sendAttributesSync(target, attributes);
            source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.attr_added",
                    formatAttributeName("points"), delta), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int applyMaxPreset(CommandSourceStack source, ServerPlayer target) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            int maxLevel = resolveMaxLevel();
            attributes.setLevel(maxLevel);
            attributes.setIntelligence(99);
            attributes.setFaith(99);
            attributes.setArcane(99);
            attributes.setStrength(99);
            attributes.setDexterity(99);
            attributes.setConstitution(99);
            attributes.setPoints(0);
            ConstitutionApplier.apply(target, attributes);
            modpackets.sendAttributesSync(target, attributes);
            modpackets.sendManaSync(target);
            source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.preset_max_applied"), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static int executeAttributeChange(CommandSourceStack source, ServerPlayer target, String key,
                                              BiConsumer<IPlayerAttributes, Number> applier, Number value,
                                              boolean addition) {
        return target.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).map(attributes -> {
            applier.accept(attributes, value);
            if ("constitution".equalsIgnoreCase(key)) {
                ConstitutionApplier.apply(target, attributes);
            }
            modpackets.sendAttributesSync(target, attributes);
            if (addition) {
                source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.attr_added",
                        formatAttributeName(key), value), false);
            } else {
                source.sendSuccess(() -> Component.translatable("msg.milagresdsmod.attr_set",
                        formatAttributeName(key), value), false);
            }
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Player is missing attribute capability"));
            return 0;
        });
    }

    private static String formatAttributeName(String key) {
        return key.toUpperCase(Locale.ROOT);
    }

    private static ServerPlayer requirePlayer(CommandSourceStack source) throws CommandSyntaxException {
        return source.getPlayerOrException();
    }

    private static int resolveMaxLevel() {
        try {
            Field field = ModCommonConfig.class.getField("MAX_LEVEL");
            Object value = field.get(null);
            if (value instanceof ForgeConfigSpec.IntValue intValue) {
                return Math.max(1, intValue.get());
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return 100;
    }
}
