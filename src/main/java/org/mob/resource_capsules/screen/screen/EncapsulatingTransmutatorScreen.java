package org.mob.resource_capsules.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.screen.menu.EncapsulatingTransmutatorMenu;
import org.mob.resource_capsules.screen.menu.ResourceGenTier1Menu;

public class EncapsulatingTransmutatorScreen extends AbstractContainerScreen<EncapsulatingTransmutatorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ResourceCapsules.MOD_ID, "textures/gui/encapsulating_transmutator/encapsulating_transmutator_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            new ResourceLocation(ResourceCapsules.MOD_ID, "textures/gui/arrow_progress.png");

    public EncapsulatingTransmutatorScreen(EncapsulatingTransmutatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW_TEXTURE,x + 89, y + 34, 0, 0, menu.getScaledProgress(), 16, 24, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);

        // --- draw progress text  ---
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        String progressText = progress + " / " + maxProgress;
        int textX = 93;
        int textY = 62;

        guiGraphics.drawString(this.font, progressText, textX, textY, 4210752, false);
    }

}
