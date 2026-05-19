package org.mob.resource_capsules.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.screen.menu.FluidGenMultiblockMenu;

import java.util.List;

public class FluidGenMultiblockScreen extends AbstractContainerScreen<FluidGenMultiblockMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "textures/gui/fluid_gen/fluid_gen_multiblock_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "textures/gui/arrow_progress_multiblock.png");

    private static final int TANK_HEIGHT = 29;
    private static final int TANK_WIDTH = 16;
    private static final int TANK_CAPACITY = 64000;

    public FluidGenMultiblockScreen(FluidGenMultiblockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrows(guiGraphics, x, y);
        renderFluidTanks(guiGraphics, x, y);
    }

    private void renderProgressArrows(GuiGraphics guiGraphics, int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (menu.isCrafting(i)) {
                int progress = menu.getScaledProgress(i);

                int arrowWidth = 16;
                int arrowHeight = 24;

                int screenX = x + 56 + (i * 24);
                int screenY = y + 28;

                guiGraphics.blit(ARROW_TEXTURE,
                        screenX,
                        screenY,
                        0,
                        0,
                        arrowWidth,
                        progress,
                        arrowWidth, arrowHeight
                );
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        renderGhostUpgradeTooltip(guiGraphics, mouseX, mouseY);
        renderFluidTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderGhostUpgradeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Slot upgradeSlot = this.menu.slots.get(36);

        if (!upgradeSlot.hasItem() && this.isHovering(upgradeSlot.x, upgradeSlot.y, 16, 16, mouseX, mouseY)) {
            List<Component> tooltipText = List.of(
                    Component.literal("Upgrade Slot").withStyle(ChatFormatting.GOLD),
                    Component.literal("place any speed upgrade").withStyle(ChatFormatting.GRAY),
                    Component.literal("or amount or speed-amount upgrade").withStyle(ChatFormatting.GRAY),
                    Component.literal("upto tier 3").withStyle(ChatFormatting.GRAY),
                    Component.literal("here to determine").withStyle(ChatFormatting.GRAY),
                    Component.literal("the processing speed and amount").withStyle(ChatFormatting.GRAY)
            );

            guiGraphics.renderComponentTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    private void renderFluidTanks(GuiGraphics guiGraphics, int x, int y) {
        for (int i = 0; i < 4; i++) {
            FluidStack fluidStack = menu.getFluidStack(i);
            if (fluidStack.isEmpty()) continue;

            // Get Fluid Render Data
            var fluid = fluidStack.getFluid();
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
            ResourceLocation stillTexture = extensions.getStillTexture(fluidStack);

            // Fetch the actual texture sprite from the Minecraft atlas
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(stillTexture);

            // Calculate Height Percentage
            int fluidHeight = (int) (TANK_HEIGHT * ((float) fluidStack.getAmount() / TANK_CAPACITY));
            if (fluidHeight < 1 && fluidStack.getAmount() > 0) fluidHeight = 1; // Show at least a sliver

            // Set Fluid Color Tint
            int color = extensions.getTintColor(fluidStack);
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = ((color >> 24) & 0xFF) / 255f;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(r, g, b, a);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            int renderX = x + 57 + (i * 24);
            int renderY = y + 52 + (TANK_HEIGHT - fluidHeight); // Starts drawing from the bottom of the tank upwards

            guiGraphics.blit(renderX, renderY, 0, TANK_WIDTH, fluidHeight, sprite);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    private void renderFluidTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int i = 0; i < 4; i++) {
            int tankX = x + 56 + (i * 24);
            int tankY = y + 52;

            if (mouseX >= tankX && mouseX <= tankX + TANK_WIDTH && mouseY >= tankY && mouseY <= tankY + TANK_HEIGHT) {
                FluidStack fluid = menu.getFluidStack(i);

                Component text = fluid.isEmpty() ?
                        Component.literal("Empty").withStyle(ChatFormatting.GRAY) :
                        Component.literal(fluid.getDisplayName().getString() + ": ")
                        .append(Component.literal(fluid.getAmount() + " / " + TANK_CAPACITY + " mB").withStyle(ChatFormatting.AQUA));

                guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
}