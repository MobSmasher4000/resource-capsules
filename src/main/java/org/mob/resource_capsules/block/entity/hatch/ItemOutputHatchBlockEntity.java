package org.mob.resource_capsules.block.entity.hatch;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;
import org.mob.resource_capsules.screen.menu.ItemOutputHatchMenu;

public class ItemOutputHatchBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int linkedSlot = 0;
    private BlockPos controllerPos = null;

    private final IItemHandler itemHandlerWrapper = new IItemHandler() {
        @Nullable
        private IItemHandler getRealHandler() {
            if (controllerPos != null && level != null) {
                if (level.getBlockEntity(controllerPos) instanceof ResourceGenMultiblockBlockEntity controller) {
                    return controller.outputHandler;
                }
            }
            return null;
        }

        @Override
        public int getSlots() { return 1; }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            IItemHandler real = getRealHandler();
            return real != null ? real.getStackInSlot(linkedSlot) : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            IItemHandler real = getRealHandler();
            return real != null ? real.extractItem(linkedSlot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            IItemHandler real = getRealHandler();
            return real != null ? real.getSlotLimit(linkedSlot) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) { return false; }
    };

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

            level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    public int getLinkedSlot() {
        return this.linkedSlot;
    }

    @Nullable
    public IItemHandler getItemHandler() {
        return this.itemHandlerWrapper;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("LinkedSlot", this.linkedSlot);
        if (this.controllerPos != null) {
            pTag.putLong("ControllerPos", this.controllerPos.asLong());
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.linkedSlot = pTag.getInt("LinkedSlot");
        if (pTag.contains("ControllerPos")) {
            this.controllerPos = BlockPos.of(pTag.getLong("ControllerPos"));
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

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}