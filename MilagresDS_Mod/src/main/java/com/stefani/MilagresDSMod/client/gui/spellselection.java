package com.stefani.MilagresDSMod.client.gui;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class spellselection extends Screen {
    private static final int ICON_SIZE = 24;
    private static final int ICON_SPACING = 8;

    private final List<SpellEntry> spellEntries = new ArrayList<>();
    private ResourceLocation selectedSpellId;

    public spellselection() {
        super(Component.literal("Spell Selection"));
    }

    @Override
    protected void init() {
        super.init();
        spellEntries.clear();
        selectedSpellId = null;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.getCapability(playerspellsprovider.PLAYER_SPELLS)
                    .ifPresent(spells -> selectedSpellId = spells.getEquippedSpellId());
        }

        List<spell> spells = new ArrayList<>(spellregistry.REGISTRY.get().getValues());
        if (spells.isEmpty()) {
            return;
        }

        int totalWidth = spells.size() * ICON_SIZE + (spells.size() - 1) * ICON_SPACING;
        int startX = (this.width - totalWidth) / 2;
        int y = this.height / 2 - ICON_SIZE / 2;

        int currentX = startX;
        for (spell spell : spells) {
            ResourceLocation spellId = spellregistry.REGISTRY.get().getKey(spell);
            if (spellId == null) {
                continue;
            }
            spellEntries.add(new SpellEntry(spell, spellId, currentX, y));
            currentX += ICON_SIZE + ICON_SPACING;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Fundo escuro semi-transparente
        this.renderBackground(guiGraphics);

        // Título
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        if (spellEntries.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, Component.literal("Nenhuma magia disponível"), this.width / 2, this.height / 2 - 10, 0xAAAAAA);
        } else {
            spellEntries.forEach(entry -> {
                if (Objects.equals(entry.id(), selectedSpellId)) {
                    guiGraphics.fill(entry.x() - 2, entry.y() - 2, entry.x() + ICON_SIZE + 2, entry.y() + ICON_SIZE + 2, 0x80FFFFFF);
                }

                RenderSystem.setShaderTexture(0, entry.spell().getIcon());
                int iconX = entry.x() + (ICON_SIZE - 16) / 2;
                int iconY = entry.y() + (ICON_SIZE - 16) / 2;
                guiGraphics.blit(entry.spell().getIcon(), iconX, iconY, 0, 0, 16, 16, 16, 16);

                if (entry.isMouseOver(mouseX, mouseY)) {
                    guiGraphics.renderTooltip(this.font, entry.buildTooltip(), mouseX, mouseY);
                }
            });
            guiGraphics.drawCenteredString(this.font, Component.literal("Clique em uma magia para equipar"), this.width / 2, this.height - 40, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // O jogo não pausa com esta tela aberta
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (SpellEntry entry : spellEntries) {
                if (entry.isMouseOver(mouseX, mouseY)) {
                    selectSpell(entry);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void selectSpell(SpellEntry entry) {
        selectedSpellId = entry.id();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.getCapability(playerspellsprovider.PLAYER_SPELLS)
                    .ifPresent(spells -> spells.setEquippedSpell(entry.spell()));
        }

        modpackets.sendSpellSelection(entry.id());
        onClose();
    }

    private record SpellEntry(spell spell, ResourceLocation id, int x, int y) {
        boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + ICON_SIZE && mouseY >= y && mouseY <= y + ICON_SIZE;
        }

        List<Component> buildTooltip() {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(spell.getDisplayName());
            tooltip.add(Component.literal("Mana: " + spell.getManaCost()));
            tooltip.add(Component.literal("Recarga: " + spell.getCooldownTicks() + " ticks"));
            return tooltip;
        }
    }
}
