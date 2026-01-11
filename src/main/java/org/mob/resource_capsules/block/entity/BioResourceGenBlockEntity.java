package org.mob.resource_capsules.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.BioResouceGenRecipe;
import org.mob.resource_capsules.screen.menu.BioResourceGenMenu;

import java.util.Optional;

public class BioResourceGenBlockEntity extends BlockEntity implements MenuProvider {

    // INVENTORY HANDLER
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int FLUID_CONTAINER_SLOT = 0;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // FLUID HANDLER
    private final FluidTank fluidTank = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return true;
        }
    };

    private LazyOptional<FluidTank> fluidOptional = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public BioResourceGenBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BIO_RESOURCE_GEN_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> BioResourceGenBlockEntity.this.progress;
                    case 1 -> BioResourceGenBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> BioResourceGenBlockEntity.this.progress = pValue;
                    case 1 -> BioResourceGenBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        fluidOptional = LazyOptional.of(() -> fluidTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        fluidOptional.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.bio_resource_gen");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BioResourceGenMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("bio_resource_gen.progress", progress);
        pTag.put("bio_resource_gen.fluid", fluidTank.writeToNBT(new CompoundTag()));
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("bio_resource_gen.progress");
        fluidTank.readFromNBT(pTag.getCompound("bio_resource_gen.fluid"));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        handleFluidContainer();

        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<BioResouceGenRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().getResultItem(null);
        FluidStack fluidNeeded = recipe.get().getInputFluid();

        this.itemHandler.extractItem(INPUT_SLOT, 1, true);
        this.fluidTank.drain(fluidNeeded.getAmount(), IFluidHandler.FluidAction.EXECUTE);

        this.itemHandler.setStackInSlot(OUTPUT_SLOT,
                new ItemStack(result.getItem(),
                        this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        Optional<BioResouceGenRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        BioResouceGenRecipe rec = recipe.get();
        ItemStack result = rec.getResultItem(getLevel().registryAccess());

        // Matches() already checked fluid and input items,
        // so we only check output space here.
        return canInsertAmountIntoOutputSlot(result.getCount()) &&
                canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<BioResouceGenRecipe> getCurrentRecipe() {
        // Use our custom container that holds both items AND fluid data
        BioResouceGenRecipe.BioContainer inventory = new BioResouceGenRecipe.BioContainer(
                itemHandler.getSlots(),
                fluidTank.getFluid()
        );

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(BioResouceGenRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <=
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    // FLUID CONTAINER HANDLING
    private void handleFluidContainer() {
        ItemStack inputStack = itemHandler.getStackInSlot(FLUID_CONTAINER_SLOT);
        if (inputStack.isEmpty()) return;

        // Try to fill the tank from container
        // Use FluidUtil to try and move fluid from the item into the internal tank
        FluidUtil.getFluidHandler(inputStack).ifPresent(containerHandler -> {
            // Try to empty the container into our tank
            var result = FluidUtil.tryEmptyContainer(inputStack, fluidTank, 1000, null, true);

            if (result.isSuccess()) {
                // result.getResult() gives you the EMPTY container (e.g., empty bucket)
                itemHandler.setStackInSlot(FLUID_CONTAINER_SLOT, result.getResult());
                setChanged();
            }
        });
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
