package com.stefani.MilagresDSMod.client.gui;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stefani.MilagresDSMod.registry.spellregistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class spellselection extends Screen {
    public spellselection() {
        super(Component.literal("Spell Selection"));
    }

    @Override
    protected void init() {
        super.init();
        // Adicionar botões ou outros widgets aqui
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Fundo escuro semi-transparente
        this.renderBackground(guiGraphics);

        // Título
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        // Renderizar ícones das magias
        final int[] x = {this.width / 2 - 50};
        int y = this.height / 2 - 20;

        spellregistry.REGISTRY.get().getValues().forEach(spell -> {
            // Desenhar ícone
            RenderSystem.setShaderTexture(0, spell.getIcon());
            guiGraphics.blit(spell.getIcon(), x[0], y, 0, 0, 16, 16, 16, 16);

            // Desenhar tooltip se o mouse estiver sobre o ícone
            if (mouseX >= x[0] && mouseX < x[0] + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(this.font, spell.getDisplayName(), mouseX, mouseY);
            }
            x[0] += 20;
        });

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // O jogo não pausa com esta tela aberta
    }
}
