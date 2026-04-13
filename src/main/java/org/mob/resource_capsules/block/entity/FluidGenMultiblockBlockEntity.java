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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.item.custom.UpgradeItem;
import org.mob.resource_capsules.block.custom.FluidGenMultiblockBlock;
import org.mob.resource_capsules.recipe.FluidGenRecipe;
import org.mob.resource_capsules.screen.menu.FluidGenMultiblockMenu;
import org.mob.resource_capsules.util.ModTags;

import java.util.Optional;

public class FluidGenMultiblockBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler inputHandler = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final ItemStackHandler upgradeHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); upgrade();}
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModTags.Items.FLUID_GENERATOR_UPGRADES);
        }
    };

    public final FluidTank[] fluidTanks = new FluidTank[] {
            new FluidTank(64000) { @Override protected void onContentsChanged() { setChanged(); if(level != null && !level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);}},
            new FluidTank(64000) { @Override protected void onContentsChanged() { setChanged(); if(level != null && !level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);}},
            new FluidTank(64000) { @Override protected void onContentsChanged() { setChanged(); if(level != null && !level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);}},
            new FluidTank(64000) { @Override protected void onContentsChanged() { setChanged(); if(level != null && !level.isClientSide()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);}}
    };

    private final RecipeManager.CachedCheck<SimpleContainer, FluidGenRecipe> quickCheckFluid;

    protected final ContainerData data;

    private int tickCount = 0;
    private final int[] progress = new int[]{0, 0, 0, 0};
    private int maxProgress = 100;
    private int processAmount = 1;
    private boolean showPreview = false;

    public FluidGenMultiblockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FLUID_GEN_MULTIBLOCK_BE.get(), pPos, pBlockState);

        this.quickCheckFluid = RecipeManager.createCheck(FluidGenRecipe.Type.INSTANCE);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                if (pIndex < 4) return FluidGenMultiblockBlockEntity.this.progress[pIndex];
                if (pIndex == 4) return FluidGenMultiblockBlockEntity.this.maxProgress;
                return 0;
            }
            @Override
            public void set(int pIndex, int pValue) {
                if (pIndex < 4) FluidGenMultiblockBlockEntity.this.progress[pIndex] = pValue;
                else if (pIndex == 4) FluidGenMultiblockBlockEntity.this.maxProgress = pValue;
            }
            @Override
            public int getCount() { return 5; }
        };
    }

    public void upgrade(){
        ItemStack upgradeStack = upgradeHandler.getStackInSlot(0);
        if (upgradeStack.isEmpty()){
            maxProgress = 100;
            processAmount = 1;
            return;
        }
        if (upgradeStack.getItem() instanceof UpgradeItem upgradeItem){
            maxProgress = upgradeItem.getSpeed();
            processAmount = upgradeItem.getAmount();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        upgrade();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        tickCount++;
        if (tickCount >= 20) {
            tickCount = 0;
            if (pState.getBlock() instanceof FluidGenMultiblockBlock block) {
                block.checkForMultiblock(pLevel, pPos);
            }
        }

        if (!pState.getValue(FluidGenMultiblockBlock.FORMED)) {
            for (int i = 0; i < 4; i++) progress[i] = 0;
            return;
        }

        boolean isActive = false;

        for (int i = 0; i < 4; i++) {
            if (hasRecipe(i)) {
                isActive = true;
                progress[i]++;

                if (progress[i] >= maxProgress) {
                    craftItem(i);
                    progress[i] = 0;
                }
            } else {
                progress[i] = 0;
            }
        }

        if (isActive) {
            setChanged(pLevel, pPos, pState);
        }
    }

    private void craftItem(int slotIndex) {
        Optional<FluidGenRecipe> recipe = getCurrentRecipe(slotIndex);

        if(recipe.isPresent()) {
            FluidStack resultFluid = recipe.get().getOutputFluid();
            resultFluid.setAmount(resultFluid.getAmount() * processAmount);

            this.inputHandler.extractItem(slotIndex, 1, true);

            this.fluidTanks[slotIndex].fill(resultFluid, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private boolean hasRecipe(int slotIndex) {
        Optional<FluidGenRecipe> recipe = getCurrentRecipe(slotIndex);
        if(recipe.isEmpty()) return false;

        FluidStack resultFluid = recipe.get().getOutputFluid();
        resultFluid.setAmount(resultFluid.getAmount() * processAmount);

        // Check if the specific tank has enough room for this liquid
        return this.fluidTanks[slotIndex].fill(resultFluid, IFluidHandler.FluidAction.SIMULATE) == resultFluid.getAmount();
    }

    private Optional<FluidGenRecipe> getCurrentRecipe(int slotIndex) {
        ItemStack inputStack = this.inputHandler.getStackInSlot(slotIndex);

        if (inputStack.isEmpty()) return Optional.empty();

        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, inputStack);

        return this.quickCheckFluid.getRecipeFor(inventory, this.level);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("upgrade", upgradeHandler.serializeNBT());

        // Save the 4 Fluid Tanks
        for (int i = 0; i < 4; i++) {
            CompoundTag tankTag = new CompoundTag();
            this.fluidTanks[i].writeToNBT(tankTag);
            pTag.put("FluidTank_" + i, tankTag);
        }

        pTag.putIntArray("progresses", progress);
        pTag.putBoolean("showPreview", this.showPreview);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        upgradeHandler.deserializeNBT(pTag.getCompound("upgrade"));

        for (int i = 0; i < 4; i++) {
            if (pTag.contains("FluidTank_" + i)) {
                this.fluidTanks[i].readFromNBT(pTag.getCompound("FluidTank_" + i));
            }
        }

        int[] loadedProgress = pTag.getIntArray("progresses");
        if (loadedProgress.length == 4) {
            System.arraycopy(loadedProgress, 0, this.progress, 0, 4);
        }
        this.showPreview = pTag.getBoolean("showPreview");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(5); // 4 inputs + upgrade
        inventory.setItem(0, inputHandler.getStackInSlot(0));
        inventory.setItem(1, inputHandler.getStackInSlot(1));
        inventory.setItem(2, inputHandler.getStackInSlot(2));
        inventory.setItem(3, inputHandler.getStackInSlot(3));
        inventory.setItem(4, upgradeHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.fluid_gen_multiblock");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FluidGenMultiblockMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public boolean isShowPreview() { return this.showPreview; }

    public void togglePreview() {
        this.showPreview = !this.showPreview;
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}