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

        SPEC = builder.build();
    }

    private ModCommonConfig() {
    }
}
