package com.stefani.MilagresDSMod.magic;

import com.stefani.MilagresDSMod.util.AttributeScaling;

import java.util.Objects;

public record SpellScaling(SpellScalingAttribute attribute, SpellScalingGrade grade, double coefficient) {
    public SpellScaling {
        Objects.requireNonNull(attribute, "attribute");
        Objects.requireNonNull(grade, "grade");
    }

    public double computeBonus(int value) {
        return AttributeScaling.computeBonus(value, coefficient);
    }
}
