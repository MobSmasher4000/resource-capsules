package org.mob.resource_capsules.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static org.mob.resource_capsules.ResourceCapsules.resourceLocation;


public class ModTags {
    public static class Blocks{

        public static TagKey<Block> createTag(String name){
            return BlockTags.create(resourceLocation(name));
        }
    }

    public static class Items{
        public static final TagKey<Item> RESOURCE_GENERATOR = createTag("resource_gen");
        public static final TagKey<Item> FLUID_GENERATOR = createTag("fluid_gen");
        public static final TagKey<Item> FLUID_GENERATOR_UPGRADES = createTag("fluid_gen_upgrade");

        public static TagKey<Item> createTag(String name){
            return ItemTags.create(resourceLocation(name));
        }

    }

}
