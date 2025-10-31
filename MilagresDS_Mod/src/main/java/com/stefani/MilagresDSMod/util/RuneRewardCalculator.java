package com.stefani.MilagresDSMod.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;

public final class RuneRewardCalculator {
    private RuneRewardCalculator() {
    }

    public static long rewardFor(LivingEntity entity) {
        double maxHealth = Math.max(1.0D, entity.getMaxHealth());
        double base = Math.ceil(maxHealth);
        double multiplier = 1.0D;

        EntityType<?> type = entity.getType();
        MobCategory category = type.getCategory();
        if (category == MobCategory.MONSTER) {
            multiplier += 0.75D;
        } else if (category == MobCategory.CREATURE || category == MobCategory.WATER_CREATURE) {
            multiplier += 0.25D;
        }

        long experience = 0L;
        if (entity instanceof Mob mob) {
            experience = Math.max(0, mob.getExperienceReward());
            if (mob.isBaby()) {
                multiplier *= 0.6D;
            }
        }

        if (entity instanceof Player) {
            multiplier += 1.25D;
            experience += 50L;
        }

        long total = Math.round(base * multiplier) + experience;
        return Math.max(0L, total);
    }
}
