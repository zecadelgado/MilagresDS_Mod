package com.stefani.MilagresDSMod.client.gui;

import com.stefani.MilagresDSMod.client.data.AttributesClientCache;
import com.stefani.MilagresDSMod.network.modpackets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class GraceAttributesScreen extends AttributesScreen {
    private final BlockPos gracePos;
    private Button levelUpButton;

    public GraceAttributesScreen(BlockPos gracePos) {
        super(Component.translatable("ui.grace.title"));
        this.gracePos = gracePos;
    }

    @Override
    protected void afterInit() {
        int buttonWidth = 140;
        int buttonHeight = 20;
        int x = leftPos() + (backgroundWidth() / 2) - (buttonWidth / 2);
        int y = topPos() + backgroundHeight() - 90;
        this.levelUpButton = addRenderableWidget(Button.builder(Component.translatable("ui.grace.confirm"),
                button -> attemptLevelUp()).bounds(x, y, buttonWidth, buttonHeight).build());
        updateButtonState();
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonState();
    }

    @Override
    protected Component hintMessage() {
        return Component.translatable("ui.grace.hint");
    }

    @Override
    protected void renderAdditional(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        long cost = AttributesClientCache.nextLevelCost();
        long runes = AttributesClientCache.storedRunes();
        int textX = leftPos() + 32;
        int costY = topPos() + backgroundHeight() - 140;
        int balanceY = costY + 16;
        int costColor = runes >= cost ? 0x9FE070 : 0xFF6B6B;
        guiGraphics.drawString(this.font, Component.translatable("ui.grace.cost", cost), textX, costY, costColor, false);
        guiGraphics.drawString(this.font, Component.translatable("ui.grace.balance", runes), textX, balanceY, 0xF7E7CE, false);
    }

    private void updateButtonState() {
        if (levelUpButton != null) {
            long cost = AttributesClientCache.nextLevelCost();
            long runes = AttributesClientCache.storedRunes();
            levelUpButton.active = cost > 0 && runes >= cost;
        }
    }

    private void attemptLevelUp() {
        modpackets.sendGraceLevelUp(gracePos);
    }
}
