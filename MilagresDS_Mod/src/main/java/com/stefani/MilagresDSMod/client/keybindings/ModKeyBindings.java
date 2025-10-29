package com.stefani.MilagresDSMod.client.keybindings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class ModKeyBindings {
    public static final String KEY_CATEGORY = "key.category.milagresdsmod";

    public static final KeyMapping OPEN_SPELL_MENU =
        new KeyMapping("key.milagresdsmod.open_spell_menu",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_G, KEY_CATEGORY);

    public static final KeyMapping CAST_SPELL =
        new KeyMapping("key.milagresdsmod.cast_spell",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_R, KEY_CATEGORY);

    private ModKeyBindings() {}
}
