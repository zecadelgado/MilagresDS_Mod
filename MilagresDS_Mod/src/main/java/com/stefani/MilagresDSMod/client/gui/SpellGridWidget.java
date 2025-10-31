package com.stefani.MilagresDSMod.client.gui;

import com.stefani.MilagresDSMod.client.data.Spell;
import com.stefani.MilagresDSMod.client.data.SpellRegistryClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SpellGridWidget extends AbstractWidget {
    private static final int COLUMNS = 6;
    private static final int ICON_SIZE = 48;
    private static final int CELL_PADDING = 2;
    private static final int CELL_SIZE = ICON_SIZE + CELL_PADDING * 2;
    public static final int DEFAULT_HORIZONTAL_SPACING = 8;

    private final List<Spell> spells;
    private final ResourceLocation frameTexture;
    private final ResourceLocation selectedFrameTexture;
    private final int horizontalSpacing;
    private Consumer<Spell> selectionListener = spell -> {};

    private int selectedIndex = -1;
    private int scrollRow = 0;

    public SpellGridWidget(int x, int y, int height, List<Spell> spells,
                           ResourceLocation frameTexture, ResourceLocation selectedFrameTexture) {
        this(x, y, height, spells, frameTexture, selectedFrameTexture, DEFAULT_HORIZONTAL_SPACING);
    }

    public SpellGridWidget(int x, int y, int height, List<Spell> spells,
                           ResourceLocation frameTexture, ResourceLocation selectedFrameTexture,
                           int horizontalSpacing) {
        super(x, y, calculateWidth(horizontalSpacing), height, Component.empty());
        this.spells = List.copyOf(spells);
        this.frameTexture = frameTexture;
        this.selectedFrameTexture = selectedFrameTexture;
        this.horizontalSpacing = horizontalSpacing;
    }

    public static int calculateWidth(int horizontalSpacing) {
        return COLUMNS * CELL_SIZE + Math.max(0, COLUMNS - 1) * horizontalSpacing;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setSelectionListener(Consumer<Spell> listener) {
        this.selectionListener = Objects.requireNonNullElse(listener, spell -> {});
    }

    public void setSelectedSpell(ResourceLocation spellId) {
        if (spellId == null) {
            setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < spells.size(); i++) {
            if (spellId.equals(spells.get(i).id())) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    @Nullable
    public Spell getSelectedSpell() {
        if (selectedIndex < 0 || selectedIndex >= spells.size()) {
            return null;
        }
        return spells.get(selectedIndex);
    }

    @Nullable
    public Spell getSpellAt(double mouseX, double mouseY) {
        int index = indexAt(mouseX, mouseY);
        if (index < 0) {
            return null;
        }
        return spells.get(index);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int visibleRows = Math.max(1, this.height / CELL_SIZE);
        int maxScroll = Math.max(0, getRowCount() - visibleRows);
        scrollRow = Mth.clamp(scrollRow, 0, maxScroll);

        guiGraphics.enableScissor(getX(), getY(), getX() + this.width, getY() + this.height);
        int stepX = CELL_SIZE + horizontalSpacing;
        for (int row = 0; row < visibleRows; row++) {
            int gridRow = scrollRow + row;
            for (int column = 0; column < COLUMNS; column++) {
                int index = gridRow * COLUMNS + column;
                if (index >= spells.size()) {
                    continue;
                }
                int cellX = getX() + column * stepX;
                int cellY = getY() + row * CELL_SIZE;
                ResourceLocation frame = index == selectedIndex ? selectedFrameTexture : frameTexture;
                guiGraphics.blit(frame, cellX, cellY, 0, 0, CELL_SIZE, CELL_SIZE, CELL_SIZE, CELL_SIZE);

                Spell spell = spells.get(index);
                ResourceLocation icon = resolveIcon(spell.icon());
                int iconX = cellX + CELL_PADDING;
                int iconY = cellY + CELL_PADDING;
                guiGraphics.blit(icon, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            }
        }
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int index = indexAt(mouseX, mouseY);
        if (index < 0) {
            return false;
        }
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        setSelectedIndex(index);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        int visibleRows = Math.max(1, this.height / CELL_SIZE);
        int maxScroll = Math.max(0, getRowCount() - visibleRows);
        scrollRow = Mth.clamp(scrollRow - (int) Math.signum(delta), 0, maxScroll);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }
        int rowCount = getRowCount();
        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> moveSelection(-1);
            case GLFW.GLFW_KEY_RIGHT -> moveSelection(1);
            case GLFW.GLFW_KEY_UP -> moveSelection(-COLUMNS);
            case GLFW.GLFW_KEY_DOWN -> moveSelection(COLUMNS);
            default -> {
                return false;
            }
        }
        int visibleRows = Math.max(1, this.height / CELL_SIZE);
        int currentRow = selectedIndex >= 0 ? selectedIndex / COLUMNS : 0;
        if (currentRow < scrollRow) {
            scrollRow = currentRow;
        } else if (currentRow >= scrollRow + visibleRows) {
            scrollRow = currentRow - visibleRows + 1;
        }
        scrollRow = Mth.clamp(scrollRow, 0, Math.max(0, rowCount - visibleRows));
        return true;
    }

    private void moveSelection(int delta) {
        if (spells.isEmpty()) {
            return;
        }
        int targetIndex = selectedIndex < 0 ? 0 : selectedIndex + delta;
        targetIndex = Mth.clamp(targetIndex, 0, spells.size() - 1);
        setSelectedIndex(targetIndex);
    }

    private int getRowCount() {
        return Mth.ceil((double) spells.size() / COLUMNS);
    }

    private int indexAt(double mouseX, double mouseY) {
        if (!isMouseOver(mouseX, mouseY)) {
            return -1;
        }
        double relativeX = mouseX - getX();
        double relativeY = mouseY - getY();
        if (relativeX < 0 || relativeY < 0) {
            return -1;
        }
        int stepX = CELL_SIZE + horizontalSpacing;
        int column = (int) (relativeX / stepX);
        int rowInView = (int) (relativeY / CELL_SIZE);
        int offsetInColumn = (int) (relativeX % stepX);
        if (column < 0 || column >= COLUMNS || rowInView < 0) {
            return -1;
        }
        if (offsetInColumn >= CELL_SIZE) {
            return -1;
        }
        int index = (scrollRow + rowInView) * COLUMNS + column;
        if (index < 0 || index >= spells.size()) {
            return -1;
        }
        return index;
    }

    private void setSelectedIndex(int index) {
        if (index < -1 || index >= spells.size()) {
            return;
        }
        this.selectedIndex = index;
        Spell selected = getSelectedSpell();
        selectionListener.accept(selected);
    }

    private ResourceLocation resolveIcon(@Nullable ResourceLocation icon) {
        if (icon == null) {
            return SpellRegistryClient.fallbackIcon();
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return SpellRegistryClient.fallbackIcon();
        }
        return minecraft.getResourceManager().getResource(icon).isPresent() ? icon : SpellRegistryClient.fallbackIcon();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        Spell selected = getSelectedSpell();
        if (selected != null) {
            narrationElementOutput.add(NarratedElementType.TITLE, selected.name());
        }
    }
}
