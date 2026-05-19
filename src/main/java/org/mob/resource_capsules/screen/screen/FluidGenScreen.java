package org.mob.resource_capsules.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.screen.menu.FluidGenMenu;

import java.util.List;

public class FluidGenScreen extends AbstractContainerScreen<FluidGenMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "textures/gui/fluid_gen/fluid_gen_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "textures/gui/arrow_progress_fluid.png");

    public FluidGenScreen(FluidGenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
        renderFluidStack(guiGraphics, x +98, y+15);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            int progress = menu.getScaledProgress();

            int arrowWidth = 25;
            int arrowHeight = 21;

            int screenX = x + 72;
            int screenY = y + 25;

            guiGraphics.blit(ARROW_TEXTURE,
                    screenX,
                    screenY,                  // Pushes the start position down to the bottom
                    0,                        // Texture U (X offset on png)
                    0,                        // Texture V (Y offset on png)
                    arrowWidth,               // Width to draw
                    progress,                 // Height to draw (grows larger over time)
                    arrowWidth, arrowHeight   // Total dimensions of the ARROW_TEXTURE png file
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderGhostUpgradeTooltip(guiGraphics, mouseX, mouseY);
        renderFluidTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderGhostUpgradeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Slot machineSlot = this.menu.slots.get(36);

        // Only show text if slot is empty AND the mouse is currently over it
        if (!machineSlot.hasItem() && this.isHovering(machineSlot.x, machineSlot.y, 16, 16, mouseX, mouseY)) {
            List<Component> tooltipText = List.of(
                    Component.literal("Upgrade Slot").withStyle(ChatFormatting.GOLD),
                    Component.literal("place speed upgrade").withStyle(ChatFormatting.GRAY),
                    Component.literal("here to determine").withStyle(ChatFormatting.GRAY),
                    Component.literal("the processing speed").withStyle(ChatFormatting.GRAY)
            );

            guiGraphics.renderComponentTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
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
        // draw from the bottom of the tank upwards
        int renderY = y + (tankHeight - fluidHeight);
        guiGraphics.blit(x, renderY, 0, 16, fluidHeight, sprite);

        // Reset Shader Color so other GUI elements aren't tinted
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 98 && mouseX <= x + 113 && mouseY >= y + 15 && mouseY <= y + 75) {
            FluidStack fluid = menu.getFluidStack();
            Component text = fluid.isEmpty() ? Component.literal("Empty") :
                    Component.literal(fluid.getDisplayName().getString() + ": " + fluid.getAmount() + "mB");
            guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }

}
