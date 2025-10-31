package com.stefani.MilagresDSMod.magic;

public enum SpellScalingGrade {
    S(0.55D),
    A(0.42D),
    B(0.30D),
    C(0.20D),
    D(0.12D),
    E(0.06D);

    private final double baseCoefficient;

    SpellScalingGrade(double baseCoefficient) {
        this.baseCoefficient = baseCoefficient;
    }

    public double baseCoefficient() {
        return baseCoefficient;
    }
}
