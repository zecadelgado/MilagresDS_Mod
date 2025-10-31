package com.stefani.MilagresDSMod.magic.visual.backend.playeranim;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public final class PlayerAnimatorCompat {
    private PlayerAnimatorCompat() {}

    public static void playClip(LivingEntity entity, String clipName) {
        if (!(entity instanceof Player player)) {
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
            var field = anims.getDeclaredField(clipName);
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
}
