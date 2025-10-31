package com.stefani.MilagresDSMod.item;

import com.stefani.MilagresDSMod.entity.LightningSpearEntity;
import com.stefani.MilagresDSMod.entity.ModEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LightningSpearItem extends Item {
    public LightningSpearItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            LightningSpearEntity proj = new LightningSpearEntity(level, player);
            proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.6F, 0.99F);
            level.addFreshEntity(proj);
        }

        player.getCooldowns().addCooldown(this, 20);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
