package org.mob.resource_capsules.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.network.ModMessages;
import org.mob.resource_capsules.network.packet.SetHatchLinkPacket;
import org.mob.resource_capsules.screen.menu.ItemOutputHatchMenu;

public class ItemOutputHatchScreen extends AbstractContainerScreen<ItemOutputHatchMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ResourceCapsules.MOD_ID, "textures/gui/hatch/item_output_hatch_gui.png");

    private final Button[] slotButtons = new Button[4];

    public ItemOutputHatchScreen(ItemOutputHatchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int i = 0; i < 4; i++) {
            final int slotIndex = i;

            int buttonX = x + 20 + (i * 35);
            int buttonY = y + 20;

            slotButtons[i] = this.addRenderableWidget(Button.builder(Component.literal("Slot " + (i + 1)), button -> {
                ModMessages.sendToServer(new SetHatchLinkPacket(this.menu.blockEntity.getBlockPos(), slotIndex));
                this.menu.blockEntity.setLinkedSlot(slotIndex);
            }).bounds(buttonX, buttonY, 32, 20).build());
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);

        int currentLinkedSlot = this.menu.blockEntity.getLinkedSlot();
        for (int i = 0; i < 4; i++) {
            slotButtons[i].active = (i != currentLinkedSlot);
        }

        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        if (menu.hasController()) {
            String posText = "Linked Controller: " + menu.getCtrlX() + ", " + menu.getCtrlY() + ", " + menu.getCtrlZ();
            guiGraphics.drawString(this.font, posText, 20, 50, 0x404040, false);
        } else {
            guiGraphics.drawString(this.font, "Linked Controller: NONE", 20, 50, 0xFF0000, false);
        }
    }
}