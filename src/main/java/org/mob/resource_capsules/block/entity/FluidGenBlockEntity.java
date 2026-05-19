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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.component.ModDataComponents;
import org.mob.mob_lib.item.custom.UpgradeItem;
import org.mob.mob_lib.util.ModTags;
import org.mob.resource_capsules.recipe.FluidGenRecipe;
import org.mob.resource_capsules.screen.menu.FluidGenMenu;

import java.util.Optional;

public class FluidGenBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final ItemStackHandler upgradeHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            upgrade();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModTags.Items.MOB_UPGRADES_SPEED);
        }
    };

    public final FluidTank fluidTank = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return true;
        }
    };

    private final RecipeManager.CachedCheck<SingleRecipeInput, FluidGenRecipe> quickRecipeCacheCheck;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public FluidGenBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FLUID_GEN_BE.get(), pPos, pBlockState);

        this.quickRecipeCacheCheck = RecipeManager.createCheck(FluidGenRecipe.Type.INSTANCE);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FluidGenBlockEntity.this.progress;
                    case 1 -> FluidGenBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> FluidGenBlockEntity.this.progress = pValue;
                    case 1 -> FluidGenBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private void upgrade(){
        ItemStack upgradeStack = upgradeHandler.getStackInSlot(0);
        if (upgradeStack.isEmpty()){
            maxProgress = 100;
            return;
        }
        maxProgress = upgradeStack.getOrDefault(ModDataComponents.SPEED, 100);
    }

    public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
        return fluidTank;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        upgrade();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, inputHandler.getStackInSlot(0));
        inventory.setItem(1, upgradeHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()) {
            progress++;

            if (progress >= this.maxProgress) {
                processRecipe();
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }

    private void processRecipe() {
        Optional<RecipeHolder<FluidGenRecipe>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            FluidStack resultFluid = recipe.get().value().getOutputFluid();

            this.inputHandler.extractItem(0, 1, true);

            this.fluidTank.fill(resultFluid, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<FluidGenRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        FluidStack resultFluid = recipe.get().value().getOutputFluid();

        return this.fluidTank.fill(resultFluid, IFluidHandler.FluidAction.SIMULATE) == resultFluid.getAmount();
    }

    private Optional<RecipeHolder<FluidGenRecipe>> getCurrentRecipe() {
        ItemStack inputStack = inputHandler.getStackInSlot(0);
        if (inputStack.isEmpty()) return Optional.empty();

        return this.quickRecipeCacheCheck.getRecipeFor(new SingleRecipeInput(inputStack), level);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("input", inputHandler.serializeNBT(pRegistries));
        pTag.put("upgrade", upgradeHandler.serializeNBT(pRegistries));
        fluidTank.writeToNBT(pRegistries, pTag);
        pTag.putInt("progress", progress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inputHandler.deserializeNBT(pRegistries, pTag.getCompound("input"));
        upgradeHandler.deserializeNBT(pRegistries, pTag.getCompound("upgrade"));
        fluidTank.readFromNBT(pRegistries, pTag);
        progress = pTag.getInt("progress");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.fluid_gen");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FluidGenMenu(pContainerId, pPlayerInventory, this, this.data);
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