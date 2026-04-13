package org.mob.resource_capsules.block.entity.hatch;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;
import org.mob.resource_capsules.screen.menu.ItemOutputHatchMenu;

public class ItemOutputHatchBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int linkedSlot = 0;
    private BlockPos controllerPos = null;

    public ItemOutputHatchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ITEM_OUTPUT_HATCH_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                if (controllerPos == null) return pIndex == 3 ? 0 : 0;

                return switch (pIndex) {
                    case 0 -> controllerPos.getX();
                    case 1 -> controllerPos.getY();
                    case 2 -> controllerPos.getZ();
                    case 3 -> 1;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {}

            @Override
            public int getCount() { return 4; }
        };
    }

    public void setLinkedSlot(int slot) {
        this.linkedSlot = slot;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public int getLinkedSlot() {
        return this.linkedSlot;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && controllerPos != null && level != null) {

            BlockEntity controllerBe = level.getBlockEntity(controllerPos);

            if (controllerBe instanceof ResourceGenMultiblockBlockEntity controller) {

                // Isolate the specific slot using RangedWrapper
                IItemHandler isolatedSlot = new RangedWrapper(controller.outputHandler, linkedSlot, linkedSlot + 1);

                // Create the One-Way Valve Wrapper
                IItemHandler extractOnlyWrapper = new IItemHandler() {
                    @Override public int getSlots() { return isolatedSlot.getSlots(); }
                    @Override public @NotNull ItemStack getStackInSlot(int slot) { return isolatedSlot.getStackInSlot(slot); }
                    @Override public int getSlotLimit(int slot) { return isolatedSlot.getSlotLimit(slot); }

                    // Allow extraction
                    @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                        return isolatedSlot.extractItem(slot, amount, simulate);
                    }

                    // DENY all insertions
                    @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                        return false;
                    }
                    @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                        return stack;
                    }
                };

                return LazyOptional.of(() -> extractOnlyWrapper).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("LinkedSlot", this.linkedSlot);
        if (this.controllerPos != null) {
            pTag.put("ControllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.linkedSlot = pTag.getInt("LinkedSlot");
        if (pTag.contains("ControllerPos")) {
            this.controllerPos = NbtUtils.readBlockPos(pTag.getCompound("ControllerPos"));
        } else {
            this.controllerPos = null; // Failsafe
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.item_output_hatch");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ItemOutputHatchMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
    }
}