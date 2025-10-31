package com.stefani.MilagresDSMod.client.gui;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.MagicStats;
import com.stefani.MilagresDSMod.client.ManaAdapter;
import com.stefani.MilagresDSMod.client.data.AttributesClientCache;
import com.stefani.MilagresDSMod.client.data.Requirements;
import com.stefani.MilagresDSMod.client.data.Spell;
import com.stefani.MilagresDSMod.client.data.SpellRegistryClient;
import com.stefani.MilagresDSMod.client.gui.AttributesScreen;
import com.stefani.MilagresDSMod.network.modpackets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Locale;

/**
 * Spell memorisation screen that allows players to assign spells to local slots without spending mana.
 */
public class SpellMemorizeScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            MilagresDSMod.MODID, "textures/gui/spell_menu_bg.png");
    private static final ResourceLocation FRAME = ResourceLocation.fromNamespaceAndPath(
            MilagresDSMod.MODID, "textures/gui/icon_frame.png");
    private static final ResourceLocation FRAME_SELECTED = ResourceLocation.fromNamespaceAndPath(
            MilagresDSMod.MODID, "textures/gui/icon_frame_selected.png");

    private static final int BACKGROUND_WIDTH = 520;
    private static final int BACKGROUND_HEIGHT = 360;
    private static final int SLOT_SIZE = 52;

    private SpellGridWidget gridWidget;
    private final int horizontalSlotSpacing = SpellGridWidget.DEFAULT_HORIZONTAL_SPACING;
    private Button equipButton;
    private Button removeButton;
    private Button backButton;
    private Button attributesButton;

    private final MagicStats magicStats = MagicStats.get();
    private Spell selectedSpell;
    private int selectedSlotIndex;
    private List<Spell> availableSpells = List.of();
    private Map<ResourceLocation, Spell> spellsById = Map.of();

    private int leftPos;
    private int topPos;

    public SpellMemorizeScreen() {
        super(Component.translatable("ui.memorize.title"));
        this.selectedSlotIndex = 0;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - BACKGROUND_WIDTH) / 2;
        this.topPos = (this.height - BACKGROUND_HEIGHT) / 2;

        reloadSpells();

        int gridLeft = leftPos + 24;
        int slotsTop = topPos + 32;
        int gridTop = slotsTop + SLOT_SIZE + 16;
        int gridHeight = SLOT_SIZE * 4 + 8;

        this.gridWidget = new SpellGridWidget(gridLeft, gridTop, gridHeight,
                availableSpells, FRAME, FRAME_SELECTED, horizontalSlotSpacing);
        this.gridWidget.setSelectionListener(this::onSpellSelected);
        addRenderableWidget(gridWidget);
        setInitialSelection();

        int gridWidth = this.gridWidget.getWidth();
        int detailLeft = gridLeft + gridWidth + 24;
        int detailWidth = BACKGROUND_WIDTH - (detailLeft - leftPos) - 32;
        int buttonsTop = topPos + BACKGROUND_HEIGHT - 60;

        this.equipButton = addRenderableWidget(Button.builder(Component.translatable("ui.memorize.button.equip"), button -> {
            equipSelectedSpell();
        }).bounds(detailLeft, buttonsTop, detailWidth, 20).build());

        this.removeButton = addRenderableWidget(Button.builder(Component.translatable("ui.memorize.button.remove"), button -> {
            removeSelectedSpell();
        }).bounds(detailLeft, buttonsTop + 24, detailWidth, 20).build());

        int halfWidth = Math.max(60, (detailWidth - 4) / 2);
        this.backButton = addRenderableWidget(Button.builder(Component.translatable("ui.memorize.button.back"), button -> onClose())
                .bounds(detailLeft, buttonsTop + 48, halfWidth, 20).build());

        this.attributesButton = addRenderableWidget(Button.builder(Component.translatable("ui.memorize.button.attributes"),
                button -> openAttributesScreen()).bounds(detailLeft + halfWidth + 4, buttonsTop + 48,
                detailWidth - halfWidth - 4, 20).build());

        updateButtonState();
        setInitialFocus(gridWidget);
    }

    private void setInitialSelection() {
        for (int slot = 0; slot < magicStats.getSlotsMax(); slot++) {
            ResourceLocation spellId = magicStats.getSpellInSlot(slot);
            if (spellId != null) {
                this.gridWidget.setSelectedSpell(spellId);
                return;
            }
        }
    }

    private void onSpellSelected(@Nullable Spell spell) {
        this.selectedSpell = spell;
        updateButtonState();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        guiGraphics.drawString(this.font, this.title, leftPos + 24, topPos + 16, 0xF3E5AB, false);

        renderEquippedSlots(guiGraphics, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int detailLeft = gridWidget.getX() + gridWidget.getWidth() + 24;
        int detailTop = gridWidget.getY();
        int detailWidth = BACKGROUND_WIDTH - (detailLeft - leftPos) - 32;
        renderSpellDetails(guiGraphics, detailLeft, detailTop, detailWidth);

        renderSpellTooltip(guiGraphics, mouseX, mouseY);

        renderHints(guiGraphics);
    }

    private void renderEquippedSlots(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int slotsLeft = leftPos + 24;
        int slotsTop = topPos + 32;

        // Renderizar título dos slots
        Component slotsTitle = Component.translatable("ui.memorize.slots",
            magicStats.getEquippedSpells().stream().filter(s -> s != null).count(),
            magicStats.getSlotsMax());
        guiGraphics.drawString(this.font, slotsTitle, slotsLeft, slotsTop - 12, 0xF7E7CE, false);

        int spacing = this.gridWidget != null ? this.gridWidget.getHorizontalSpacing() : horizontalSlotSpacing;
        int slotStep = SLOT_SIZE + spacing;
        for (int i = 0; i < magicStats.getSlotsMax(); i++) {
            int slotX = slotsLeft + i * slotStep;
            ResourceLocation frame = i == selectedSlotIndex ? FRAME_SELECTED : FRAME;
            guiGraphics.blit(frame, slotX, slotsTop, 0, 0, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);

            ResourceLocation spellId = magicStats.getSpellInSlot(i);
            Spell spell = spellId != null ? spellsById.get(spellId) : null;
            if (spell == null && spellId != null) {
                spell = SpellRegistryClient.get(spellId).orElse(null);
            }
            ResourceLocation icon = resolveIcon(spell != null ? spell.icon() : null);
            guiGraphics.blit(icon, slotX + 2, slotsTop + 2, 0, 0, 48, 48, 48, 48);

            int labelColor = i == selectedSlotIndex ? 0xF5D76E : 0xC6B46A;
            guiGraphics.drawString(this.font, String.valueOf(i + 1), slotX + 4, slotsTop + SLOT_SIZE - 10, labelColor, false);
        }
    }

    private void renderSpellTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.gridWidget == null) {
            return;
        }
        Spell hoveredSpell = this.gridWidget.getSpellAt(mouseX, mouseY);
        if (hoveredSpell != null) {
            guiGraphics.renderTooltip(this.font, hoveredSpell.name(), mouseX, mouseY);
        }
    }

    private void renderSpellDetails(GuiGraphics guiGraphics, int left, int top, int width) {
        int y = top;
        if (selectedSpell == null) {
            guiGraphics.drawString(this.font, Component.translatable("ui.memorize.empty_selection"), left, y, 0xAAAAAA, false);
            return;
        }
        guiGraphics.drawString(this.font, selectedSpell.name(), left, y, 0xF7E7CE, false);
        y += 12;
        guiGraphics.drawString(this.font, selectedSpell.category().getDisplayName(), left, y, 0xFFD700, false);
        y += 12;

        for (FormattedCharSequence line : this.font.split(selectedSpell.description(), width)) {
            guiGraphics.drawString(this.font, line, left, y, 0xDDDDDD, false);
            y += 10;
        }
        y += 4;

        int playerMana = getPlayerMana();
        int playerMaxMana = getPlayerMaxMana();
        int manaCost = selectedSpell.manaCost();
        int manaColor = playerMana >= manaCost ? 0x55FF55 : 0xFF5555;
        Component manaLine = Component.translatable("ui.memorize.cost", manaCost);
        guiGraphics.drawString(this.font, Component.translatable("ui.memorize.mana_label"), left, y, 0xF7E7CE, false);
        y += 10;
        guiGraphics.drawString(this.font, manaLine, left + 8, y, manaColor, false);
        Component currentMana = Component.translatable("ui.memorize.current_mana", playerMana, playerMaxMana);
        guiGraphics.drawString(this.font, currentMana, left + 8, y + 10, 0xDDDDDD, false);
        y += 22;

        int bottom = renderRequirementBlock(guiGraphics, left, y, width, selectedSpell.requirements());
        renderAttributeSummary(guiGraphics, left, bottom + 8);
    }

    private int renderRequirementBlock(GuiGraphics guiGraphics, int left, int y, int width, Requirements requirements) {
        guiGraphics.drawString(this.font, Component.translatable("ui.memorize.requirements.title"), left, y, 0xF7E7CE, false);
        y += 10;

        Player player = Minecraft.getInstance().player;
        int level = AttributesClientCache.level();
        OptionalInt intelligence = getPlaceholderAttribute(player, "intelligence");
        OptionalInt faith = getPlaceholderAttribute(player, "faith");
        OptionalInt arcane = getPlaceholderAttribute(player, "arcane");

        List<Component> lines = new ArrayList<>();
        lines.add(formatRequirement("ui.memorize.requirement.level", requirements.requiredLevel(), OptionalInt.of(level)));
        lines.add(formatRequirement("ui.memorize.requirement.intelligence", requirements.intelligence(), intelligence));
        lines.add(formatRequirement("ui.memorize.requirement.faith", requirements.faith(), faith));
        lines.add(formatRequirement("ui.memorize.requirement.arcane", requirements.arcane(), arcane));
        for (Component note : requirements.additionalNotes()) {
            lines.add(note.copy().withStyle(ChatFormatting.GRAY));
        }

        for (Component line : lines) {
            for (FormattedCharSequence split : this.font.split(line, width)) {
                guiGraphics.drawString(this.font, split, left, y, 0xFFFFFF, false);
                y += 10;
            }
        }
        return y;
    }

    private void renderAttributeSummary(GuiGraphics guiGraphics, int left, int y) {
        guiGraphics.drawString(this.font, Component.translatable("ui.attributes.hint"), left, y, 0xBBAA88, false);
        y += 12;
        Component summary = Component.literal(String.format(Locale.ROOT,
                "STR %d | DEX %d | CON %d | INT %d | FAI %d | ARC %d",
                AttributesClientCache.strength(),
                AttributesClientCache.dexterity(),
                AttributesClientCache.constitution(),
                AttributesClientCache.intelligence(),
                AttributesClientCache.faith(),
                AttributesClientCache.arcane()));
        guiGraphics.drawString(this.font, summary, left, y, 0xFFFFFF, false);
    }

    private Component formatRequirement(String translationKey, int required, OptionalInt current) {
        if (current.isPresent()) {
            int value = current.getAsInt();
            ChatFormatting color = value >= required ? ChatFormatting.GREEN : ChatFormatting.RED;
            return Component.translatable(translationKey, required, value).withStyle(color);
        }
        return Component.translatable(translationKey, required, Component.literal("?"))
                .withStyle(ChatFormatting.GRAY);
    }

    private OptionalInt getPlaceholderAttribute(@Nullable Player player, String key) {
        if (key == null) {
            return OptionalInt.empty();
        }
        switch (key.toLowerCase(Locale.ROOT)) {
            case "intelligence":
                return OptionalInt.of(AttributesClientCache.intelligence());
            case "faith":
                return OptionalInt.of(AttributesClientCache.faith());
            case "arcane":
                return OptionalInt.of(AttributesClientCache.arcane());
            default:
                return OptionalInt.empty();
        }
    }

    private int getPlayerMana() {
        Player player = Minecraft.getInstance().player;
        return ManaAdapter.getCurrent(player);
    }

    private int getPlayerMaxMana() {
        Player player = Minecraft.getInstance().player;
        return ManaAdapter.getMax(player);
    }

    private void renderHints(GuiGraphics guiGraphics) {
        int hintY = topPos + BACKGROUND_HEIGHT - 16;
        Component scrollHint = Component.translatable("ui.memorize.hint.scroll");
        Component navigationHint = Component.translatable("ui.memorize.hint.navigate");
        Component confirmHint = Component.translatable("ui.memorize.hint.confirm", Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage());

        int center = leftPos + BACKGROUND_WIDTH / 2;
        guiGraphics.drawCenteredString(this.font, scrollHint, center, hintY - 20, 0xCCCCCC);
        guiGraphics.drawCenteredString(this.font, navigationHint, center, hintY - 10, 0xCCCCCC);
        guiGraphics.drawCenteredString(this.font, confirmHint, center, hintY, 0xCCCCCC);
    }

    private void equipSelectedSpell() {
        if (selectedSpell == null) {
            return;
        }
        // Atualiza primeiro o snapshot local para que a grade responda imediatamente ao jogador.
        magicStats.equipSpell(selectedSlotIndex, selectedSpell.id());
        sendPrimarySlotToServer();
        updateButtonState();
    }

    private void removeSelectedSpell() {
        // Ao remover usamos o mesmo fluxo do equipar: estado local primeiro, sincronização depois.
        magicStats.clearSlot(selectedSlotIndex);
        sendPrimarySlotToServer();
        updateButtonState();
    }

    private void sendPrimarySlotToServer() {
        ResourceLocation primary = magicStats.getSpellInSlot(0);
        if (primary != null && SpellRegistryClient.get(primary).isEmpty()) {
            primary = null;
        }
        // Only synchronise the primary slot for now so the server keeps the active spell used during casting.
        modpackets.sendSpellSelection(primary);
    }

    private void updateButtonState() {
        boolean hasSelection = selectedSpell != null;
        boolean isEquipped = hasSelection && magicStats.isEquipped(selectedSpell.id());
        boolean meetsRequirements = hasSelection && checkRequirements(selectedSpell);
        this.equipButton.active = hasSelection && !isEquipped && meetsRequirements;
        ResourceLocation slotSpell = magicStats.getSpellInSlot(selectedSlotIndex);
        this.removeButton.active = slotSpell != null;
    }

    private void reloadSpells() {
        this.availableSpells = SpellRegistryClient.getAll();
        Map<ResourceLocation, Spell> map = new LinkedHashMap<>();
        for (Spell spell : availableSpells) {
            map.put(spell.id(), spell);
        }
        this.spellsById = Map.copyOf(map);
    }

    private boolean checkRequirements(Spell spell) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        Requirements req = spell.requirements();
        int level = AttributesClientCache.level();
        if (level < req.requiredLevel()) {
            return false;
        }

        OptionalInt intelligence = getPlaceholderAttribute(player, "intelligence");
        if (intelligence.isPresent() && intelligence.getAsInt() < req.intelligence()) {
            return false;
        }

        OptionalInt faith = getPlaceholderAttribute(player, "faith");
        if (faith.isPresent() && faith.getAsInt() < req.faith()) {
            return false;
        }

        OptionalInt arcane = getPlaceholderAttribute(player, "arcane");
        if (arcane.isPresent() && arcane.getAsInt() < req.arcane()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (handleSlotClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleSlotClick(double mouseX, double mouseY) {
        int slotsLeft = leftPos + 24;
        int slotsTop = topPos + 32;
        int spacing = gridWidget != null ? gridWidget.getHorizontalSpacing() : horizontalSlotSpacing;
        for (int i = 0; i < magicStats.getSlotsMax(); i++) {
            int slotX = slotsLeft + i * (SLOT_SIZE + spacing);
            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE && mouseY >= slotsTop && mouseY < slotsTop + SLOT_SIZE) {
                selectedSlotIndex = i;
                gridWidget.setSelectedSpell(magicStats.getSpellInSlot(i));
                updateButtonState();
                return true;
            }
        }
        return false;
    }

    private void openAttributesScreen() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(new AttributesScreen());
        }
    }

    private ResourceLocation resolveIcon(@Nullable ResourceLocation icon) {
        Minecraft minecraft = Minecraft.getInstance();
        if (icon == null || minecraft == null) {
            return SpellRegistryClient.fallbackIcon();
        }
        return minecraft.getResourceManager().getResource(icon).isPresent() ? icon : SpellRegistryClient.fallbackIcon();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.gridWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (gridWidget == null) {
            return;
        }
        int slots = Math.max(1, magicStats.getSlotsMax());
        if (selectedSlotIndex >= slots) {
            selectedSlotIndex = slots - 1;
            gridWidget.setSelectedSpell(magicStats.getSpellInSlot(selectedSlotIndex));
            updateButtonState();
        }
    }
}
