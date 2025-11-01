package com.stefani.MilagresDSMod.item;

import com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity;
import com.stefani.MilagresDSMod.registry.EntityRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningSpearItem extends Item {
    public LightningSpearItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            LightningSpearEntity spear = new LightningSpearEntity(EntityRegistry.LIGHTNING_SPEAR.get(), level);
            Vec3 spawnPos = player.position().add(0, player.getEyeHeight() * 0.7, 0);
            spear.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            Vec3 lookVec = player.getLookAngle();
            spear.configure(player, lookVec, 10);

            level.addFreshEntity(spear);
        }

        player.getCooldowns().addCooldown(this, 20);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
