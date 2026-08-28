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
import net.minecraft.world.item.ItemStack;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.screen.menu.ResourceGenMultiblockMenu;

import java.util.List;

public class ResourceGenMultiblockScreen extends AbstractContainerScreen<ResourceGenMultiblockMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ResourceCapsules.MOD_ID, "textures/gui/resource_gen_tier/resource_gen_multiblock_gui.png");

    private static final ResourceLocation ARROW_TEXTURE =
            new ResourceLocation(ResourceCapsules.MOD_ID, "textures/gui/arrow_progress_multiblock.png");

    private final List<ItemStack> validMachines = List.of(
            new ItemStack(ModBlocks.RESOURCE_GEN_TIER_1.get()),
            new ItemStack(ModBlocks.RESOURCE_GEN_TIER_2.get()),
            new ItemStack(ModBlocks.RESOURCE_GEN_TIER_3.get()),
            new ItemStack(ModBlocks.RESOURCE_GEN_TIER_4.get()),
            new ItemStack(ModBlocks.RESOURCE_GEN_TIER_5.get())
    );

    public ResourceGenMultiblockScreen(ResourceGenMultiblockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
                        0,              // Texture U
                        0,                      // Texture V (Y offset on png)
                        arrowWidth,             // Width to draw
                        progress,               // Height to draw (grows larger over time)
                        arrowWidth, arrowHeight // Total dimensions of the ARROW_TEXTURE png file
                );
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        renderGhostMachineItems(guiGraphics);
        renderGhostMachineTooltip(guiGraphics, mouseX, mouseY);
        renderGhostUpgradeTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderGhostMachineItems(GuiGraphics guiGraphics) {
        Slot machineSlot = this.menu.slots.get(37);

        if (!machineSlot.hasItem()) {
            // Cycle the item once per second
            int index = (int) ((System.currentTimeMillis() / 1000) % validMachines.size());
            ItemStack displayStack = validMachines.get(index);

            int x = this.leftPos + machineSlot.x;
            int y = this.topPos + machineSlot.y;

            guiGraphics.renderFakeItem(displayStack, x, y);
            guiGraphics.fill(x, y, x + 16, y + 16, 0x80000000);
        }
    }

    private void renderGhostMachineTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Slot machineSlot = this.menu.slots.get(37);

        // Only show text if slot is empty AND the mouse is currently over it
        if (!machineSlot.hasItem() && this.isHovering(machineSlot.x, machineSlot.y, 16, 16, mouseX, mouseY)) {
            List<Component> tooltipText = List.of(
                    Component.literal("Machine Controller Slot").withStyle(ChatFormatting.GOLD),
                    Component.literal("place Resource Generator").withStyle(ChatFormatting.GRAY),
                    Component.literal("here to determine").withStyle(ChatFormatting.GRAY),
                    Component.literal("the processing tier").withStyle(ChatFormatting.GRAY)
            );

            guiGraphics.renderComponentTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    private void renderGhostUpgradeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Slot machineSlot = this.menu.slots.get(36);

        // Only show text if slot is empty AND the mouse is currently over it
        if (!machineSlot.hasItem() && this.isHovering(machineSlot.x, machineSlot.y, 16, 16, mouseX, mouseY)) {
            List<Component> tooltipText = List.of(
                    Component.literal("Upgrade Slot").withStyle(ChatFormatting.GOLD),
                    Component.literal("place any upgrade").withStyle(ChatFormatting.GRAY),
                    Component.literal("here to determine").withStyle(ChatFormatting.GRAY),
                    Component.literal("the processing speed and amount").withStyle(ChatFormatting.GRAY)
            );

            guiGraphics.renderComponentTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
}