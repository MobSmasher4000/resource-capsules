package org.mob.resource_capsules.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.custom.*;
import org.mob.resource_capsules.block.custom.hatch.FluidOutputHatchBlock;
import org.mob.resource_capsules.block.custom.hatch.ItemOutputHatchBlock;
import org.mob.resource_capsules.block.custom.resource_gen_tier.*;
import org.mob.resource_capsules.item.ModItems;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ResourceCapsules.MOD_ID);

//    Resource gen tiers
    public static final DeferredBlock<Block> RESOURCE_GEN_TIER_1 = registerBlock("resource_gen_tier_1",
            () -> new ResourceGenTier1Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> RESOURCE_GEN_TIER_2 = registerBlock("resource_gen_tier_2",
            () -> new ResourceGenTier2Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> RESOURCE_GEN_TIER_3 = registerBlock("resource_gen_tier_3",
            () -> new ResourceGenTier3Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> RESOURCE_GEN_MULTIBLOCK = registerBlock("resource_gen_multiblock",
            () -> new ResourceGenMultiblockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> FLUID_GEN = registerBlock("fluid_gen",
            () -> new FluidGenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> FLUID_GEN_MULTIBLOCK = registerBlock("fluid_gen_multiblock",
            () -> new FluidGenMultiblockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

//   Duper tier 9001
    public static final DeferredBlock<Block> TIER_9001 = registerBlock("tier_9001",
        () -> new Tier9001Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()){
            @Override
            public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
                components.add(Component.translatable("tooltip.resource_capsules.tier_9001"));
                super.appendHoverText(itemStack, tooltipContext, components, tooltipFlag);
            }
        });

//    Dimensional resource gen
    public static final DeferredBlock<Block> DIMENSIONAL_RESOURCE_GEN = registerBlock("dimensional_resource_gen",
        () -> new DimensionalResourceGenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

//    Encapsulating Transmutator
    public static final DeferredBlock<Block> ENCAPSULATING_TRANSMUTATOR = registerBlock("encapsulating_transmutator",
        () -> new EncapsulatingTransmutatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

//   Catalytic Converter
    public static final DeferredBlock<Block> CATALYTIC_CONVERTER = registerBlock("catalytic_converter",
        () -> new CatalyticConverterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

//    Bio resource gen
    public static final DeferredBlock<Block> BIO_RESOURCE_GEN = registerBlock("bio_resource_gen",
        () -> new BioResourceGenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> MACHINE_CASING = registerBlock("machine_casing",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ITEM_OUTPUT_HATCH = registerBlock("item_output_hatch",
            () -> new ItemOutputHatchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> FLUID_OUTPUT_HATCH = registerBlock("fluid_output_hatch",
            () -> new FluidOutputHatchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
