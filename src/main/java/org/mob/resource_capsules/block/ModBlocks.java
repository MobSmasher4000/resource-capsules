package org.mob.resource_capsules.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.custom.ResourceGenTier1Block;
import org.mob.resource_capsules.block.custom.ResourceGenTier2Block;
import org.mob.resource_capsules.block.custom.ResourceGenTier3Block;
import org.mob.resource_capsules.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ResourceCapsules.MOD_ID);

//    Resource gen tiers
    public static final RegistryObject<Block> RESOURCE_GEN_TIER_1 = registerBlock("resource_gen_tier_1",
            () -> new ResourceGenTier1Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RESOURCE_GEN_TIER_2 = registerBlock("resource_gen_tier_2",
            () -> new ResourceGenTier2Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RESOURCE_GEN_TIER_3 = registerBlock("resource_gen_tier_3",
            () -> new ResourceGenTier3Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
