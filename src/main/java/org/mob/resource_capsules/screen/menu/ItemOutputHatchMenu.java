package org.mob.resource_capsules.screen.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.entity.hatch.ItemOutputHatchBlockEntity;
import org.mob.resource_capsules.screen.ModMenuTypes;

public class ItemOutputHatchMenu extends AbstractContainerMenu {
    public final ItemOutputHatchBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ItemOutputHatchMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public ItemOutputHatchMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ITEM_OUTPUT_HATCH_MENU.get(), pContainerId);

        this.blockEntity = (ItemOutputHatchBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    public boolean hasController() { return data.get(3) == 1; }
    public int getCtrlX() { return data.get(0); }
    public int getCtrlY() { return data.get(1); }
    public int getCtrlZ() { return data.get(2); }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.ITEM_OUTPUT_HATCH.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18)); // Adjust Y coordinates to fit your GUI texture!
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}