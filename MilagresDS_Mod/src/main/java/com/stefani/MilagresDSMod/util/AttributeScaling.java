package com.stefani.MilagresDSMod.util;

import com.stefani.MilagresDSMod.magic.SpellScalingGrade;

/**
 * Utility helpers for applying softcap-aware attribute scaling across the mod.
 */
public final class AttributeScaling {
    private static final int[] DEFAULT_SOFTCAPS = new int[]{20, 40, 60};
    private static final double[] DEFAULT_SEGMENT_MULTIPLIERS = new double[]{1.0D, 0.6D, 0.3D, 0.1D};

    private AttributeScaling() {
    }

    /**
     * Applies the shared softcap curve to an attribute value. The first 20 points receive full value,
     * the next 20 grant 60%, the following 20 grant 30% and everything afterwards yields 10%.
     */
    public static double applySoftcaps(int value) {
        return applySoftcaps(value, DEFAULT_SOFTCAPS, DEFAULT_SEGMENT_MULTIPLIERS);
    }

    public static double applySoftcaps(int value, int[] thresholds, double[] multipliers) {
        if (value <= 0) {
            return 0.0D;
        }
        int sanitizedValue = Math.max(0, value);
        double result = 0.0D;
        int previousThreshold = 0;
        for (int i = 0; i < multipliers.length; i++) {
            double multiplier = multipliers[i];
            int cap = (i < thresholds.length) ? thresholds[i] : Integer.MAX_VALUE;
            int segment = Math.min(sanitizedValue, cap) - previousThreshold;
            if (segment > 0) {
                result += segment * Math.max(0.0D, multiplier);
            }
            previousThreshold = cap;
            if (sanitizedValue <= cap) {
                break;
            }
        }
        return result;
    }

    /**
     * Calculates a multiplicative bonus using the provided coefficient after applying softcaps.
     */
    public static double computeBonus(int value, double coefficient) {
        if (coefficient <= 0.0D) {
            return 0.0D;
        }
        return applySoftcaps(value) * coefficient;
    }

    /**
     * Helper for spell bonus previews based on a scaling grade.
     */
    public static double computeSpellBonus(int value, SpellScalingGrade grade) {
        if (grade == null) {
            return 0.0D;
        }
        return computeBonus(value, grade.baseCoefficient());
    }
}
