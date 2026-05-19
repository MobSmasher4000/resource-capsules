package org.mob.resource_capsules.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.inventory.SlotFilteredItemHandler;
import org.mob.resource_capsules.recipe.CatalyticConverterRecipe;
import org.mob.resource_capsules.screen.menu.CatalyticConverterMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CatalyticConverterBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int[] INPUT_SLOTS = {0,1,2,3,4,5,6,7,8};
    private static final int OUTPUT_SLOT = 9;

    private final IItemHandler ioHandler =new SlotFilteredItemHandler(itemHandler,List.of(0,1,2,3,4,5,6,7,8),List.of(9));

    private final RecipeManager.CachedCheck<CatalyticConverterRecipe.CatalyticRecipeInput, CatalyticConverterRecipe> quickCheck;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public CatalyticConverterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CATALYTIC_CONVERTER_BE.get(), pPos, pBlockState);

        // Initialize the cache
        this.quickCheck = RecipeManager.createCheck(CatalyticConverterRecipe.Type.INSTANCE);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CatalyticConverterBlockEntity.this.progress;
                    case 1 -> CatalyticConverterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CatalyticConverterBlockEntity.this.progress = pValue;
                    case 1 -> CatalyticConverterBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        return ioHandler;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_capsules.catalytic_converter");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CatalyticConverterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("catalytic_converter.progress", progress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("catalytic_converter.progress");
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
        Optional<RecipeHolder<CatalyticConverterRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return;

        CatalyticConverterRecipe recipe = recipeOpt.get().value();
        ItemStack result = recipe.getResultItem(this.level.registryAccess());

        for (CatalyticConverterRecipe.SizedIngredient sizedIng : recipe.getSizedIngredients()) {
            int amountToExtract = sizedIng.count();

            for (int slot : INPUT_SLOTS) {
                if (amountToExtract <= 0) break;

                ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
                if (sizedIng.ingredient().test(stackInSlot)) {
                    int extractable = Math.min(amountToExtract, stackInSlot.getCount());
                    itemHandler.extractItem(slot, extractable, false);
                    amountToExtract -= extractable;
                }
            }
        }

        ItemStack outputSlotStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, result.copy());
        } else {
            outputSlotStack.grow(result.getCount());
            itemHandler.setStackInSlot(OUTPUT_SLOT, outputSlotStack);
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<CatalyticConverterRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isEmpty()) return false;

        ItemStack result = recipeOpt.get().value().getResultItem(level.registryAccess());
        return canInsertAmountIntoOutputSlot(result.getCount()) &&
                canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<RecipeHolder<CatalyticConverterRecipe>> getCurrentRecipe() {
        List<ItemStack> inputItems = new ArrayList<>();
        for (int slot : INPUT_SLOTS) {
            inputItems.add(itemHandler.getStackInSlot(slot));
        }

        CatalyticConverterRecipe.CatalyticRecipeInput input = new CatalyticConverterRecipe.CatalyticRecipeInput(inputItems);
        return this.quickCheck.getRecipeFor(input, this.level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
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
}