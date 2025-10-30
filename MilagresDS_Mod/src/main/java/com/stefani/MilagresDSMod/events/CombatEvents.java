package com.stefani.MilagresDSMod.events;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CombatEvents {
    private CombatEvents() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof ServerPlayer serverPlayer)) {
            return;
        }

        boolean isProjectile = event.getSource().getDirectEntity() instanceof Projectile;
        serverPlayer.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
            double multiplier = 1.0D;
            if (isProjectile) {
                multiplier += 0.05D * attributes.getDexterity();
            } else {
                multiplier += 0.05D * attributes.getStrength();
            }
            event.setAmount((float) (event.getAmount() * multiplier));
        });
    }
}
