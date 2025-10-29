package com.stefani.MilagresDSMod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        COMMON = new Common(b);
        COMMON_SPEC = b.build();
    }

    private ModConfig() {}

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }

    public static final class Common {
        public final ForgeConfigSpec.IntValue maxMana;
        public final ForgeConfigSpec.IntValue manaRegenPerTick;
        public final ForgeConfigSpec.BooleanValue showHud;

        Common(ForgeConfigSpec.Builder b) {
            b.push("mana");
            maxMana = b.defineInRange("maxMana", 100, 1, 10000);
            manaRegenPerTick = b.defineInRange("manaRegenPerTick", 1, 0, 100);
            b.pop();
            showHud = b.define("showHud", true);
        }
    }
}
