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
import org.mob.resource_capsules.block.entity.FluidGenMultiblockBlockEntity; // Make sure this imports the FLUID controller!
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.screen.menu.FluidOutputHatchMenu;

public class FluidOutputHatchBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int linkedTank = 0;
    private BlockPos controllerPos = null;

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
        }
    }

    public int getLinkedTank() {
        return this.linkedTank;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && controllerPos != null && level != null) {

            BlockEntity controllerBe = level.getBlockEntity(controllerPos);

            if (controllerBe instanceof FluidGenMultiblockBlockEntity controller) {

                // Grab the real tank from the controller
                IFluidHandler realTank = controller.fluidTanks[linkedTank];

                // "One-Way Valve" wrapper that prevents pipes from pushing fluids in
                IFluidHandler extractOnlyWrapper = new IFluidHandler() {
                    @Override public int getTanks() { return realTank.getTanks(); }
                    @Override public @NotNull FluidStack getFluidInTank(int tank) { return realTank.getFluidInTank(tank); }
                    @Override public int getTankCapacity(int tank) { return realTank.getTankCapacity(tank); }

                    // Deny all insertion attempts
                    @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return false; }
                    @Override public int fill(FluidStack resource, FluidAction action) { return 0; }

                    // Allow extraction
                    @Override public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return realTank.drain(resource, action); }
                    @Override public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return realTank.drain(maxDrain, action); }
                };

                return LazyOptional.of(() -> extractOnlyWrapper).cast();
            }
        }

        return super.getCapability(cap, side);
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

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
    }
}