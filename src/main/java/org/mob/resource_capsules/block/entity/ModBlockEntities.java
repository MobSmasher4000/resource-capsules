package org.mob.resource_capsules.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.entity.hatch.FluidOutputHatchBlockEntity;
import org.mob.resource_capsules.block.entity.hatch.ItemOutputHatchBlockEntity;
import org.mob.resource_capsules.block.entity.resource_gen_tier.ResourceGenTier1BlockEntity;
import org.mob.resource_capsules.block.entity.resource_gen_tier.ResourceGenTier2BlockEntity;
import org.mob.resource_capsules.block.entity.resource_gen_tier.ResourceGenTier3BlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ResourceCapsules.MOD_ID);

    public static final RegistryObject<BlockEntityType<ResourceGenTier1BlockEntity>> RESOURCE_GEN_TIER_1_BE =
            BLOCK_ENTITIES.register("resource_gen_tier_1_be", () ->
                    BlockEntityType.Builder.of(ResourceGenTier1BlockEntity::new,
                            ModBlocks.RESOURCE_GEN_TIER_1.get()).build(null));

    public static final RegistryObject<BlockEntityType<ResourceGenTier2BlockEntity>> RESOURCE_GEN_TIER_2_BE =
            BLOCK_ENTITIES.register("resource_gen_tier_2_be", () ->
                    BlockEntityType.Builder.of(ResourceGenTier2BlockEntity::new,
                            ModBlocks.RESOURCE_GEN_TIER_2.get()).build(null));

    public static final RegistryObject<BlockEntityType<ResourceGenTier3BlockEntity>> RESOURCE_GEN_TIER_3_BE =
            BLOCK_ENTITIES.register("resource_gen_tier_3_be", () ->
                    BlockEntityType.Builder.of(ResourceGenTier3BlockEntity::new,
                            ModBlocks.RESOURCE_GEN_TIER_3.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidGenBlockEntity>> FLUID_GEN_BE =
            BLOCK_ENTITIES.register("fluid_gen_be", () ->
                    BlockEntityType.Builder.of(FluidGenBlockEntity::new,
                            ModBlocks.FLUID_GEN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ItemOutputHatchBlockEntity>> ITEM_OUTPUT_HATCH_BE =
            BLOCK_ENTITIES.register("item_output_hatch_be", () ->
                    BlockEntityType.Builder.of(ItemOutputHatchBlockEntity::new,
                            ModBlocks.ITEM_OUTPUT_HATCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidOutputHatchBlockEntity>> FLUID_OUTPUT_HATCH_BE =
            BLOCK_ENTITIES.register("fluid_output_hatch_be", () ->
                    BlockEntityType.Builder.of(FluidOutputHatchBlockEntity::new,
                            ModBlocks.FLUID_OUTPUT_HATCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<Tier9001BlockEntity>> TIER_9001_BE =
            BLOCK_ENTITIES.register("tier_9001_be", () ->
                    BlockEntityType.Builder.of(Tier9001BlockEntity::new,
                            ModBlocks.TIER_9001.get()).build(null));

    public static final RegistryObject<BlockEntityType<DimensionalResourceGenBlockEntity>> DIMENSIONAL_RESOURCE_GEN_BE =
            BLOCK_ENTITIES.register("dimensional_resource_gen_be", () ->
                    BlockEntityType.Builder.of(DimensionalResourceGenBlockEntity::new,
                            ModBlocks.DIMENSIONAL_RESOURCE_GEN.get()).build(null));

    public static final RegistryObject<BlockEntityType<EncapsulatingTransmutatorBlockEntity>> ENCAPSULATING_TRANSMUTATOR_BE =
            BLOCK_ENTITIES.register("encapsulating_transmutator_be", () ->
                    BlockEntityType.Builder.of(EncapsulatingTransmutatorBlockEntity::new,
                            ModBlocks.ENCAPSULATING_TRANSMUTATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CatalyticConverterBlockEntity>> CATALYTIC_CONVERTER_BE =
            BLOCK_ENTITIES.register("catalytic_converter_be", () ->
                    BlockEntityType.Builder.of(CatalyticConverterBlockEntity::new,
                            ModBlocks.CATALYTIC_CONVERTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BioResourceGenBlockEntity>> BIO_RESOURCE_GEN_BE =
            BLOCK_ENTITIES.register("bio_resource_gen_be", () ->
                    BlockEntityType.Builder.of(BioResourceGenBlockEntity::new,
                            ModBlocks.BIO_RESOURCE_GEN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ResourceGenMultiblockBlockEntity>> RESOURCE_GEN_MULTIBLOCK_BE =
            BLOCK_ENTITIES.register("resource_gen_multiblock_be", () ->
                    BlockEntityType.Builder.of(ResourceGenMultiblockBlockEntity::new,
                            ModBlocks.RESOURCE_GEN_MULTIBLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidGenMultiblockBlockEntity>> FLUID_GEN_MULTIBLOCK_BE =
            BLOCK_ENTITIES.register("fluid_gen_multiblock_be", () ->
                    BlockEntityType.Builder.of(FluidGenMultiblockBlockEntity::new,
                            ModBlocks.FLUID_GEN_MULTIBLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
