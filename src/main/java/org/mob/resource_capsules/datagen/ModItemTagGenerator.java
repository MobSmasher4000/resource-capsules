package org.mob.resource_capsules.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import org.mob.mob_lib.item.ModItems;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, ResourceCapsules.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.RESOURCE_GENERATOR)
                .add(ModBlocks.RESOURCE_GEN_TIER_1.get().asItem())
                .add(ModBlocks.RESOURCE_GEN_TIER_2.get().asItem())
                .add(ModBlocks.RESOURCE_GEN_TIER_3.get().asItem())
        ;

        tag(ModTags.Items.FLUID_GENERATOR)
                .add(ModBlocks.FLUID_GEN.get().asItem())
        ;

        tag(ModTags.Items.FLUID_GENERATOR_UPGRADES)
                .add(ModItems.UPGRADE_SPEED_TIER_1.get())
                .add(ModItems.UPGRADE_SPEED_TIER_2.get())
                .add(ModItems.UPGRADE_SPEED_TIER_3.get())
                .add(ModItems.UPGRADE_SPEED_TIER_4.get())
                .add(ModItems.UPGRADE_TIER_1.get())
                .add(ModItems.UPGRADE_TIER_2.get())
                .add(ModItems.UPGRADE_TIER_3.get())
                .add(ModItems.UPGRADE_AMOUNT_TIER_1.get())
                .add(ModItems.UPGRADE_AMOUNT_TIER_2.get())
                .add(ModItems.UPGRADE_AMOUNT_TIER_3.get())
        ;
    }
}
