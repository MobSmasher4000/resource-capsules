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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.item.custom.UpgradeItem;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.custom.ResourceGenMultiblockBlock;
import org.mob.resource_capsules.recipe.ResourceGenTier1Recipe;
import org.mob.resource_capsules.recipe.ResourceGenTier2Recipe;
import org.mob.resource_capsules.recipe.ResourceGenTier3Recipe;
import org.mob.resource_capsules.screen.menu.ResourceGenMultiblockMenu;
import org.mob.resource_capsules.util.ModTags;

import java.util.ArrayList;
import java.util.List;
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
    public final ItemStackHandler outputHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 16384;
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 16384;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = super.serializeNBT();
            nbt.putInt("RealCount", this.stacks.get(0).getCount());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            if(nbt.contains("RealCount")) {
                this.stacks.get(0).setCount(nbt.getInt("RealCount"));
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

    private int maxProgress = 100;
    private int processAmount = 1;
    public final ItemStackHandler upgradeHandler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); upgrade();}
        private void upgrade(){
            ItemStack upgradeStack = getStackInSlot(0);
            if (getStackInSlot(0).isEmpty()){
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
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(org.mob.mob_lib.util.ModTags.Items.ORE_UPGRADES);
        }
    };

    private LazyOptional<IItemHandler> lazyOptionalHandler = LazyOptional.empty();

    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier1Recipe> quickCheckTier1;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier2Recipe> quickCheckTier2;
    private final RecipeManager.CachedCheck<SimpleContainer, ResourceGenTier3Recipe> quickCheckTier3;

    protected final ContainerData data;
    private int progress = 0;
    private boolean showPreview = false;

    public ResourceGenMultiblockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESOURCE_GEN_MULTIBLOCK_BE.get(), pPos, pBlockState);

        this.quickCheckTier1 = RecipeManager.createCheck(ResourceGenTier1Recipe.Type.INSTANCE);
        this.quickCheckTier2 = RecipeManager.createCheck(ResourceGenTier2Recipe.Type.INSTANCE);
        this.quickCheckTier3 = RecipeManager.createCheck(ResourceGenTier3Recipe.Type.INSTANCE);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ResourceGenMultiblockBlockEntity.this.progress;
                    case 1 -> ResourceGenMultiblockBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ResourceGenMultiblockBlockEntity.this.progress = pValue;
                    case 1 -> ResourceGenMultiblockBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private List<Integer> getParticipatingSlots() {
        List<Integer> slots = new ArrayList<>();
        ItemStack machineStack = this.machineHandler.getStackInSlot(0);
        if (machineStack.isEmpty()) return slots;

        ItemStack primaryCatalyst = ItemStack.EMPTY;

        // Loop through all 4 input slots
        for (int i = 0; i < this.inputHandler.getSlots(); i++) {
            ItemStack inputStack = this.inputHandler.getStackInSlot(i);
            if (inputStack.isEmpty()) continue;

            if (primaryCatalyst.isEmpty()) {
                SimpleContainer inventory = new SimpleContainer(2);
                inventory.setItem(0, inputStack);
                inventory.setItem(1, machineStack);

                if (checkRecipeCache(inventory, machineStack).isPresent()) {
                    primaryCatalyst = inputStack;
                    slots.add(i);
                }
            } else {
                if (inputStack.is(primaryCatalyst.getItem())) {
                    slots.add(i);
                }
            }
        }
        return slots;
    }

    private Optional<? extends Recipe<SimpleContainer>> checkRecipeCache(SimpleContainer inventory, ItemStack machineStack) {
        if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_1.get().asItem())) {
            return this.quickCheckTier1.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_2.get().asItem())) {
            return this.quickCheckTier2.getRecipeFor(inventory, this.level);
        } else if (machineStack.is(ModBlocks.RESOURCE_GEN_TIER_3.get().asItem())) {
            return this.quickCheckTier3.getRecipeFor(inventory, this.level);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyOptionalHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyOptionalHandler = LazyOptional.of(() -> outputHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyOptionalHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("machine", machineHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.put("upgrade", upgradeHandler.serializeNBT());
        pTag.putInt("progress", progress);
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
        progress = pTag.getInt("progress");
        this.showPreview = pTag.getBoolean("showPreview");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(7);
        inventory.setItem(0, inputHandler.getStackInSlot(0));
        inventory.setItem(1, inputHandler.getStackInSlot(1));
        inventory.setItem(2, inputHandler.getStackInSlot(2));
        inventory.setItem(3, inputHandler.getStackInSlot(3));
        inventory.setItem(4, machineHandler.getStackInSlot(0));
        inventory.setItem(5, upgradeHandler.getStackInSlot(0));
        inventory.setItem(6, outputHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pState.getValue(ResourceGenMultiblockBlock.FORMED)) {
            this.progress = 0;
            return;
        }

        if(hasRecipe()) {
            progress++;
            setChanged(pLevel, pPos, pState);

            if(progress >= maxProgress) {
                craftItem();
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }

    private void craftItem() {
        Optional<? extends Recipe<SimpleContainer>> recipe = getCurrentRecipe();

        List<Integer> participating = getParticipatingSlots();
        int multiplier = participating.size();

        if(recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

            int totalOutput = processAmount * multiplier;

            for (int slot : participating) {
                this.inputHandler.extractItem(slot, 1, true);
            }

            this.outputHandler.setStackInSlot(0, new ItemStack(result.getItem(),
                    this.outputHandler.getStackInSlot(0).getCount() + totalOutput));
        }
    }

    private boolean hasRecipe() {
        Optional<? extends Recipe<SimpleContainer>> recipe = getCurrentRecipe();

        if(recipe.isEmpty()) {
            return false;
        }

        List<Integer> participating = getParticipatingSlots();
        int multiplier = participating.size();

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        int totalOutput = processAmount * multiplier;

        // Ensure the output slot is either empty or holds the same item and won't exceed the max storage limit
        return this.outputHandler.getStackInSlot(0).isEmpty() ||
                (this.outputHandler.getStackInSlot(0).is(result.getItem()) &&
                        this.outputHandler.getStackInSlot(0).getCount() + totalOutput <= this.outputHandler.getSlotLimit(0));
    }

    private Optional<? extends Recipe<SimpleContainer>> getCurrentRecipe() {
        List<Integer> participating = getParticipatingSlots();
        if (participating.isEmpty()) return Optional.empty();

        ItemStack machineStack = this.machineHandler.getStackInSlot(0);
        ItemStack inputStack = this.inputHandler.getStackInSlot(participating.get(0));

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, inputStack);
        inventory.setItem(1, machineStack);

        return checkRecipeCache(inventory, machineStack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.resource_gen_multiblock");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ResourceGenMultiblockMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public boolean isShowPreview() {
        return this.showPreview;
    }

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