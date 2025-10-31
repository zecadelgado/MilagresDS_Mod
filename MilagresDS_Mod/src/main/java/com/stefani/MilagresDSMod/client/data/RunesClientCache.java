package com.stefani.MilagresDSMod.client.data;

import net.minecraft.core.GlobalPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class RunesClientCache {
    private static long activeRunes;
    private static long lostRunes;
    @Nullable
    private static GlobalPos bloodstainLocation;

    private RunesClientCache() {
    }

    public static void updateRunes(long active, long lost) {
        activeRunes = Math.max(0L, active);
        lostRunes = Math.max(0L, lost);
    }

    public static long activeRunes() {
        return activeRunes;
    }

    public static long lostRunes() {
        return lostRunes;
    }

    public static void updateBloodstain(@Nullable GlobalPos pos) {
        bloodstainLocation = pos;
    }

    public static Optional<GlobalPos> bloodstain() {
        return Optional.ofNullable(bloodstainLocation);
    }

    public static boolean hasBloodstain() {
        return bloodstainLocation != null;
    }
}
