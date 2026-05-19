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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.entity.FluidGenMultiblockBlockEntity;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.screen.menu.FluidOutputHatchMenu;

public class FluidOutputHatchBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int linkedTank = 0;
    private BlockPos controllerPos = null;

    private final IFluidHandler fluidHandlerWrapper = new IFluidHandler() {
        @Nullable
        private IFluidHandler getRealTank() {
            if (controllerPos != null && level != null) {
                if (level.getBlockEntity(controllerPos) instanceof FluidGenMultiblockBlockEntity controller) {
                    return controller.fluidTanks[linkedTank];
                }
            }
            return null;
        }

        @Override public int getTanks() {
            IFluidHandler tank = getRealTank();
            return tank != null ? tank.getTanks() : 1;
        }

        @Override public @NotNull FluidStack getFluidInTank(int tank) {
            IFluidHandler real = getRealTank();
            return real != null ? real.getFluidInTank(tank) : FluidStack.EMPTY;
        }

        @Override public int getTankCapacity(int tank) {
            IFluidHandler real = getRealTank();
            return real != null ? real.getTankCapacity(tank) : 0;
        }

        @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return false; }
        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }

        @Override public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            IFluidHandler real = getRealTank();
            return real != null ? real.drain(resource, action) : FluidStack.EMPTY;
        }

        @Override public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            IFluidHandler real = getRealTank();
            return real != null ? real.drain(maxDrain, action) : FluidStack.EMPTY;
        }
    };

    public FluidOutputHatchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FLUID_OUTPUT_HATCH_BE.get(), pPos, pBlockState);

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

    public void setLinkedTank(int tankIndex) {
        this.linkedTank = tankIndex;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

            level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    public int getLinkedTank() {
        return this.linkedTank;
    }

    @Nullable
    public IFluidHandler getFluidHandler() {
        return this.fluidHandlerWrapper;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("LinkedTank", this.linkedTank);
        if (this.controllerPos != null) {
            pTag.putLong("ControllerPos", this.controllerPos.asLong());
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.linkedTank = pTag.getInt("LinkedTank");
        if (pTag.contains("ControllerPos")) {
            this.controllerPos = BlockPos.of(pTag.getLong("ControllerPos"));
        } else {
            this.controllerPos = null;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.fluid_output_hatch");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FluidOutputHatchMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
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