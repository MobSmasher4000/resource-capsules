package org.mob.resource_capsules.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.block.ModBlocks;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.RESOURCE_GEN_TIER_1.get());
        dropSelf(ModBlocks.RESOURCE_GEN_TIER_2.get());
        dropSelf(ModBlocks.RESOURCE_GEN_TIER_3.get());
        dropSelf(ModBlocks.TIER_9001.get());
        dropSelf(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
