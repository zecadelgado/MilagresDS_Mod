package com.stefani.MilagresDSMod.magic.spells;
import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.magic.spell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;

public class lightningspear extends spell {
    public lightningspear() {
        super(
                "lightningspear",
                20,
                100,
                ResourceLocation.fromNamespaceAndPath(MilagresDSMod.MODID, "textures/gui/spells/fireball.png")
        );
    }
@Override
public void cast(Player player, Level level) {
    if (!level.isClientSide) {
        DragonFireball lightningspear = new DragonFireball(level,player, player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z);
        lightningspear.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        level.addFreshEntity(lightningspear);

        level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
}
