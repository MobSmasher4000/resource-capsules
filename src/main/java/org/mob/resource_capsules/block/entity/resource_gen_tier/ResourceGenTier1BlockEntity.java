package org.mob.resource_capsules.block.entity.resource_gen_tier;

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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.component.ModDataComponents;
import org.mob.mob_lib.item.custom.UpgradeItem;
import org.mob.mob_lib.util.ModTags;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.recipe.ResourceGenTier1Recipe;
import org.mob.resource_capsules.screen.menu.ResourceGenTier1Menu;

import java.util.Optional;

public class ResourceGenTier1BlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public final ItemStackHandler outputhandler = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
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

    private final RecipeManager.CachedCheck<SingleRecipeInput, ResourceGenTier1Recipe> quickCheck;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;


    public ResourceGenTier1BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESOURCE_GEN_TIER_1_BE.get(), pPos, pBlockState);

        this.quickCheck = RecipeManager.createCheck(ResourceGenTier1Recipe.Type.INSTANCE);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ResourceGenTier1BlockEntity.this.progress;
                    case 1 -> ResourceGenTier1BlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ResourceGenTier1BlockEntity.this.progress = pValue;
                    case 1 -> ResourceGenTier1BlockEntity.this.maxProgress = pValue;
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
        maxProgress = upgradeStack.get(ModDataComponents.SPEED);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        upgrade();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(3);
        inventory.setItem(0, itemHandler.getStackInSlot(0));
        inventory.setItem(1, outputhandler.getStackInSlot(0));
        inventory.setItem(2, upgradeHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.resource_gen_tier_1");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ResourceGenTier1Menu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.put("output", outputhandler.serializeNBT(pRegistries));
        pTag.put("upgrade", upgradeHandler.serializeNBT(pRegistries));
        pTag.putInt("resource_gen_tier_1.progress", progress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        outputhandler.deserializeNBT(pRegistries, pTag.getCompound("output"));
        upgradeHandler.deserializeNBT(pRegistries, pTag.getCompound("upgrade"));
        progress = pTag.getInt("resource_gen_tier_1.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
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
        Optional<RecipeHolder<ResourceGenTier1Recipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().value().getResultItem(this.level.registryAccess());

            this.itemHandler.extractItem(0, 1, true);

            this.outputhandler.setStackInSlot(0, new ItemStack(result.getItem(),
                    this.outputhandler.getStackInSlot(0).getCount() + result.getCount()));
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<ResourceGenTier1Recipe>> recipe = getCurrentRecipe();

        if(recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<RecipeHolder<ResourceGenTier1Recipe>> getCurrentRecipe() {
        ItemStack inputStack = this.itemHandler.getStackInSlot(0);
        if (inputStack.isEmpty()) return Optional.empty();

        return this.quickCheck.getRecipeFor(new SingleRecipeInput(inputStack), this.level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.outputhandler.getStackInSlot(0).isEmpty() || this.outputhandler.getStackInSlot(0).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.outputhandler.getStackInSlot(0).getCount() + count <= this.outputhandler.getStackInSlot(0).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
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

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        return outputhandler;
    }
}