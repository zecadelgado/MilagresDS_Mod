package com.stefani.MilagresDSMod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stefani.MilagresDSMod.capability.playerspellsprovider;
import com.stefani.MilagresDSMod.client.ManaAdapter;
import com.stefani.MilagresDSMod.magic.spell;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class SpellMemorizeScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen.milagresdsmod.spell_memorize.title");
    private static final Component EQUIP_LABEL = Component.translatable("screen.milagresdsmod.spell_memorize.equip");
    private static final Component REMOVE_LABEL = Component.translatable("screen.milagresdsmod.spell_memorize.remove");
    private static final Component NO_SPELLS = Component.translatable("screen.milagresdsmod.spell_memorize.none");
    private static final Component SELECT_PROMPT = Component.translatable("screen.milagresdsmod.spell_memorize.select");
    private static final Component MEMORIZED_LABEL = Component.translatable("screen.milagresdsmod.spell_memorize.memorized");
    private static final int GRID_COLUMNS = 4;
    private static final int ENTRY_SIZE = 26;
    private static final int ENTRY_SPACING = 6;
    private static final int ICON_SIZE = 16;
    private static final int DETAILS_WIDTH = 180;
    private static final int DETAILS_HEIGHT = 170;
    private static final int BUTTON_HEIGHT = 20;

    private final ManaAdapter manaAdapter = new ManaAdapter();
    private final List<SpellEntry> spellEntries = new ArrayList<>();

    private ResourceLocation selectedSpellId;
    private ResourceLocation equippedSpellId;

    private Button equipButton;
    private Button removeButton;

    private int listLeft;
    private int listTop;
    private int listColumns;

    public SpellMemorizeScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();
        spellEntries.clear();

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(playerspellsprovider.PLAYER_SPELLS)
                    .ifPresent(spells -> equippedSpellId = spells.getEquippedSpellId());
        } else {
            equippedSpellId = null;
        }

        for (spell spell : spellregistry.REGISTRY.get().getValues()) {
            ResourceLocation id = spellregistry.REGISTRY.get().getKey(spell);
            if (id != null) {
                spellEntries.add(new SpellEntry(spell, id));
            }
        }

        if (selectedSpellId != null && spellEntries.stream().noneMatch(entry -> Objects.equals(entry.id(), selectedSpellId))) {
            selectedSpellId = null;
        }

        if (selectedSpellId == null) {
            selectedSpellId = equippedSpellId != null ? equippedSpellId : (!spellEntries.isEmpty() ? spellEntries.get(0).id() : null);
        }

        calculateGridLayout();

        int panelLeft = this.width / 2 + 20;
        int panelTop = this.height / 2 - DETAILS_HEIGHT / 2;
        int buttonWidth = DETAILS_WIDTH - 20;

        equipButton = addRenderableWidget(Button.builder(EQUIP_LABEL, button -> equipSelectedSpell())
                .pos(panelLeft + 10, panelTop + DETAILS_HEIGHT - (BUTTON_HEIGHT + 4) * 2)
                .size(buttonWidth, BUTTON_HEIGHT)
                .build());

        removeButton = addRenderableWidget(Button.builder(REMOVE_LABEL, button -> removeEquippedSpell())
                .pos(panelLeft + 10, panelTop + DETAILS_HEIGHT - (BUTTON_HEIGHT + 4))
                .size(buttonWidth, BUTTON_HEIGHT)
                .build());

        updateButtonStates();
    }

    private void calculateGridLayout() {
        listColumns = Math.min(GRID_COLUMNS, Math.max(1, spellEntries.size()));
        int rows = listColumns == 0 ? 0 : (int) Math.ceil(spellEntries.size() / (double) listColumns);
        int gridWidth = listColumns * ENTRY_SIZE + Math.max(0, listColumns - 1) * ENTRY_SPACING;
        int gridHeight = rows * ENTRY_SIZE + Math.max(0, rows - 1) * ENTRY_SPACING;

        listLeft = this.width / 2 - DETAILS_WIDTH / 2 - gridWidth - 20;
        listTop = this.height / 2 - gridHeight / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);

        updateButtonStates();

        SpellEntry hoveredEntry = renderSpellGrid(guiGraphics, mouseX, mouseY);
        renderDetailsPanel(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (hoveredEntry != null) {
            guiGraphics.renderTooltip(this.font, hoveredEntry.buildTooltip(), mouseX, mouseY);
        }
    }

    private SpellEntry renderSpellGrid(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (spellEntries.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, NO_SPELLS, this.width / 2 - DETAILS_WIDTH / 2 - 20, this.height / 2, 0xAAAAAA);
            return null;
        }

        SpellEntry hoveredEntry = null;
        for (int index = 0; index < spellEntries.size(); index++) {
            SpellEntry entry = spellEntries.get(index);
            int column = listColumns == 0 ? 0 : index % listColumns;
            int row = listColumns == 0 ? 0 : index / listColumns;
            int x = listLeft + column * (ENTRY_SIZE + ENTRY_SPACING);
            int y = listTop + row * (ENTRY_SIZE + ENTRY_SPACING);

            boolean isHovered = mouseX >= x && mouseX <= x + ENTRY_SIZE && mouseY >= y && mouseY <= y + ENTRY_SIZE;
            boolean isSelected = Objects.equals(entry.id(), selectedSpellId);

            if (isSelected || isHovered) {
                int color = isSelected ? 0x80FFFFFF : 0x80999999;
                guiGraphics.fill(x - 2, y - 2, x + ENTRY_SIZE + 2, y + ENTRY_SIZE + 2, color);
            }

            RenderSystem.setShaderTexture(0, entry.spell().getIcon());
            int iconX = x + (ENTRY_SIZE - ICON_SIZE) / 2;
            int iconY = y + (ENTRY_SIZE - ICON_SIZE) / 2;
            guiGraphics.blit(entry.spell().getIcon(), iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            if (isHovered) {
                hoveredEntry = entry;
            }
        }
        return hoveredEntry;
    }

    private void renderDetailsPanel(GuiGraphics guiGraphics) {
        int panelLeft = this.width / 2 + 20;
        int panelTop = this.height / 2 - DETAILS_HEIGHT / 2;
        guiGraphics.fill(panelLeft - 4, panelTop - 4, panelLeft + DETAILS_WIDTH + 4, panelTop + DETAILS_HEIGHT + 4, 0xA0101010);

        LocalPlayer player = Minecraft.getInstance().player;
        int currentMana = manaAdapter.getCurrent(player);
        int maxMana = manaAdapter.getMax(player);

        int textX = panelLeft + 8;
        int y = panelTop + 8;

        guiGraphics.drawString(this.font, Component.translatable("screen.milagresdsmod.spell_memorize.mana", currentMana, maxMana), textX, y, 0xFFFFFF);
        y += 12;

        SpellEntry selected = getSelectedEntry();
        if (selected == null) {
            guiGraphics.drawString(this.font, SELECT_PROMPT, textX, y + 8, 0xCCCCCC);
            return;
        }

        spell spell = selected.spell();
        guiGraphics.drawString(this.font, spell.getDisplayName(), textX, y, 0xFFFFFF);
        y += 12;

        int manaCost = spell.getManaCost();
        int manaColor = currentMana >= manaCost ? 0x55FF55 : 0xFF5555;
        guiGraphics.drawString(this.font, Component.translatable("screen.milagresdsmod.spell_memorize.mana_cost", manaCost), textX, y, manaColor);
        y += 12;

        guiGraphics.drawString(this.font, Component.translatable("tooltip.milagresdsmod.spell.cooldown", spell.getCooldownTicks()), textX, y, 0xFFFFFF);
        y += 12;

        if (Objects.equals(equippedSpellId, selected.id())) {
            guiGraphics.drawString(this.font, MEMORIZED_LABEL, textX, y, 0x55FF55);
            y += 12;
        }

        Optional<Component> effectSummary = spell.getEffectSummary();
        if (effectSummary.isPresent()) {
            y = drawWrapped(guiGraphics, effectSummary.get(), textX, y + 4, DETAILS_WIDTH - 16, 0xFFFFFF);
        }

        Optional<Component> description = spell.getDescription();
        if (description.isPresent()) {
            y = drawWrapped(guiGraphics, description.get(), textX, y + 2, DETAILS_WIDTH - 16, 0xCCCCCC);
        }

        spell.getBaseDamage().ifPresent(damage -> {
            Component damageText = Component.translatable("tooltip.milagresdsmod.spell.damage", String.format(Locale.ROOT, "%.1f", damage));
            guiGraphics.drawString(this.font, damageText, textX, y + 4, 0xFFAA55);
        });

        spell.getHealingAmount().ifPresent(healing -> {
            Component healingText = Component.translatable("tooltip.milagresdsmod.spell.heal", String.format(Locale.ROOT, "%.1f", healing));
            guiGraphics.drawString(this.font, healingText, textX, y + 16, 0x55FFAA);
        });
    }

    private int drawWrapped(GuiGraphics guiGraphics, Component text, int x, int startY, int maxWidth, int color) {
        List<FormattedCharSequence> lines = this.font.split(text, maxWidth);
        int y = startY;
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(this.font, line, x, y, color, false);
            y += 9;
        }
        return y;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            SpellEntry entry = getEntryAt(mouseX, mouseY);
            if (entry != null) {
                setSelectedSpell(entry.id());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private SpellEntry getEntryAt(double mouseX, double mouseY) {
        for (int index = 0; index < spellEntries.size(); index++) {
            int column = listColumns == 0 ? 0 : index % listColumns;
            int row = listColumns == 0 ? 0 : index / listColumns;
            int x = listLeft + column * (ENTRY_SIZE + ENTRY_SPACING);
            int y = listTop + row * (ENTRY_SIZE + ENTRY_SPACING);
            if (mouseX >= x && mouseX <= x + ENTRY_SIZE && mouseY >= y && mouseY <= y + ENTRY_SIZE) {
                return spellEntries.get(index);
            }
        }
        return null;
    }

    private void setSelectedSpell(ResourceLocation id) {
        this.selectedSpellId = id;
        updateButtonStates();
    }

    private SpellEntry getSelectedEntry() {
        if (selectedSpellId == null) {
            return null;
        }
        for (SpellEntry entry : spellEntries) {
            if (Objects.equals(entry.id(), selectedSpellId)) {
                return entry;
            }
        }
        return null;
    }

    private void equipSelectedSpell() {
        SpellEntry selected = getSelectedEntry();
        LocalPlayer player = Minecraft.getInstance().player;
        if (selected == null || player == null) {
            return;
        }

        int manaCost = selected.spell().getManaCost();
        if (!manaAdapter.has(player, manaCost)) {
            return;
        }

        player.getCapability(playerspellsprovider.PLAYER_SPELLS)
                .ifPresent(spells -> spells.setEquippedSpell(selected.spell()));
        equippedSpellId = selected.id();
        modpackets.sendSpellSelection(selected.id());
        updateButtonStates();
    }

    private void removeEquippedSpell() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || equippedSpellId == null) {
            return;
        }

        player.getCapability(playerspellsprovider.PLAYER_SPELLS)
                .ifPresent(spells -> spells.setEquippedSpell(null));
        equippedSpellId = null;
        modpackets.sendSpellSelection(null);
        updateButtonStates();
    }

    private void updateButtonStates() {
        SpellEntry selected = getSelectedEntry();
        LocalPlayer player = Minecraft.getInstance().player;
        boolean hasSelection = selected != null;
        boolean isEquipped = hasSelection && Objects.equals(selected.id(), equippedSpellId);
        boolean hasMana = hasSelection && player != null && manaAdapter.has(player, selected.spell().getManaCost());

        if (equipButton != null) {
            equipButton.active = hasSelection && !isEquipped && hasMana;
        }
        if (removeButton != null) {
            removeButton.active = equippedSpellId != null;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record SpellEntry(spell spell, ResourceLocation id) {
        private List<Component> buildTooltip() {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(spell.getDisplayName());
            tooltip.add(Component.translatable("tooltip.milagresdsmod.spell.mana", spell.getManaCost()));
            tooltip.add(Component.translatable("tooltip.milagresdsmod.spell.cooldown", spell.getCooldownTicks()));
            spell.getEffectSummary().ifPresent(tooltip::add);
            spell.getDescription().ifPresent(tooltip::add);
            spell.getBaseDamage().ifPresent(damage -> tooltip.add(Component.translatable(
                    "tooltip.milagresdsmod.spell.damage",
                    String.format(Locale.ROOT, "%.1f", damage)
            )));
            spell.getHealingAmount().ifPresent(healing -> tooltip.add(Component.translatable(
                    "tooltip.milagresdsmod.spell.heal",
                    String.format(Locale.ROOT, "%.1f", healing)
            )));
            return tooltip;
        }
    }
}
