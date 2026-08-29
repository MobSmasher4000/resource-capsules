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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.item.custom.UpgradeItem;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.custom.ResourceGenMultiblockBlock;
import org.mob.resource_capsules.recipe.*;
import org.mob.resource_capsules.screen.menu.ResourceGenMultiblockMenu;
import org.mob.resource_capsules.util.ModTags;

import java.util.Optional;

public class ResourceGenMultiblockBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler inputHandler = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    public final ItemStackHandler machineHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModTags.Items.RESOURCE_GENERATOR);
        }
    };

    public final ItemStackHandler outputHandler = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) { return 2048; }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) { return 2048; }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = super.serializeNBT();
            for (int i = 0; i < this.getSlots(); i++) {
                nbt.putInt("RealCount_" + i, this.stacks.get(i).getCount());
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            for (int i = 0; i < this.getSlots(); i++) {
                if(nbt.contains("RealCount_" + i)) {
                    this.stacks.get(i).setCount(nbt.getInt("RealCount_" + i));
                }
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public final ItemStackHandler upgradeHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); upgrade();}
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(org.mob.mob_lib.util.ModTags.Items.MOB_UPGRADES);
        }
    };

    private LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.empty();

    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier1Recipe> quickCheckTier1;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier2Recipe> quickCheckTier2;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier3Recipe> quickCheckTier3;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier4Recipe> quickCheckTier4;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier5Recipe> quickCheckTier5;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier6Recipe> quickCheckTier6;

    protected final ContainerData data;

    private int tickCount = 0;
    private final int[] progress = new int[]{0, 0, 0, 0};
    private int maxProgress = 100;
    private int processAmount = 1;
    private boolean showPreview = false;

    public ResourceGenMultiblockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESOURCE_GEN_MULTIBLOCK_BE.get(), pPos, pBlockState);

        this.quickCheckTier1 = RecipeManager.createCheck(ResourceGenTier1Recipe.Type.INSTANCE);
        this.quickCheckTier2 = RecipeManager.createCheck(ResourceGenTier2Recipe.Type.INSTANCE);
        this.quickCheckTier3 = RecipeManager.createCheck(ResourceGenTier3Recipe.Type.INSTANCE);
        this.quickCheckTier4 = RecipeManager.createCheck(ResourceGenTier4Recipe.Type.INSTANCE);
        this.quickCheckTier5 = RecipeManager.createCheck(ResourceGenTier5Recipe.Type.INSTANCE);
        this.quickCheckTier6 = RecipeManager.createCheck(ResourceGenTier6Recipe.Type.INSTANCE);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                if (pIndex < 4) return ResourceGenMultiblockBlockEntity.this.progress[pIndex];
                if (pIndex == 4) return ResourceGenMultiblockBlockEntity.this.maxProgress;
                return 0;
            }

            @Override
            public void set(int pIndex, int pValue) {
                if (pIndex < 4) ResourceGenMultiblockBlockEntity.this.progress[pIndex] = pValue;
                else if (pIndex == 4) ResourceGenMultiblockBlockEntity.this.maxProgress = pValue;
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public void upgrade(){
        ItemStack upgradeStack = upgradeHandler.getStackInSlot(0);
        if (upgradeStack.isEmpty()){
            maxProgress = 100;
            processAmount = 1;
            return;
        }
        if (!upgradeStack.isEmpty() && upgradeStack.getItem() instanceof UpgradeItem upgradeItem){
            maxProgress = upgradeItem.getSpeed();
            processAmount = upgradeItem.getAmount();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyOutputHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyOutputHandler = LazyOptional.of(() -> outputHandler);
        upgrade();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyOutputHandler.invalidate();
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {

        tickCount++;
        if (tickCount >= 20) {
            tickCount = 0;
            if (pState.getBlock() instanceof ResourceGenMultiblockBlock block) {
                block.checkForMultiblock(pLevel, pPos);
            }
        }

        if (!pState.getValue(ResourceGenMultiblockBlock.FORMED)) {
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
        Optional<? extends Recipe<SimpleContainer>> recipe = getCurrentRecipe(slotIndex);

        if(recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

            this.inputHandler.extractItem(slotIndex, 1, true);

            this.outputHandler.setStackInSlot(slotIndex, new ItemStack(result.getItem(),
                    this.outputHandler.getStackInSlot(slotIndex).getCount() + processAmount));
        }
    }

    private boolean hasRecipe(int slotIndex) {
        Optional<? extends Recipe<SimpleContainer>> recipe = getCurrentRecipe(slotIndex);
        if(recipe.isEmpty()) return false;

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return this.outputHandler.getStackInSlot(slotIndex).isEmpty() ||
                (this.outputHandler.getStackInSlot(slotIndex).is(result.getItem()) &&
                        this.outputHandler.getStackInSlot(slotIndex).getCount() + processAmount <= this.outputHandler.getSlotLimit(slotIndex));
    }

    private Optional<? extends Recipe<SimpleContainer>> getCurrentRecipe(int slotIndex) {
        ItemStack machineStack = this.machineHandler.getStackInSlot(0);
        ItemStack inputStack = this.inputHandler.getStackInSlot(slotIndex);

        if (machineStack.isEmpty() || inputStack.isEmpty()) return Optional.empty();

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, inputStack);
        inventory.setItem(1, machineStack);

        return checkRecipeCache(inventory, machineStack);
    }

    private Optional<? extends Recipe<SimpleContainer>> checkRecipeCache(SimpleContainer inventory, ItemStack machineStack) {
        if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_1.get().asItem())) {
            return this.quickCheckTier1.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_2.get().asItem())) {
            return this.quickCheckTier2.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_3.get().asItem())) {
            return this.quickCheckTier3.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_4.get().asItem())) {
            return this.quickCheckTier4.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_5.get().asItem())) {
            return this.quickCheckTier5.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_6.get().asItem())) {
            return this.quickCheckTier6.getRecipeFor(inventory, this.level);
        }
        return Optional.empty();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("machine", machineHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.put("upgrade", upgradeHandler.serializeNBT());
        pTag.putIntArray("progresses", progress);
        pTag.putBoolean("showPreview", this.showPreview);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        machineHandler.deserializeNBT(pTag.getCompound("machine"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        upgradeHandler.deserializeNBT(pTag.getCompound("upgrade"));

        int[] loadedProgress = pTag.getIntArray("progresses");
        if (loadedProgress.length == 4) {
            System.arraycopy(loadedProgress, 0, this.progress, 0, 4);
        }
        this.showPreview = pTag.getBoolean("showPreview");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(10);
        for (int i = 0; i < 4; i++) inventory.setItem(i, inputHandler.getStackInSlot(i));
        inventory.setItem(4, machineHandler.getStackInSlot(0));
        inventory.setItem(5, upgradeHandler.getStackInSlot(0));
        for (int i = 0; i < 4; i++) inventory.setItem(6 + i, outputHandler.getStackInSlot(i));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.resource_gen_multiblock");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ResourceGenMultiblockMenu(pContainerId, pPlayerInventory, this, this.data);
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
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}