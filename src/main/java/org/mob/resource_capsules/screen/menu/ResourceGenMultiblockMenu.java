package org.mob.resource_capsules.screen.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mob.mob_lib.inventory.slot.OutputSlot;
import org.mob.mob_lib.inventory.slot.SingleSlot;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;
import org.mob.resource_capsules.block.entity.resource_gen_tier.ResourceGenTier1BlockEntity;
import org.mob.resource_capsules.screen.ModMenuTypes;

public class ResourceGenMultiblockMenu extends AbstractContainerMenu {
    public final ResourceGenMultiblockBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ResourceGenMultiblockMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public ResourceGenMultiblockMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.RESOURCE_GEN_MULTIBLOCK_MENU.get(), pContainerId);

        blockEntity = (ResourceGenMultiblockBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SingleSlot(blockEntity.upgradeHandler, 0, 6, 6)); // slot 36
        this.addSlot(new SingleSlot(blockEntity.machineHandler, 0, 6, 27)); // slot 37
        this.addSlot(new SingleSlot(blockEntity.inputHandler, 0, 56, 7));   // slot 38
        this.addSlot(new SingleSlot(blockEntity.inputHandler, 1, 80, 7));   // slot 39
        this.addSlot(new SingleSlot(blockEntity.inputHandler, 2, 104, 7));   // slot 40
        this.addSlot(new SingleSlot(blockEntity.inputHandler, 3, 128, 7));   // slot 41
        this.addSlot(new OutputSlot(blockEntity.outputHandler, 0, 90, 56)); // slot 42

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;


    private static final int TE_INVENTORY_SLOT_COUNT = 7;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 1. Shift-clicking from Player Inventory -> Machine
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            // Shift-clicking till before output slot
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + 6, false)) {
                return ItemStack.EMPTY;
            }

        }
        // 2. Shift-clicking from Machine -> Player Inventory
        else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.RESOURCE_GEN_MULTIBLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }
}