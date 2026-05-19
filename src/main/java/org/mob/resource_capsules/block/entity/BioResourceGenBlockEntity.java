package org.mob.resource_capsules.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.BioResouceGenRecipe;
import org.mob.resource_capsules.screen.menu.BioResourceGenMenu;

import java.util.Optional;

public class BioResourceGenBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public final ItemStackHandler outputHandler = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 1;
    private static final int FLUID_CONTAINER_SLOT = 0;

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

    private final RecipeManager.CachedCheck<BioResouceGenRecipe.BioRecipeInput, BioResouceGenRecipe> quickCheck;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public BioResourceGenBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BIO_RESOURCE_GEN_BE.get(), pPos, pBlockState);

        this.quickCheck = RecipeManager.createCheck(BioResouceGenRecipe.Type.INSTANCE);

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

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        return outputHandler;
    }

    public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
        return fluidTank;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(3);
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        inventory.setItem(1, itemHandler.getStackInSlot(1));
        inventory.setItem(2, outputHandler.getStackInSlot(0));
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
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.put("output", outputHandler.serializeNBT(pRegistries));
        pTag.putInt("bio_resource_gen.progress", progress);

        CompoundTag fluidTag = new CompoundTag();
        fluidTank.writeToNBT(pRegistries, fluidTag);
        pTag.put("bio_resource_gen.fluid", fluidTag);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        outputHandler.deserializeNBT(pRegistries, pTag.getCompound("output"));
        progress = pTag.getInt("bio_resource_gen.progress");
        fluidTank.readFromNBT(pRegistries, pTag.getCompound("bio_resource_gen.fluid"));
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
        Optional<RecipeHolder<BioResouceGenRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().value().getResultItem(this.level.registryAccess());
        FluidStack fluidNeeded = recipe.get().value().getInputFluid();

        this.itemHandler.extractItem(INPUT_SLOT, 1, true);
        this.fluidTank.drain(fluidNeeded.getAmount(), IFluidHandler.FluidAction.EXECUTE);

        this.outputHandler.setStackInSlot(0,
                new ItemStack(result.getItem(),
                        this.outputHandler.getStackInSlot(0).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<BioResouceGenRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        BioResouceGenRecipe rec = recipe.get().value();
        ItemStack result = rec.getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) &&
                canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<RecipeHolder<BioResouceGenRecipe>> getCurrentRecipe() {
        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        if(inputStack.isEmpty()) return Optional.empty();

        BioResouceGenRecipe.BioRecipeInput input = new BioResouceGenRecipe.BioRecipeInput(inputStack, fluidTank.getFluid());

        return this.quickCheck.getRecipeFor(input, this.level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.outputHandler.getStackInSlot(0).isEmpty() ||
                this.outputHandler.getStackInSlot(0).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.outputHandler.getStackInSlot(0).getCount() + count <=
                this.outputHandler.getStackInSlot(0).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private void handleFluidContainer() {
        ItemStack inputStack = itemHandler.getStackInSlot(FLUID_CONTAINER_SLOT);
        if (inputStack.isEmpty()) return;

        var result = FluidUtil.tryEmptyContainer(inputStack, fluidTank, 1000, null, true);

        if (result.isSuccess()) {
            itemHandler.setStackInSlot(FLUID_CONTAINER_SLOT, result.getResult());
            setChanged();
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, pRegistries);
        return tag;
    }
}