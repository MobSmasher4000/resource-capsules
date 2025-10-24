package org.mob.resource_capsules.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;

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

        customBlockStatesWithoutTopSlot(ModBlocks.TIER_9001.get());
        customBlockStatesWithoutTopSlot(ModBlocks.ENCAPSULATING_TRANSMUTATOR.get());
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

    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
