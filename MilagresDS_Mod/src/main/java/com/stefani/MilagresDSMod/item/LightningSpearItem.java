package com.stefani.MilagresDSMod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Simple item used to represent the Lightning Spear spell on the client.  The
 * server uses an {@link com.stefani.MilagresDSMod.magic.visual.lightning.LightningSpearEntity}
 * to actually perform the spell logic, but an {@link Item} is still required so
 * the renderer has something to display when the spear is in flight.  This
 * implementation does not add any special behaviour beyond displaying a
 * tooltip when hovered in an inventory.
 */
public class LightningSpearItem extends Item {
    public LightningSpearItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        // Provide a short description for the item.  Translatable text keys could be used here
        // if localisation support is desired, but a hard‑coded string suffices for a placeholder.
        tooltip.add(Component.translatable("item.milagresdsmod.lightning_spear.description")
                .withStyle(ChatFormatting.GRAY));
    }
}