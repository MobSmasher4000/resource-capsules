package org.mob.resource_capsules.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.datagen.builder.*;
import org.mob.resource_capsules.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        resourceGenTier1Recipe(consumer, ModItems.TIER_1_MINI_CATALYST.get(), ModItems.TIER_1_MINI_CAPSULE.get());
        resourceGenTier1Recipe(consumer, ModItems.TIER_1_MEDIUM_CATALYST.get(), ModItems.TIER_1_MEDIUM_CAPSULE.get());
        resourceGenTier1Recipe(consumer, ModItems.TIER_1_LARGE_CATALYST.get(), ModItems.TIER_1_LARGE_CAPSULE.get());

        resourceGenTier2Recipe(consumer, ModItems.TIER_2_MINI_CATALYST.get(), ModItems.TIER_2_MINI_CAPSULE.get());
        resourceGenTier2Recipe(consumer, ModItems.TIER_2_MEDIUM_CATALYST.get(), ModItems.TIER_2_MEDIUM_CAPSULE.get());
        resourceGenTier2Recipe(consumer, ModItems.TIER_2_LARGE_CATALYST.get(), ModItems.TIER_2_LARGE_CAPSULE.get());

        resourceGenTier3Recipe(consumer, ModItems.TIER_3_MINI_CATALYST.get(), ModItems.TIER_3_MINI_CAPSULE.get());
        resourceGenTier3Recipe(consumer, ModItems.TIER_3_MEDIUM_CATALYST.get(), ModItems.TIER_3_MEDIUM_CAPSULE.get());
        resourceGenTier3Recipe(consumer, ModItems.TIER_3_LARGE_CATALYST.get(), ModItems.TIER_3_LARGE_CAPSULE.get());

        dimensionalResourceGenRecipe(consumer, ModItems.OVERWORLD_CATALYST.get(), ModItems.OVERWORLD_CAPSULE.get(), "minecraft:overworld");
        dimensionalResourceGenRecipe(consumer, ModItems.NETHER_CATALYST.get(), ModItems.NETHER_CAPSULE.get(), "minecraft:the_nether");
        dimensionalResourceGenRecipe(consumer, ModItems.NETHER_ADVANCED_CATALYST.get(), ModItems.NETHER_ADVANCED_CAPSULE.get(), "minecraft:the_nether");
        dimensionalResourceGenRecipe(consumer, ModItems.END_CATALYST.get(), ModItems.END_CAPSULE.get(), "minecraft:the_end");
        dimensionalResourceGenRecipe(consumer, ModItems.END_ADVANCED_CATALYST.get(), ModItems.END_ADVANCED_CAPSULE.get(), "minecraft:the_end");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.NETHER_ADVANCED_CATALYST.get())
                .requires(ModItems.NETHER_CATALYST.get())
                .requires(Items.WITHER_SKELETON_SKULL)
                .requires(Items.SOUL_SAND)
                .unlockedBy("has_nether_catalyst", has(ModItems.NETHER_CATALYST.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.END_ADVANCED_CATALYST.get())
                .requires(ModItems.END_CATALYST.get())
                .requires(Items.DRAGON_HEAD)
                .requires(Items.DRAGON_BREATH)
                .unlockedBy("has_end_catalyst", has(ModItems.END_CATALYST.get()))
                .save(consumer);


//      Encapsulating Transmutator
        EncapsulatingTransmutatorRecipeBuilder.encapsulatingTransmutatorRecipe()
                .addIngredient(Ingredient.of(Items.FEATHER), 64)
                .addIngredient(Ingredient.of(ModItems.BIO_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.BIO_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.BIO_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.BIO_CAPSULE.get()), 64)
                .addOutput(new ItemStack(Items.PHANTOM_MEMBRANE))
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(consumer);

        EncapsulatingTransmutatorRecipeBuilder.encapsulatingTransmutatorRecipe()
                .addIngredient(Ingredient.of(Items.WITHER_SKELETON_SKULL), 1)
                .addIngredient(Ingredient.of(ModItems.NETHER_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.NETHER_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.NETHER_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.NETHER_ADVANCED_CAPSULE.get()), 64)
                .addOutput(new ItemStack(Items.NETHER_STAR))
                .unlockedBy("has_wither_skull", has(Items.WITHER_SKELETON_SKULL))
                .save(consumer);

        EncapsulatingTransmutatorRecipeBuilder.encapsulatingTransmutatorRecipe()
                .addIngredient(Ingredient.of(Items.GLASS_BOTTLE), 1)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addOutput(new ItemStack(Items.DRAGON_BREATH))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(consumer);

        EncapsulatingTransmutatorRecipeBuilder.encapsulatingTransmutatorRecipe()
                .addIngredient(Ingredient.of(Items.CHORUS_FLOWER), 1)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addIngredient(Ingredient.of(ModItems.END_ADVANCED_CAPSULE.get()), 64)
                .addOutput(new ItemStack(Items.DRAGON_HEAD))
                .unlockedBy("has_chorus_flower", has(Items.CHORUS_FLOWER))
                .save(consumer);

//      Catalytic Converter
        CatalyticConverterRecipeBuilder.catalyticConverterRecipe()
                .addIngredient(Ingredient.of(ModItems.TIER_1_MINI_CATALYST.get()), 1)
                .addIngredient(Ingredient.of(Items.COPPER_INGOT), 64)
                .addIngredient(Ingredient.of(Items.IRON_INGOT), 64)
                .addIngredient(Ingredient.of(Items.BONE_MEAL), 64)
                .addIngredient(Ingredient.of(Items.COAL), 64)
                .addIngredient(Ingredient.of(Items.REDSTONE), 64)
                .addOutput(new ItemStack(ModItems.TIER_1_MEDIUM_CATALYST.get()))
                .unlockedBy("has_mini_catalyst", has(ModItems.TIER_1_MINI_CATALYST.get()))
                .save(consumer);

        CatalyticConverterRecipeBuilder.catalyticConverterRecipe()
                .addIngredient(Ingredient.of(ModItems.TIER_2_MINI_CATALYST.get()), 1)
                .addIngredient(Ingredient.of(Items.LAPIS_LAZULI), 64)
                .addIngredient(Ingredient.of(Items.GOLD_INGOT), 64)
                .addIngredient(Ingredient.of(Items.DIAMOND), 64)
                .addOutput(new ItemStack(ModItems.TIER_2_MEDIUM_CATALYST.get()))
                .unlockedBy("has_mini_catalyst", has(ModItems.TIER_2_MINI_CATALYST.get()))
                .save(consumer);

        CatalyticConverterRecipeBuilder.catalyticConverterRecipe()
                .addIngredient(Ingredient.of(ModItems.TIER_3_MINI_CATALYST.get()), 1)
                .addIngredient(Ingredient.of(Items.EMERALD), 64)
                .addOutput(new ItemStack(ModItems.TIER_3_MEDIUM_CATALYST.get()))
                .unlockedBy("has_mini_catalyst", has(ModItems.TIER_3_MINI_CATALYST.get()))
                .save(consumer);

        CatalyticConverterRecipeBuilder.catalyticConverterRecipe()
                .addIngredient(Ingredient.of(ModItems.TIER_1_MEDIUM_CATALYST.get()), 1)
                .addIngredient(Ingredient.of(Items.COPPER_BLOCK), 64)
                .addIngredient(Ingredient.of(Items.IRON_BLOCK), 64)
                .addIngredient(Ingredient.of(Items.BONE_BLOCK), 64)
                .addIngredient(Ingredient.of(Items.COAL_BLOCK), 64)
                .addIngredient(Ingredient.of(Items.REDSTONE_BLOCK), 64)
                .addOutput(new ItemStack(ModItems.TIER_1_LARGE_CATALYST.get()))
                .unlockedBy("has_medium_catalyst", has(ModItems.TIER_1_MEDIUM_CATALYST.get()))
                .save(consumer);

        CatalyticConverterRecipeBuilder.catalyticConverterRecipe()
                .addIngredient(Ingredient.of(ModItems.TIER_2_MEDIUM_CATALYST.get()), 1)
                .addIngredient(Ingredient.of(Items.LAPIS_BLOCK), 64)
                .addIngredient(Ingredient.of(Items.DIAMOND_BLOCK), 64)
                .addIngredient(Ingredient.of(Items.GOLD_BLOCK), 64)
                .addOutput(new ItemStack(ModItems.TIER_2_LARGE_CATALYST.get()))
                .unlockedBy("has_medium_catalyst", has(ModItems.TIER_2_MEDIUM_CATALYST.get()))
                .save(consumer);

        CatalyticConverterRecipeBuilder.catalyticConverterRecipe()
                .addIngredient(Ingredient.of(ModItems.TIER_3_MEDIUM_CATALYST.get()), 1)
                .addIngredient(Ingredient.of(Items.EMERALD_BLOCK), 64)
                .addOutput(new ItemStack(ModItems.TIER_3_LARGE_CATALYST.get()))
                .unlockedBy("has_medium_catalyst", has(ModItems.TIER_3_MEDIUM_CATALYST.get()))
                .save(consumer);


//        Resource gen crafting recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RESOURCE_GEN_TIER_1.get())
                .pattern("CCC")
                .pattern("DDD")
                .pattern("CCC")
                .define('C', Items.COBBLESTONE)
                .define('D', Items.DIRT)
                .unlockedBy("has_cobble", has(Items.COBBLESTONE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RESOURCE_GEN_TIER_2.get())
                .pattern("ABC")
                .pattern("DED")
                .pattern("FGH")
                .define('A', Items.IRON_BLOCK)
                .define('B', Items.COPPER_BLOCK)
                .define('C', Items.COAL_BLOCK)
                .define('D', Items.SAND)
                .define('E', ModBlocks.RESOURCE_GEN_TIER_1.get())
                .define('F', Items.IRON_BLOCK)
                .define('G', Items.BONE_BLOCK)
                .define('H', Items.REDSTONE_BLOCK)
                .unlockedBy("has_resource_gen_tier_1", has(ModBlocks.RESOURCE_GEN_TIER_1.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RESOURCE_GEN_TIER_3.get())
                .pattern("ABC")
                .pattern("DED")
                .pattern("FGH")
                .define('A', Items.BLAZE_ROD)
                .define('B', Items.GOLD_BLOCK)
                .define('C', Items.GOLD_BLOCK)
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', ModBlocks.RESOURCE_GEN_TIER_2.get())
                .define('F', Items.QUARTZ_BLOCK)
                .define('G', Items.GLOWSTONE)
                .define('H', Items.IRON_BLOCK)
                .unlockedBy("has_resource_gen_tier_2", has(ModBlocks.RESOURCE_GEN_TIER_2.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DIMENSIONAL_RESOURCE_GEN.get())
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Items.CRYING_OBSIDIAN)
                .define('B', Items.OBSIDIAN)
                .define('C', ModBlocks.RESOURCE_GEN_TIER_2.get())
                .unlockedBy("has_resource_gen_tier_2", has(ModBlocks.RESOURCE_GEN_TIER_2.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BIO_RESOURCE_GEN.get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', Items.OAK_LOG)
                .define('B', Items.WATER_BUCKET)
                .unlockedBy("has_log", has(Items.OAK_LOG))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CATALYTIC_CONVERTER.get())
                .pattern("ABC")
                .pattern("DIF")
                .pattern("GHE")
                .define('A', ModBlocks.BIO_RESOURCE_GEN.get()) // bio generator
                .define('B', ModItems.TIER_1_MINI_CATALYST.get())
                .define('C', ModBlocks.RESOURCE_GEN_TIER_1.get())
                .define('D', ModItems.TIER_2_MINI_CATALYST.get())
                .define('E', ModBlocks.RESOURCE_GEN_TIER_2.get())
                .define('F', ModItems.TIER_3_MINI_CATALYST.get())
                .define('G', ModBlocks.RESOURCE_GEN_TIER_3.get())
                .define('H', ModItems.BIO_CATALYST.get())
                .define('I', ModBlocks.DIMENSIONAL_RESOURCE_GEN.get())
                .unlockedBy("has_beacon", has(Items.BEACON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ENCAPSULATING_TRANSMUTATOR.get())
                .pattern("DNZ")
                .pattern("EXB")
                .pattern("YBV")
                .define('Y', ModItems.END_CATALYST.get())
                .define('E', ModItems.END_ADVANCED_CAPSULE.get())
                .define('X', ModItems.BIO_CATALYST.get())
                .define('B', ModItems.BIO_CAPSULE.get())
                .define('Z', ModItems.NETHER_CATALYST.get())
                .define('N', ModItems.NETHER_ADVANCED_CAPSULE.get())
                .define('D', ModBlocks.DIMENSIONAL_RESOURCE_GEN.get())
                .define('V', Items.EMERALD_BLOCK)
                .unlockedBy("has_dimensional_resource_gen", has(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get()))
                .save(consumer);

        // Bio Catalyst
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIO_CATALYST.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("ABA")
                .define('A', Items.OAK_LOG)
                .define('B', Items.OAK_SAPLING)
                .define('C', Items.BONE_MEAL)
                .define('D', ModItems.TIER_1_MINI_CATALYST.get())
                .unlockedBy("has_log", has(Items.OAK_LOG))
                .save(consumer);

        // Nether Catalyst
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NETHER_CATALYST.get())
                .pattern("ABA")
                .pattern("BDB")
                .pattern("ABA")
                .define('A', Items.NETHER_BRICK)
                .define('B', Items.SOUL_SAND)
                .define('D', ModItems.TIER_1_MINI_CATALYST.get())
                .unlockedBy("has_netherrack", has(Items.NETHERRACK))
                .save(consumer);

        // End Catalyst
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.END_CATALYST.get())
                .pattern("ABA")
                .pattern("BDB")
                .pattern("ABA")
                .define('A', Items.PURPUR_BLOCK)
                .define('B', Items.END_STONE)
                .define('D', ModItems.TIER_1_MINI_CATALYST.get())
                .unlockedBy("has_end_stone", has(Items.END_STONE))
                .save(consumer);

        // Overworld Catalyst
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.OVERWORLD_CATALYST.get())
                .pattern("ABA")
                .pattern("BDB")
                .pattern("ABA")
                .define('A', Items.ROTTEN_FLESH)
                .define('B', Items.BONE)
                .define('D', ModItems.TIER_1_MINI_CATALYST.get())
                .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
                .save(consumer);

        SimpleCookingRecipeBuilder.campfireCooking(
                        Ingredient.of(Items.CLAY_BALL),
                        RecipeCategory.MISC,
                        ModItems.TIER_1_MINI_CATALYST.get(),
                        0.35f,
                        60
                )
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "tier_1_catalyst_mini"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TIER_2_MINI_CATALYST.get())
                .pattern("DBD")
                .pattern("AEA")
                .pattern("CBC")
                .define('D', Items.IRON_BLOCK)
                .define('C', Items.COPPER_BLOCK)
                .define('A', Items.COAL_BLOCK)
                .define('B', Items.REDSTONE_BLOCK)
                .define('E', ModItems.TIER_1_MINI_CATALYST.get())
                .unlockedBy("has_tier_1_mini_catalyst", has(ModItems.TIER_1_MINI_CATALYST.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TIER_3_MINI_CATALYST.get())
                .pattern("AAA")
                .pattern("BCB")
                .pattern("DDD")
                .define('D', Items.DIAMOND_BLOCK)
                .define('C', ModItems.TIER_2_MINI_CATALYST.get())
                .define('A', Items.GOLD_BLOCK)
                .define('B', Items.BLAZE_ROD)
                .unlockedBy("has_tier_2_mini_catalyst", has(ModItems.TIER_2_MINI_CATALYST.get()))
                .save(consumer);

//        Bio Capsule Recipes
        // Cactus
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.CACTUS)
                .pattern("DDD")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "cactus_from_bio_capsule"));

        // Bamboo
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BAMBOO)
                .pattern("DD ")
                .pattern("  D")
                .pattern("   ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "bamboo_from_bio_capsule"));

        // Wheat
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WHEAT)
                .pattern("D  ")
                .pattern(" D ")
                .pattern("  D")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "wheat_from_bio_capsule"));

        // Brown Mushroom
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BROWN_MUSHROOM)
                .pattern("D  ")
                .pattern(" D ")
                .pattern(" D ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "brown_mushroom_from_bio_capsule"));

        // Red Mushroom
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.RED_MUSHROOM)
                .pattern("  D")
                .pattern(" D ")
                .pattern(" D ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "red_mushroom_from_bio_capsule"));

        // Sugar Cane
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SUGAR_CANE)
                .pattern("D D")
                .pattern(" D ")
                .pattern("D D")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "sugar_cane_from_bio_capsule"));

        // Potato
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.POTATO)
                .pattern("  D")
                .pattern(" D ")
                .pattern("   ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "potato_from_bio_capsule"));

        // Carrot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.CARROT)
                .pattern("D  ")
                .pattern(" D ")
                .pattern("   ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "carrot_from_bio_capsule"));

        // Melon
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.MELON_SLICE)
                .pattern("DD ")
                .pattern("D D")
                .pattern("DD ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "melon_from_bio_capsule"));

        // Pumpkin
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PUMPKIN)
                .pattern(" DD")
                .pattern("D D")
                .pattern(" DD")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "pumpkin_from_bio_capsule"));

        // Dandelion
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DANDELION)
                .pattern("D  ")
                .pattern(" D ")
                .pattern(" E ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .define('E', Items.WHITE_DYE)
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "dandelion_from_bio_capsule"));

        // Poppy
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.POPPY)
                .pattern(" E ")
                .pattern(" D ")
                .pattern("D  ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .define('E', Items.WHITE_DYE)
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, new ResourceLocation(ResourceCapsules.MOD_ID, "poppy_from_bio_capsule"));

        // Bio Capsule
        BioResourceGenRecipeBuilder.bioResourceGenRecipe()
                .addIngredient(Ingredient.of(ModItems.BIO_CATALYST.get()))
                .addFluidInput(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation("minecraft:water")), 1000)) // 1000mb = 1 bucket
                .addOutput(ModItems.BIO_CAPSULE.get().getDefaultInstance())
                .unlockedBy("has_bio_catalyst", has(ModItems.BIO_CATALYST.get()))
                .save(consumer);
    }

    private void resourceGenTier1Recipe(Consumer<FinishedRecipe> consumer, Item input, Item output) {
        ResourceGenTier1RecipeBuilder.resourceGenTier1Recipe()
                .addIngredient(Ingredient.of(input))
                .addOutput(new ItemStack(output))
                .unlockedBy("has_" + input.toString().replace("minecraft:", ""), has(input))
                .save(consumer);
    }
    private void resourceGenTier2Recipe(Consumer<FinishedRecipe> consumer, Item input, Item output) {
        ResourceGenTier2RecipeBuilder.resourceGenTier2Recipe()
                .addIngredient(Ingredient.of(input))
                .addOutput(new ItemStack(output))
                .unlockedBy("has_" + input.toString().replace("minecraft:", ""), has(input))
                .save(consumer);
    }

    private void resourceGenTier3Recipe(Consumer<FinishedRecipe> consumer, Item input, Item output) {
        ResourceGenTier3RecipeBuilder.resourceGenTier3Recipe()
                .addIngredient(Ingredient.of(input))
                .addOutput(new ItemStack(output))
                .unlockedBy("has_" + input.toString().replace("minecraft:", ""), has(input))
                .save(consumer);
    }

    private void dimensionalResourceGenRecipe(Consumer<FinishedRecipe> consumer, Item input, Item output, String dimension) {
        DimensionalResourceGenRecipeBuilder.dimensionalResourceGenRecipe()
                .addIngredient(Ingredient.of(input))
                .addOutput(new ItemStack(output))
                .dimension(dimension)
                .unlockedBy("has_" + input.toString().replace("minecraft:", ""), has(input))
                .save(consumer);
    }
}
