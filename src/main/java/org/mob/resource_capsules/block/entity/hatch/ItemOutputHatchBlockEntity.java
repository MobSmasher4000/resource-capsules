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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;
import org.mob.resource_capsules.screen.menu.ItemOutputHatchMenu;

public class ItemOutputHatchBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int linkedSlot = 0;
    private BlockPos controllerPos = null;

    private LazyOptional<IItemHandler> itemCapability = LazyOptional.empty();

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
            @Override public void set(int pIndex, int pValue) {}
            @Override public int getCount() { return 4; }
        };
    }

    public void setLinkedSlot(int slot) {
        if (this.linkedSlot == slot) return;

        this.linkedSlot = slot;
        setChanged();

        if (level != null && !level.isClientSide()) {
            itemCapability.invalidate();
            itemCapability = LazyOptional.empty();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    public int getLinkedSlot() {
        return this.linkedSlot;
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
        if (level != null && !level.isClientSide()) {
            itemCapability.invalidate();
            itemCapability = LazyOptional.empty();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    @Nullable
    private IItemHandler getTargetHandler() {
        if (level == null || controllerPos == null) return null;
        BlockEntity be = level.getBlockEntity(controllerPos);
        if (be instanceof ResourceGenMultiblockBlockEntity controller) {
            return controller.outputHandler;
        }
        return null;
    }

    private IItemHandler createItemWrapper() {
        return new IItemHandler() {
            private boolean isValidTarget() {
                IItemHandler handler = getTargetHandler();
                return handler != null && linkedSlot >= 0 && linkedSlot < handler.getSlots();
            }

            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                if (slot == 0 && isValidTarget()) {
                    return getTargetHandler().getStackInSlot(linkedSlot);
                }
                return ItemStack.EMPTY;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack; // Output only
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 0 && isValidTarget()) {
                    return getTargetHandler().extractItem(linkedSlot, amount, simulate);
                }
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot == 0 && isValidTarget()) {
                    return getTargetHandler().getSlotLimit(linkedSlot);
                }
                return 0;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false; // Output only
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (!itemCapability.isPresent()) {
                itemCapability = LazyOptional.of(this::createItemWrapper);
            }
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
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
            this.controllerPos = null;
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
}