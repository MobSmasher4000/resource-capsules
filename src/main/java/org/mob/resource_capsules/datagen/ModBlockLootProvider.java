package org.mob.resource_capsules.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.mob.resource_capsules.block.ModBlocks;

import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {
    public ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.RESOURCE_GEN_TIER_1.get());
        dropSelf(ModBlocks.RESOURCE_GEN_TIER_2.get());
        dropSelf(ModBlocks.RESOURCE_GEN_TIER_3.get());
        dropSelf(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get());
        dropSelf(ModBlocks.TIER_9001.get());
        dropSelf(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get());
        dropSelf(ModBlocks.ENCAPSULATING_TRANSMUTATOR.get());
        dropSelf(ModBlocks.CATALYTIC_CONVERTER.get());
        dropSelf(ModBlocks.BIO_RESOURCE_GEN.get());
        dropSelf(ModBlocks.MACHINE_CASING.get());
        dropSelf(ModBlocks.FLUID_GEN.get());
        dropSelf(ModBlocks.ITEM_OUTPUT_HATCH.get());
        dropSelf(ModBlocks.FLUID_OUTPUT_HATCH.get());
        dropSelf(ModBlocks.FLUID_GEN_MULTIBLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
