package org.mob.resource_capsules.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ResourceCapsules.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RESOURCE_GEN_TIER_1.get())
                .add(ModBlocks.RESOURCE_GEN_TIER_2.get())
                .add(ModBlocks.RESOURCE_GEN_TIER_3.get())
                .add(ModBlocks.RESOURCE_GEN_TIER_4.get())
                .add(ModBlocks.RESOURCE_GEN_TIER_5.get())
                .add(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get())
                .add(ModBlocks.TIER_9001.get())
                .add(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get())
                .add(ModBlocks.ENCAPSULATING_TRANSMUTATOR.get())
                .add(ModBlocks.CATALYTIC_CONVERTER.get())
                .add(ModBlocks.BIO_RESOURCE_GEN.get())
                .add(ModBlocks.MACHINE_CASING.get())
                .add(ModBlocks.FLUID_GEN.get())
                .add(ModBlocks.ITEM_OUTPUT_HATCH.get())
                .add(ModBlocks.FLUID_OUTPUT_HATCH.get())
                .add(ModBlocks.FLUID_GEN_MULTIBLOCK.get())
        ;
    }
}
