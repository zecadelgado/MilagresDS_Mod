package com.stefani.MilagresDSMod.server.stats;

import com.stefani.MilagresDSMod.attribute.IPlayerAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class ConstitutionApplier {
    private static final UUID CONSTITUTION_HEALTH_UUID = UUID.fromString("b5a5d6a2-6f2a-4d3d-8ffe-2f1f8a5000c1");

    private ConstitutionApplier() {
    }

    public static void apply(ServerPlayer player, IPlayerAttributes attributes) {
        if (player == null || attributes == null) {
            return;
        }
        AttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
        if (instance == null) {
            return;
        }

        instance.removeModifier(CONSTITUTION_HEALTH_UUID);
        int constitution = Math.max(0, attributes.getConstitution());
        if (constitution > 0) {
            double multiplier = 0.05D * constitution;
            AttributeModifier modifier = new AttributeModifier(CONSTITUTION_HEALTH_UUID,
                    "MilagresDS Constitution Bonus",
                    multiplier,
                    AttributeModifier.Operation.MULTIPLY_BASE);
            instance.addPermanentModifier(modifier);
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
