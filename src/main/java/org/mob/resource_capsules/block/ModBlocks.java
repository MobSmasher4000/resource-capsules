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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.custom.*;
import org.mob.resource_capsules.block.custom.resource_gen_tier.ResourceGenTier1Block;
import org.mob.resource_capsules.block.custom.resource_gen_tier.ResourceGenTier2Block;
import org.mob.resource_capsules.block.custom.resource_gen_tier.ResourceGenTier3Block;
import org.mob.resource_capsules.item.ModItems;

import java.util.List;
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

//   Duper tier 9001
    public static final RegistryObject<Block> TIER_9001 = registerBlock("tier_9001",
        () -> new Tier9001Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()){
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
                pTooltip.add(Component.translatable("tooltip.resource_capsules.tier_9001"));
                super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
            }
        });

//    Dimensional resource gen
    public static final RegistryObject<Block> DIMENSIONAL_RESOURCE_GEN = registerBlock("dimensional_resource_gen",
        () -> new DimensionalResourceGenBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

//    Encapsulating Transmutator
    public static final RegistryObject<Block> ENCAPSULATING_TRANSMUTATOR = registerBlock("encapsulating_transmutator",
        () -> new EncapsulatingTransmutatorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

//   Catalytic Converter
    public static final RegistryObject<Block> CATALYTIC_CONVERTER = registerBlock("catalytic_converter",
        () -> new CatalyticConverterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()){
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
                pTooltip.add(Component.translatable("tooltip.resource_capsules.catalytic_converter"));
                super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
            }
        });

//    Bio resource gen
    public static final RegistryObject<Block> BIO_RESOURCE_GEN = registerBlock("bio_resource_gen",
        () -> new BioResourceGenBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().requiresCorrectToolForDrops()));

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
