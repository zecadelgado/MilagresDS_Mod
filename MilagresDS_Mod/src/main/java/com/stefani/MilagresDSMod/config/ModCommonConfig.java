package com.stefani.MilagresDSMod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModCommonConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue MAX_MANA;
    public static final ForgeConfigSpec.IntValue MANA_REGEN_PER_TICK;
    public static final ForgeConfigSpec.DoubleValue COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue COOLDOWN_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue MIN_TICKS_BETWEEN_CASTS;
    public static final ForgeConfigSpec.BooleanValue SHOW_HUD;
    public static final ForgeConfigSpec.IntValue STARTING_LEVEL;
    public static final ForgeConfigSpec.LongValue BASE_XP_TO_LEVEL_2;
    public static final ForgeConfigSpec.DoubleValue XP_GROWTH_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue STARTING_POINTS;
    public static final ForgeConfigSpec.IntValue POINTS_PER_LEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("mana");
        MAX_MANA = builder.comment("Maximum mana a player can have.")
                .defineInRange("maxMana", 100, 1, 10000);
        MANA_REGEN_PER_TICK = builder.comment("Mana regenerated per server tick.")
                .defineInRange("manaRegenPerTick", 1, 0, 100);
        COST_MULTIPLIER = builder.comment("Multiplier applied to spell mana costs.")
                .defineInRange("costMultiplier", 1.0D, 0.0D, 100.0D);
        COOLDOWN_MULTIPLIER = builder.comment("Multiplier applied to spell cooldown times.")
                .defineInRange("cooldownMultiplier", 1.0D, 0.0D, 100.0D);
        MIN_TICKS_BETWEEN_CASTS = builder.comment("Minimum ticks between spell casts from the same player.")
                .defineInRange("minTicksBetweenCasts", 4, 0, 1200);
        SHOW_HUD = builder.comment("Whether the mana HUD overlay should be displayed on the client.")
                .define("showHud", true);
        builder.pop();

        builder.push("attributes");
        STARTING_LEVEL = builder.comment("Initial level for players when the attribute capability is first created.")
                .defineInRange("startingLevel", 1, 1, Integer.MAX_VALUE);
        BASE_XP_TO_LEVEL_2 = builder.comment("Base XP required to reach level 2 from level 1.")
                .defineInRange("baseXpToLevel2", 100L, 1L, Long.MAX_VALUE);
        XP_GROWTH_MULTIPLIER = builder.comment("Multiplier applied to XP requirement for each subsequent level.")
                .defineInRange("xpGrowthMultiplier", 1.35D, 0.1D, 100.0D);
        STARTING_POINTS = builder.comment("Attribute points granted when the capability is first created.")
                .defineInRange("startingPoints", 0, 0, Integer.MAX_VALUE);
        POINTS_PER_LEVEL = builder.comment("Attribute points awarded on each level-up.")
                .defineInRange("pointsPerLevel", 2, 0, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }

    private ModCommonConfig() {
    }
}
