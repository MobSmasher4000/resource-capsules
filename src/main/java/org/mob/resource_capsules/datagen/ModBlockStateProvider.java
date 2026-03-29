package org.mob.resource_capsules.datagen;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.custom.ResourceGenMultiblockBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ResourceCapsules.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        customBlockStatesWithTopSlot(ModBlocks.RESOURCE_GEN_TIER_1.get());
        customBlockStatesWithTopSlot(ModBlocks.RESOURCE_GEN_TIER_2.get());
        customBlockStatesWithTopSlot(ModBlocks.RESOURCE_GEN_TIER_3.get());
        customBlockStatesWithTopSlot(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get());
        customBlockStatesWithTopSlot(ModBlocks.BIO_RESOURCE_GEN.get());

        customBlockStatesWithoutTopSlot(ModBlocks.TIER_9001.get());
        customBlockStatesWithoutTopSlot(ModBlocks.ENCAPSULATING_TRANSMUTATOR.get());
        customBlockStatesWithoutTopSlot(ModBlocks.CATALYTIC_CONVERTER.get());

        machineCasingTexture(ModBlocks.MACHINE_CASING.get());
        resourceGenMultiblockTexture(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get());
    }

    private void customBlockStatesWithTopSlot(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        // Block-specific front texture
        ResourceLocation frontTex = modLoc("block/" + name);

        // Shared textures for other faces
        ResourceLocation sideTex = modLoc("block/side");
        ResourceLocation topTex = modLoc("block/top_with_slot");
        ResourceLocation bottomTex = modLoc("block/bottom");

        ModelFile model = models().cube(name,
                bottomTex, // down
                topTex,    // up
                frontTex,  // north (front)
                sideTex,   // south
                sideTex,   // east
                sideTex    // west
        ).texture("particle",sideTex);

        horizontalBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void customBlockStatesWithoutTopSlot(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        // Block-specific front texture
        ResourceLocation frontTex = modLoc("block/" + name);

        // Shared textures for other faces
        ResourceLocation sideTex = modLoc("block/side");
        ResourceLocation topTex = modLoc("block/top");
        ResourceLocation bottomTex = modLoc("block/bottom");

        ModelFile model = models().cube(name,
                bottomTex, // down
                topTex,    // up
                frontTex,  // north (front)
                sideTex,   // south
                sideTex,   // east
                sideTex    // west
        ).texture("particle",sideTex);

        horizontalBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void machineCasingTexture(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ResourceLocation sideTex = modLoc("block/side");
        ResourceLocation topTex = modLoc("block/top");
        ResourceLocation bottomTex = modLoc("block/bottom");

        ModelFile model = models().cube(name,
                bottomTex, // down
                topTex,    // up
                sideTex,  // north (front)
                sideTex,   // south
                sideTex,   // east
                sideTex    // west
        ).texture("particle",sideTex);

        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void resourceGenMultiblockTexture(Block block) {
        // Gets the exact registry name (e.g., "resource_gen_tier_3")
        String name = ForgeRegistries.BLOCKS.getKey(block).getPath();

        // 1. Shared textures for the casing
        ResourceLocation sideTex = modLoc("block/side");
        ResourceLocation topTex = modLoc("block/top");
        ResourceLocation bottomTex = modLoc("block/bottom");

        // 2. The two front textures (OFF and ON)
        ResourceLocation frontOffTex = modLoc("block/" + name);
        ResourceLocation frontOnTex = modLoc("block/" + name + "_formed");

        // 3. Generate the "OFF" Model
        ModelFile modelOff = models().cube(name,
                bottomTex,   // down
                topTex,      // up
                frontOffTex, // north (front)
                sideTex,     // south
                sideTex,     // east
                sideTex      // west
        ).texture("particle", sideTex);

        // 4. Generate the "ON" Model
        ModelFile modelOn = models().cube(name + "_on",
                bottomTex,   // down
                topTex,      // up
                frontOnTex,  // north (front)
                sideTex,     // south
                sideTex,     // east
                sideTex      // west
        ).texture("particle", sideTex);

        // 5. Build the BlockStates (Handles Rotation AND Formed State)
        getVariantBuilder(block).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean isFormed = state.getValue(ResourceGenMultiblockBlock.FORMED);

            // Pick the correct model based on whether the machine is running
            ModelFile currentModel = isFormed ? modelOn : modelOff;

            // Rotate the model based on the direction it's facing
            int yRot = switch (dir) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0; // NORTH
            };

            return ConfiguredModel.builder()
                    .modelFile(currentModel)
                    .rotationY(yRot)
                    .build();
        });

        // 6. Generate the Item Model (Uses the OFF state so it looks unformed in your hand)
        simpleBlockItem(block, modelOff);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
