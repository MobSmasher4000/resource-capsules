package org.mob.resource_capsules.block.entity.dummy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;

public class ResourceGenMultiblockDummyBlockEntity extends BlockEntity {

    private BlockPos controllerPos = null;

    public ResourceGenMultiblockDummyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESOURCE_GEN_DUMMY_BE.get(), pPos, pBlockState);
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        this.setChanged();
    }

    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && controllerPos != null && level != null) {

            BlockEntity be = level.getBlockEntity(controllerPos);

            if (be instanceof ResourceGenMultiblockBlockEntity controller) {
                return controller.getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        if (controllerPos != null) {
            pTag.put("controller_pos", NbtUtils.writeBlockPos(controllerPos));
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("controller_pos")) {
            this.controllerPos = NbtUtils.readBlockPos(pTag.getCompound("controller_pos"));
        }
    }
}