package com.stefani.MilagresDSMod.util;

import com.stefani.MilagresDSMod.attribute.IPlayerAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates strength/dexterity coefficients for physical weapons.
 */
public final class WeaponScaling {
    private static final Map<ResourceLocation, WeaponScalingProfile> OVERRIDES = new HashMap<>();

    private static final WeaponScalingProfile DEFAULT_MELEE = new WeaponScalingProfile(1.0D, 0.028D, 0.014D);
    private static final WeaponScalingProfile DEFAULT_RANGED = new WeaponScalingProfile(1.0D, 0.008D, 0.036D);
    private static final WeaponScalingProfile DEFAULT_UNARMED = new WeaponScalingProfile(1.0D, 0.015D, 0.015D);

    static {
        register(Items.BOW, new WeaponScalingProfile(1.0D, 0.006D, 0.042D));
        register(Items.CROSSBOW, new WeaponScalingProfile(1.0D, 0.010D, 0.038D));
        register(Items.TRIDENT, new WeaponScalingProfile(1.0D, 0.032D, 0.020D));
    }

    private WeaponScaling() {
    }

    public static WeaponScalingProfile defaultMelee() {
        return DEFAULT_MELEE;
    }

    public static WeaponScalingProfile defaultRanged() {
        return DEFAULT_RANGED;
    }

    public static WeaponScalingProfile resolve(ItemStack stack, boolean projectile) {
        if (stack == null || stack.isEmpty()) {
            return projectile ? DEFAULT_RANGED : DEFAULT_UNARMED;
        }
        Item item = stack.getItem();
        WeaponScalingProfile override = OVERRIDES.get(BuiltInRegistries.ITEM.getKey(item));
        if (override != null) {
            return override;
        }
        if (item instanceof BowItem || item instanceof CrossbowItem) {
            return DEFAULT_RANGED;
        }
        if (projectile) {
            return DEFAULT_RANGED;
        }
        if (item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem) {
            return DEFAULT_MELEE;
        }
        return DEFAULT_UNARMED;
    }

    private static void register(Item item, WeaponScalingProfile profile) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id != null) {
            OVERRIDES.put(id, Objects.requireNonNull(profile));
        }
    }

    public record WeaponScalingProfile(double baseMultiplier, double strengthCoefficient, double dexterityCoefficient) {
        public double computeMultiplier(IPlayerAttributes attributes) {
            return baseMultiplier + computeBonus(attributes);
        }

        public double computeBonus(IPlayerAttributes attributes) {
            return computeStrengthBonus(attributes.getStrength()) + computeDexterityBonus(attributes.getDexterity());
        }

        public double computeBonus(int strength, int dexterity) {
            return computeStrengthBonus(strength) + computeDexterityBonus(dexterity);
        }

        public double computeStrengthBonus(int strength) {
            return AttributeScaling.computeBonus(strength, strengthCoefficient);
        }

        public double computeDexterityBonus(int dexterity) {
            return AttributeScaling.computeBonus(dexterity, dexterityCoefficient);
        }
    }
}
