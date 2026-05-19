package org.mob.resource_capsules.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.screen.menu.BioResourceGenMenu;

public class BioResourceGenScreen extends AbstractContainerScreen<BioResourceGenMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "textures/gui/bio_resource_gen/bio_resource_gen_gui.png");

    public BioResourceGenScreen(BioResourceGenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
        renderFluidStack(guiGraphics, x+55, y+15);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE,x + 105, y + 33, 176, 0, 8, menu.getScaledProgress());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderFluidTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 3, 4210752, false);

        // --- draw progress text  ---
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();

        String progressText = progress + " / " + maxProgress;
        int textX = 115;
        int textY = 38;

        guiGraphics.drawString(this.font, progressText, textX, textY, 4210752, false);
    }

    private void renderFluidStack(GuiGraphics guiGraphics, int x, int y) {
        FluidStack fluidStack = menu.getFluidStack();
        if (fluidStack.isEmpty()) return;

        // Get Fluid Extensions & Sprite
        var fluid = fluidStack.getFluid();
        var extensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillTexture = extensions.getStillTexture(fluidStack);

        // Fetch the actual sprite from the Atlas
        var sprite = net.minecraft.client.Minecraft.getInstance()
                .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                .apply(stillTexture);

        // Calculate Height (Tank height is 62px, Capacity is 16000)
        int tankHeight = 62;
        int fluidHeight = (int) (tankHeight * ((float) fluidStack.getAmount() / 16000));
        if (fluidHeight < 1 && fluidStack.getAmount() > 0) fluidHeight = 1; // Show at least a sliver

        // Set Fluid Tint
        int color = extensions.getTintColor(fluidStack);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.setShaderTexture(0, net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS);

        // Blit the texture
        // We draw from the bottom of the tank upwards
        int renderY = y + (tankHeight - fluidHeight);
        guiGraphics.blit(x, renderY, 0, 16, fluidHeight, sprite);

        // Reset Shader Color so other GUI elements aren't tinted
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Check if mouse is over the tank area (x+55 to x+70, y+15 to y+75)
        if (mouseX >= x + 55 && mouseX <= x + 70 && mouseY >= y + 15 && mouseY <= y + 75) {
            FluidStack fluid = menu.getFluidStack();
            Component text = fluid.isEmpty() ? Component.literal("Empty") :
                    Component.literal(fluid.getDisplayName().getString() + ": " + fluid.getAmount() + "mB");
            guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }

}
