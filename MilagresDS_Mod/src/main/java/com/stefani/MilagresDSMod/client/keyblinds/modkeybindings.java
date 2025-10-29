package com.stefani.MilagresDSMod.client.keyblinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class modkeybindings {
    public static final String KEY_CATEGORY = "key.category.milagresdsmod";

    public static final KeyMapping OPEN_SPELL_MENU = new KeyMapping("key.milagresdsmod.open_spell_menu", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, InputConstants.KEY_M, KEY_CATEGORY_MILAGES);

    public static final KeyMapping CAST_SPELL = new KeyMapping("key.MilagresDSMod.cast_spell", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, InputConstants.KEY_R, KEY_CATEGORY_MILAGES);
}