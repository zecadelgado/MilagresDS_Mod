package com.stefani.MilagresDSMod.client.gui;

import com.stefani.MilagresDSMod.MilagresDSMod;
import com.stefani.MilagresDSMod.client.data.AttributesClientCache;
import com.stefani.MilagresDSMod.magic.SpellScalingGrade;
import com.stefani.MilagresDSMod.network.modpackets;
import com.stefani.MilagresDSMod.util.AttributeScaling;
import com.stefani.MilagresDSMod.util.WeaponScaling;
import com.stefani.MilagresDSMod.util.WeaponScaling.WeaponScalingProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttributesScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            MilagresDSMod.MODID, "textures/gui/spell_menu_bg.png");

    private static final int BACKGROUND_WIDTH = 520;
    private static final int BACKGROUND_HEIGHT = 360;

    private static final Component DEFAULT_TITLE = Component.translatable("ui.attributes.title");

    private final List<AttributeRow> rows = new ArrayList<>();
    private final Map<AttributeRow, Button> allocationButtons = new HashMap<>();

    private Button resetButton;
    private Button backButton;

    private int leftPos;
    private int topPos;
    private int valueColumn;
    private int bonusColumn;
    private int buttonColumn;

    public AttributesScreen() {
        this(DEFAULT_TITLE);
    }

    protected AttributesScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - BACKGROUND_WIDTH) / 2;
        this.topPos = (this.height - BACKGROUND_HEIGHT) / 2;
        this.valueColumn = leftPos + BACKGROUND_WIDTH - 260;
        this.bonusColumn = leftPos + BACKGROUND_WIDTH - 150;
        this.buttonColumn = leftPos + BACKGROUND_WIDTH - 72;

        setupRows();

        int rowY = topPos + 64;
        allocationButtons.clear();
        for (int i = 0; i < rows.size(); i++) {
            AttributeRow row = rows.get(i);
            row.setY(rowY);
            if (row.allocatable()) {
                Button button = Button.builder(Component.translatable("ui.attributes.plus"),
                        btn -> allocateAttribute(row.key()))
                        .bounds(buttonColumn, rowY - 6, 32, 20)
                        .build();
                addRenderableWidget(button);
                allocationButtons.put(row, button);
            }
            rowY += 26;
        }

        int buttonsY = topPos + BACKGROUND_HEIGHT - 50;
        this.resetButton = addRenderableWidget(Button.builder(Component.translatable("ui.attributes.reset"),
                button -> resetAttributes()).bounds(leftPos + 32, buttonsY, 120, 20).build());
        this.backButton = addRenderableWidget(Button.builder(Component.translatable("ui.attributes.back"),
                button -> onClose()).bounds(leftPos + BACKGROUND_WIDTH - 32 - 80, buttonsY, 80, 20).build());

        updateAllocationButtons();
        afterInit();
    }

    private void setupRows() {
        this.rows.clear();
        rows.add(new AttributeRow("strength", Component.translatable("ui.attributes.strength"),
                Component.translatable("ui.attributes.strength.tooltip"), true));
        rows.add(new AttributeRow("dexterity", Component.translatable("ui.attributes.dexterity"),
                Component.translatable("ui.attributes.dexterity.tooltip"), true));
        rows.add(new AttributeRow("constitution", Component.translatable("ui.attributes.constitution"),
                Component.translatable("ui.attributes.constitution.tooltip"), true));
        rows.add(new AttributeRow("intelligence", Component.translatable("ui.attributes.intelligence"),
                Component.translatable("ui.attributes.intelligence.tooltip"), true));
        rows.add(new AttributeRow("faith", Component.translatable("ui.attributes.faith"),
                Component.translatable("ui.attributes.faith.tooltip"), true));
        rows.add(new AttributeRow("arcane", Component.translatable("ui.attributes.arcane"),
                Component.translatable("ui.attributes.arcane.tooltip"), true));
        rows.add(new AttributeRow("level", Component.translatable("ui.attributes.level"), Component.empty(), false));
    }

    @Override
    public void tick() {
        super.tick();
        updateAllocationButtons();
    }

    private void updateAllocationButtons() {
        int availablePoints = AttributesClientCache.points();
        boolean hasPoints = availablePoints > 0;
        int allocated = getTotalAllocated();
        for (Map.Entry<AttributeRow, Button> entry : allocationButtons.entrySet()) {
            Button button = entry.getValue();
            button.active = hasPoints;
        }
        if (resetButton != null) {
            resetButton.active = allocated > 0;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        guiGraphics.drawString(this.font, this.title, leftPos + 32, topPos + 32, 0xF3E5AB, false);

        Component points = Component.translatable("ui.attributes.points", AttributesClientCache.points());
        int pointsX = leftPos + BACKGROUND_WIDTH - 32 - this.font.width(points);
        guiGraphics.drawString(this.font, points, pointsX, topPos + 36, 0xF7E7CE, false);

        Component runes = Component.translatable("ui.attributes.runes", AttributesClientCache.storedRunes());
        guiGraphics.drawString(this.font, runes, leftPos + 32, topPos + 56, 0xF7E7CE, false);

        for (AttributeRow row : rows) {
            guiGraphics.drawString(this.font, row.name(), leftPos + 40, row.y(), 0xFFFFFF, false);
            int baseValue = getValueFor(row.key());
            double effective = AttributeScaling.applySoftcaps(baseValue);
            Component valueComponent = Component.translatable("ui.attributes.value_format",
                    baseValue,
                    formatNumber(effective),
                    formatNumber(effective - baseValue));
            String valueText = valueComponent.getString();
            guiGraphics.drawString(this.font, valueText, valueColumn - this.font.width(valueText), row.y(), 0xF7E7CE, false);

            Component bonusComponent = getBonusComponent(row.key(), baseValue, effective);
            if (!bonusComponent.getString().isBlank()) {
                guiGraphics.drawString(this.font, bonusComponent, bonusColumn, row.y(), 0xBBAA88, false);
            }
        }

        Component hint = hintMessage();
        if (hint != null) {
            guiGraphics.drawString(this.font, hint, leftPos + 32, topPos + BACKGROUND_HEIGHT - 80, 0xBBAA88, false);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (AttributeRow row : rows) {
            Button button = allocationButtons.get(row);
            if (button != null && button.isHoveredOrFocused()) {
                guiGraphics.renderTooltip(this.font, row.tooltip(), mouseX, mouseY);
            }
        }

        renderAdditional(guiGraphics, mouseX, mouseY, partialTick);
    }

    private int getValueFor(String key) {
        if (key == null) {
            return AttributesClientCache.level();
        }
        return switch (key) {
            case "strength" -> AttributesClientCache.strength();
            case "dexterity" -> AttributesClientCache.dexterity();
            case "constitution" -> AttributesClientCache.constitution();
            case "intelligence" -> AttributesClientCache.intelligence();
            case "faith" -> AttributesClientCache.faith();
            case "arcane" -> AttributesClientCache.arcane();
            case "level" -> AttributesClientCache.level();
            default -> 0;
        };
    }

    private int getTotalAllocated() {
        return AttributesClientCache.strength()
                + AttributesClientCache.dexterity()
                + AttributesClientCache.constitution()
                + AttributesClientCache.intelligence()
                + AttributesClientCache.faith()
                + AttributesClientCache.arcane();
    }

    private void allocateAttribute(String key) {
        if (AttributesClientCache.points() <= 0) {
            displayNoPointsMessage();
            return;
        }
        modpackets.sendAllocateAttribute(key, 1);
    }

    private void resetAttributes() {
        if (getTotalAllocated() <= 0) {
            return;
        }
        modpackets.sendResetAttributes();
    }

    private void displayNoPointsMessage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.displayClientMessage(Component.translatable("msg.milagresdsmod.no_points"), true);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    private Component getBonusComponent(String key, int baseValue, double effective) {
        return switch (key) {
            case "strength" -> {
                WeaponScalingProfile melee = WeaponScaling.defaultMelee();
                double percent = melee.computeStrengthBonus(baseValue) * 100.0D;
                yield Component.translatable("ui.attributes.bonus.melee", formatPercent(percent));
            }
            case "dexterity" -> {
                WeaponScalingProfile ranged = WeaponScaling.defaultRanged();
                double percent = ranged.computeDexterityBonus(baseValue) * 100.0D;
                yield Component.translatable("ui.attributes.bonus.ranged", formatPercent(percent));
            }
            case "constitution" -> {
                double buffer = Math.max(0.0D, effective - baseValue);
                yield Component.translatable("ui.attributes.bonus.constitution", formatNumber(buffer));
            }
            case "intelligence" -> {
                double bonus = AttributeScaling.computeSpellBonus(baseValue, SpellScalingGrade.A);
                yield Component.translatable("ui.attributes.bonus.sorcery", formatNumber(bonus));
            }
            case "faith" -> {
                double bonus = AttributeScaling.computeSpellBonus(baseValue, SpellScalingGrade.S);
                yield Component.translatable("ui.attributes.bonus.miracle", formatNumber(bonus));
            }
            case "arcane" -> {
                double bonus = AttributeScaling.computeSpellBonus(baseValue, SpellScalingGrade.B);
                yield Component.translatable("ui.attributes.bonus.occult", formatNumber(bonus));
            }
            default -> Component.empty();
        };
    }

    private static String formatNumber(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.0f", value);
    }

    private static final class AttributeRow {
        private final String key;
        private final Component name;
        private final Component tooltip;
        private final boolean allocatable;
        private int y;

        private AttributeRow(String key, Component name, Component tooltip, boolean allocatable) {
            this.key = key;
            this.name = name;
            this.tooltip = tooltip;
            this.allocatable = allocatable;
            this.y = 0;
        }

        private void setY(int y) {
            this.y = y;
        }

        private String key() {
            return key;
        }

        private Component name() {
            return name;
        }

        private Component tooltip() {
            return tooltip;
        }

        private boolean allocatable() {
            return allocatable;
        }

        private int y() {
            return y;
        }
    }
}
