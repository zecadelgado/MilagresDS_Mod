package com.stefani.MilagresDSMod.client;

import com.stefani.MilagresDSMod.MilagresDSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MilagresDSMod.MODID, value = Dist.CLIENT)
public final class LightningSpearClientAccess {
    private LightningSpearClientAccess() {
    }

    @Nullable
    public static LivingEntity resolveCaster(UUID uuid) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return null;
        }
        for (Entity entity : level.entitiesForRendering()) {
            if (entity.getUUID().equals(uuid) && entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }
}
