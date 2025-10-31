package com.stefani.MilagresDSMod.util;

import com.stefani.MilagresDSMod.magic.visual.flame.FlameSlingEntity;
import com.stefani.MilagresDSMod.magic.visual.heal.HealAreaEntity;
import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.network.packets.SpellLightS2CPacket;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

public final class SpellVisuals {
    private SpellVisuals() {}

    public static void showLightningSpear(Level level, LivingEntity caster, Vec3 dir) {
        if (level.isClientSide) {
            return;
        }
        LightningSpearEntity entity = EntityRegistry.LIGHTNING_SPEAR.get().create(level);
        if (entity == null) {
            return;
        }
        Vec3 hand = caster.position().add(0, caster.getEyeHeight() * 0.7, 0);
        entity.moveTo(hand.x, hand.y, hand.z);
        entity.setDeltaMovement(dir.normalize().scale(1.8));
        level.addFreshEntity(entity);
        tryCastPose(caster, "CastLightning");
        tryDynamicLight(level, entity, 0xF7E27A, 12f, 300);
    }

    public static void showFlameSling(Level level, LivingEntity caster, Vec3 dir) {
        if (level.isClientSide) {
            return;
        }
        FlameSlingEntity entity = EntityRegistry.FLAME_SLING.get().create(level);
        if (entity == null) {
            return;
        }
        Vec3 hand = caster.position().add(0, caster.getEyeHeight() * 0.65, 0);
        entity.moveTo(hand.x, hand.y, hand.z);
        entity.setDeltaMovement(dir.normalize().scale(1.2));
        level.addFreshEntity(entity);
        tryCastPose(caster, "CastFlame");
        tryDynamicLight(level, entity, 0xFF6A2A, 10f, 500);
    }

    public static void showHeal(Level level, LivingEntity caster) {
        if (level.isClientSide) {
            return;
        }
        HealAreaEntity entity = EntityRegistry.HEAL_AREA.get().create(level);
        if (entity == null) {
            return;
        }
        entity.setOwner(caster);
        entity.moveTo(caster.getX(), caster.getY() - 0.1, caster.getZ());
        level.addFreshEntity(entity);
        tryCastPose(caster, "CastHeal");
        tryDynamicLight(level, entity, 0xF9EFAF, 8f, 3600);
    }

    private static void tryCastPose(LivingEntity caster, String animName) {
        if (!(caster instanceof Player player)) {
            return;
        }
        if (!ModList.get().isLoaded("playeranimator")) {
            return;
        }
        try {
            Class<?> accessClass = Class.forName("dev.kosmx.playerAnim.api.layered.PlayerAnimationAccess");
            var getData = accessClass.getMethod("getPlayerAssociatedData", Player.class);
            Object data = getData.invoke(null, player);
            if (data == null) {
                return;
            }
            Class<?> anims = Class.forName("com.stefani.MilagresDSMod.magic.visual.backend.playeranim.MyCastAnimations");
            var field = anims.getDeclaredField(animName);
            Object clip = field.get(null);
            if (clip == null) {
                return;
            }
            Class<?> modifierLayer = Class.forName("dev.kosmx.playerAnim.api.layered.ModifierLayer");
            var set = data.getClass().getMethod("setAnimation", modifierLayer);
            set.invoke(data, clip);
        } catch (Throwable ignored) {
        }
    }

    private static void tryDynamicLight(Level level, Entity entity, int rgb, float radius, int durationMs) {
        if (level.isClientSide || !(level instanceof ServerLevel)) {
            return;
        }
        int durationTicks = Math.max(1, durationMs / 50);
        modpackets.sendTracking(entity, new SpellLightS2CPacket(entity.getId(), rgb, radius, durationTicks));
    }
}
