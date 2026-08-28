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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.entity.FluidGenMultiblockBlockEntity;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.screen.menu.FluidOutputHatchMenu;

public class FluidOutputHatchBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int linkedTank = 0;
    private BlockPos controllerPos = null;

    private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.empty();

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
        if (this.linkedTank == tankIndex) return;

        this.linkedTank = tankIndex;
        setChanged();

        if (level != null && !level.isClientSide()) {
            fluidCapability.invalidate();
            fluidCapability = LazyOptional.empty();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    public int getLinkedTank() {
        return this.linkedTank;
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
    }

    @Nullable
    private IFluidHandler getTargetTank() {
        if (level == null || controllerPos == null) return null;

        BlockEntity be = level.getBlockEntity(controllerPos);
        if (be instanceof FluidGenMultiblockBlockEntity controller) {
            if (linkedTank >= 0 && linkedTank < controller.fluidTanks.length) {
                return controller.fluidTanks[linkedTank];
            }
        }
        return null;
    }

    private IFluidHandler createFluidWrapper() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                IFluidHandler tank = getTargetTank();
                return tank != null ? tank.getTanks() : 0;
            }

            @Override
            public @NotNull FluidStack getFluidInTank(int tankId) {
                IFluidHandler tank = getTargetTank();
                return tank != null ? tank.getFluidInTank(tankId) : FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tankId) {
                IFluidHandler tank = getTargetTank();
                return tank != null ? tank.getTankCapacity(tankId) : 0;
            }

            @Override
            public boolean isFluidValid(int tankId, @NotNull FluidStack stack) {
                return false; // Output only
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return 0; // Output only
            }

            @Override
            public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
                IFluidHandler tank = getTargetTank();
                return tank != null ? tank.drain(resource, action) : FluidStack.EMPTY;
            }

            @Override
            public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
                IFluidHandler tank = getTargetTank();
                return tank != null ? tank.drain(maxDrain, action) : FluidStack.EMPTY;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (!fluidCapability.isPresent()) {
                fluidCapability = LazyOptional.of(this::createFluidWrapper);
            }
            return fluidCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidCapability.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("LinkedTank", this.linkedTank);
        if (this.controllerPos != null) {
            pTag.put("ControllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.linkedTank = pTag.getInt("LinkedTank");
        if (pTag.contains("ControllerPos")) {
            this.controllerPos = NbtUtils.readBlockPos(pTag.getCompound("ControllerPos"));
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
}