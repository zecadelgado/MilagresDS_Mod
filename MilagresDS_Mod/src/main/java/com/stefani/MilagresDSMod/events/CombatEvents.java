package com.stefani.MilagresDSMod.events;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.attribute.playerattributesprovider;
import com.stefani.MilagresDSMod.util.WeaponScaling;
import com.stefani.MilagresDSMod.util.WeaponScaling.WeaponScalingProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
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
        ItemStack heldItem = serverPlayer.getMainHandItem();
        WeaponScalingProfile profile = WeaponScaling.resolve(heldItem, isProjectile);
        serverPlayer.getCapability(playerattributesprovider.PLAYER_ATTRIBUTES).ifPresent(attributes -> {
            double bonus = profile.computeBonus(attributes);
            double multiplier = profile.baseMultiplier() + bonus;
            event.setAmount((float) (event.getAmount() * multiplier));
        });
    }
}
