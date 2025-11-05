package com.stefani.MilagresDSMod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.UUID;

/**
 * Utility class used on the client side to resolve the caster of a lightning
 * spear entity.  When the {@link com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity}
 * is deserialised on the client, its caster is not automatically resolved
 * because entity lookups require a client level.  This helper method looks up
 * the entity with the given UUID in the current client level and returns it
 * if it is a living entity.
 */
public final class LightningSpearClientAccess {
    private LightningSpearClientAccess() {
    }

    /**
     * Attempts to resolve an entity UUID to a living entity on the client.
     *
     * @param id the UUID of the caster sent over the network
     * @return the corresponding living entity or {@code null} if not present
     */
    public static LivingEntity resolveCaster(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        // Iterate over players in the current level and match on UUID.  Client levels
        // do not provide a direct lookup by UUID for arbitrary entities, so this loop
        // ensures the caster can be resolved even when only the UUID is known.
        for (net.minecraft.world.entity.player.Player player : mc.level.players()) {
            if (player.getUUID().equals(id)) {
                return player;
            }
        }
        // Fallback: search all entities rendered in the client world
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && entity.getUUID().equals(id)) {
                return living;
            }
        }
        return null;
    }
}