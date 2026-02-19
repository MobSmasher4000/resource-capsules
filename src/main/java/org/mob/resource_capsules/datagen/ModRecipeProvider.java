package org.mob.resource_capsules.datagen;

import net.allthemods.alltheores.blocks.BlockList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
                .unlockedBy("has_dimensional", has(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get()))
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
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "tier_1_catalyst_mini"));

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
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "cactus_from_bio_capsule"));

        // Bamboo
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BAMBOO)
                .pattern("DD ")
                .pattern("  D")
                .pattern("   ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "bamboo_from_bio_capsule"));

        // Wheat
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WHEAT)
                .pattern("D  ")
                .pattern(" D ")
                .pattern("  D")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "wheat_from_bio_capsule"));

        // Brown Mushroom
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BROWN_MUSHROOM)
                .pattern("D  ")
                .pattern(" D ")
                .pattern(" D ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "brown_mushroom_from_bio_capsule"));

        // Red Mushroom
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.RED_MUSHROOM)
                .pattern("  D")
                .pattern(" D ")
                .pattern(" D ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "red_mushroom_from_bio_capsule"));

        // Sugar Cane
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SUGAR_CANE)
                .pattern("D D")
                .pattern(" D ")
                .pattern("D D")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "sugar_cane_from_bio_capsule"));

        // Potato
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.POTATO)
                .pattern("  D")
                .pattern(" D ")
                .pattern("   ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "potato_from_bio_capsule"));

        // Carrot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.CARROT)
                .pattern("D  ")
                .pattern(" D ")
                .pattern("   ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "carrot_from_bio_capsule"));

        // Melon
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.MELON_SLICE)
                .pattern("DD ")
                .pattern("D D")
                .pattern("DD ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "melon_from_bio_capsule"));

        // Pumpkin
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PUMPKIN)
                .pattern(" DD")
                .pattern("D D")
                .pattern(" DD")
                .define('D', ModItems.BIO_CAPSULE.get())
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "pumpkin_from_bio_capsule"));

        // Dandelion
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DANDELION)
                .pattern("D  ")
                .pattern(" D ")
                .pattern(" E ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .define('E', Items.WHITE_DYE)
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "dandelion_from_bio_capsule"));

        // Poppy
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.POPPY)
                .pattern(" E ")
                .pattern(" D ")
                .pattern("D  ")
                .define('D', ModItems.BIO_CAPSULE.get())
                .define('E', Items.WHITE_DYE)
                .unlockedBy("has_bio_capsule", has(ModItems.BIO_CAPSULE.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "poppy_from_bio_capsule"));

        // Bio Capsule
        BioResourceGenRecipeBuilder.bioResourceGenRecipe()
                .addIngredient(Ingredient.of(ModItems.BIO_CATALYST.get()))
                .addFluidInput(new FluidStack(ForgeRegistries.FLUIDS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft","water")), 1000)) // 1000mb = 1 bucket
                .addOutput(ModItems.BIO_CAPSULE.get().getDefaultInstance())
                .unlockedBy("has_bio_catalyst", has(ModItems.BIO_CATALYST.get()))
                .save(consumer);

        registerOverworldRecipes(consumer);
        registerNetherRecipes(consumer);
        registerEndRecipes(consumer);

        // Tier 1 capsule recipes
        Item[] capsules_tier1 = new Item[]{ModItems.TIER_1_MINI_CAPSULE.get(), ModItems.TIER_1_MEDIUM_CAPSULE.get(), ModItems.TIER_1_LARGE_CAPSULE.get()};

        // IRON
        registerTieredRecipe(consumer, "iron", capsules_tier1,
                new Item[]{Items.IRON_NUGGET, Items.IRON_INGOT, Items.IRON_BLOCK},
                new int[]{3, 1, 1}, new String[]{"CC ","  C","   "});

        // COPPER (Pattern: CCC)
        registerTieredRecipe(consumer, "copper", capsules_tier1,
                new Item[]{BlockList.COPPER_NUGGET.get(), Items.COPPER_INGOT, Items.COPPER_BLOCK},
                new int[]{3, 1, 1}, new String[]{"CCC","   ","   "});

        // Tin
        registerTieredRecipe(consumer, "tin", capsules_tier1,
                new Item[]{BlockList.TIN_NUGGET.get(), BlockList.TIN_INGOT.get(), BlockList.TIN_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"C  ","C  ","C  "});

        // REDSTONE
        registerTieredRecipe(consumer, "redstone", capsules_tier1,
                new Item[]{Items.REDSTONE, Items.REDSTONE, Items.REDSTONE_BLOCK},
                new int[]{3, 6, 1}, new String[]{"CCC", "C C", "CCC"});

        // COAL
        registerTieredRecipe(consumer, "coal", capsules_tier1,
                new Item[]{Items.COAL, Items.COAL, Items.COAL_BLOCK},
                new int[]{3, 6, 1}, new String[]{"C C", " C ", "C C"});

        // BONE MEAL
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 1).requires(capsules_tier1[0])
                .unlockedBy("has_item", has(capsules_tier1[0])).save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "bone_meal_from_mini_capsule"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 3).requires(capsules_tier1[1])
                .unlockedBy("has_item", has(capsules_tier1[1])).save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "bone_meal_from_medium_capsule"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_BLOCK, 1).requires(capsules_tier1[2])
                .unlockedBy("has_item", has(capsules_tier1[2])).save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "bone_block_from_large_capsule"));

        // Tier 2 capsule recipes
        Item[] capsules_tier2 = new Item[]{ModItems.TIER_2_MINI_CAPSULE.get(), ModItems.TIER_2_MEDIUM_CAPSULE.get(), ModItems.TIER_2_LARGE_CAPSULE.get()};

        // Gold
        registerTieredRecipe(consumer, "gold", capsules_tier2,
                new Item[]{Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.GOLD_BLOCK},
                new int[]{3, 1, 1}, new String[]{"CCC", "   ", "   "});

        // Diamond
        registerTieredRecipe(consumer, "diamond", capsules_tier2,
                new Item[]{Items.DIAMOND, Items.DIAMOND, Items.DIAMOND_BLOCK},
                new int[]{1, 3, 1}, new String[]{"CCC", "C C", "CCC"});

        // Silver
        registerTieredRecipe(consumer, "silver", capsules_tier2,
                new Item[]{BlockList.SILVER_NUGGET.get(), BlockList.SILVER_INGOT.get(), BlockList.SILVER_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"C  ", "C  ", "C  "});

        // Nickel
        registerTieredRecipe(consumer, "nickel", capsules_tier2,
                new Item[]{BlockList.NICKEL_NUGGET.get(), BlockList.NICKEL_INGOT.get(), BlockList.NICKEL_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"  C", "CC ", "   "});

        // Lapis
        registerTieredRecipe(consumer, "lapis", capsules_tier2,
                new Item[]{Items.LAPIS_LAZULI, Items.LAPIS_LAZULI, Items.LAPIS_BLOCK},
                new int[]{3, 6, 1}, new String[]{"C C", "   ", "C C"});

        // Lead
        registerTieredRecipe(consumer, "lead", capsules_tier2,
                new Item[]{BlockList.LEAD_NUGGET.get(), BlockList.LEAD_INGOT.get(), BlockList.LEAD_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"C C", " C ", "   "});

        // Tier 3 capsule recipes
        Item[] capsules_tier3 = new Item[]{ModItems.TIER_3_MINI_CAPSULE.get(), ModItems.TIER_3_MEDIUM_CAPSULE.get(), ModItems.TIER_3_LARGE_CAPSULE.get()};

        // Osmium
        registerTieredRecipe(consumer, "osmium", capsules_tier3,
                new Item[]{BlockList.OSMIUM_NUGGET.get(), BlockList.OSMIUM_INGOT.get(), BlockList.OSMIUM_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"CCC", "   ", "   "});

        // Uranium
        registerTieredRecipe(consumer, "uranium", capsules_tier3,
                new Item[]{BlockList.URANIUM_NUGGET.get(), BlockList.URANIUM_INGOT.get(), BlockList.URANIUM_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"CC ", "  C", "   "});

        // Emerald
        registerTieredRecipe(consumer, "emerald", capsules_tier3,
                new Item[]{Items.EMERALD, Items.EMERALD, Items.EMERALD_BLOCK},
                new int[]{1, 3, 1}, new String[]{"CCC", "C C", "CCC"});

        // Platinum
        registerTieredRecipe(consumer, "platinum", capsules_tier3,
                new Item[]{BlockList.PLATINUM_NUGGET.get(), BlockList.PLATINUM_INGOT.get(), BlockList.PLATINUM_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"C  ", "C  ", "C  "});

        // Aluminum
        registerTieredRecipe(consumer, "aluminum", capsules_tier3,
                new Item[]{BlockList.ALUMINUM_NUGGET.get(), BlockList.ALUMINUM_INGOT.get(), BlockList.ALUMINUM_BLOCK.get().asItem()},
                new int[]{3, 1, 1}, new String[]{"C C", " C ", "   "});

    }

    private void registerOverworldRecipes(Consumer<FinishedRecipe> consumer) {
        Item capsule = ModItems.OVERWORLD_CAPSULE.get();

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ARROW, 2)
                .requires(capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "arrow_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PRISMARINE_CRYSTALS)
                .pattern("DDD").pattern("DDD").pattern("DDD")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "prismarine_crystals_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PRISMARINE_SHARD, 2)
                .pattern("DDD").pattern("D D").pattern("DDD")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "prismarine_shard_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SPIDER_EYE, 2)
                .pattern("DDD").pattern(" D ").pattern("DDD")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "spider_eye_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.ROTTEN_FLESH, 3)
                .pattern("D  ").pattern(" D ").pattern("  D")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "rotten_flesh_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BONE, 2)
                .pattern("D  ").pattern("D  ").pattern("D  ")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "bone_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GUNPOWDER, 2)
                .pattern("D  ").pattern(" D ").pattern("D  ")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "gunpowder_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SLIME_BALL, 3)
                .pattern("  D").pattern(" D ").pattern("D  ")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "slime_ball_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SALMON, 3)
                .pattern("DD ").pattern("DD ").pattern("   ")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "salmon_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COD, 3)
                .pattern("D D").pattern("   ").pattern("D D")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "cod_from_overworld_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TROPICAL_FISH, 3)
                .pattern("D D").pattern("D D").pattern("   ")
                .define('D', capsule)
                .unlockedBy("has_overworld_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "tropical_fish_from_overworld_capsule"));
    }

    private void registerNetherRecipes(Consumer<FinishedRecipe> consumer) {
        Item capsule = ModItems.NETHER_CAPSULE.get();
        Item advCapsule = ModItems.NETHER_ADVANCED_CAPSULE.get();

        // Nether Capsule
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.QUARTZ)
                .pattern("D D").pattern(" D ").pattern("D D").define('D', capsule)
                .unlockedBy("has_nether_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "quartz_from_nether_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GLOWSTONE_DUST, 2)
                .pattern("DDD").define('D', capsule)
                .unlockedBy("has_nether_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "glowstone_dust_from_nether_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SOUL_SAND)
                .pattern("D  ").pattern(" D ").pattern("  D").define('D', capsule)
                .unlockedBy("has_nether_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "soul_sand_from_nether_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.MAGMA_CREAM)
                .pattern("  D").pattern(" D ").pattern("D  ").define('D', capsule)
                .unlockedBy("has_nether_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "magma_cream_from_nether_capsule"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.NETHER_WART)
                .requires(capsule)
                .unlockedBy("has_nether_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "nether_wart_from_nether_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GHAST_TEAR)
                .pattern("DDD").pattern("   ").pattern("DDD").define('D', capsule)
                .unlockedBy("has_nether_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "ghast_tear_from_nether_capsule"));

        // Nether Advanced Capsule
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.NETHERITE_SCRAP)
                .pattern("DDD").pattern("DDD").pattern("DDD").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "netherite_scrap_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WITHER_SKELETON_SKULL)
                .pattern("DDD").pattern("DED").pattern("DDD").define('D', advCapsule).define('E', Items.BONE_BLOCK)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "wither_skeleton_skull_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BLAZE_ROD)
                .pattern("DDD").pattern(" D ").pattern("DDD").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "blaze_rod_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.QUARTZ, 2)
                .pattern("D D").pattern(" D ").pattern("D D").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "quartz_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GLOWSTONE_DUST, 4)
                .pattern("DDD").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "glowstone_dust_from_nether_adv_capsule"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.NETHER_WART, 2)
                .requires(advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "nether_wart_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GHAST_TEAR, 2)
                .pattern("DDD").pattern("   ").pattern("DDD").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "ghast_tear_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SOUL_SAND, 2)
                .pattern("D  ").pattern(" D ").pattern("  D").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "soul_sand_from_nether_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.MAGMA_CREAM, 2)
                .pattern("  D").pattern(" D ").pattern("D  ").define('D', advCapsule)
                .unlockedBy("has_nether_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "magma_cream_from_nether_adv_capsule"));
    }

    private void registerEndRecipes(Consumer<FinishedRecipe> consumer) {
        Item capsule = ModItems.END_CAPSULE.get();
        Item advCapsule = ModItems.END_ADVANCED_CAPSULE.get();

        // End Capsule
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.ENDER_PEARL)
                .pattern("DDD").pattern("D D").pattern("DDD").define('D', capsule)
                .unlockedBy("has_end_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "ender_pearl_from_end_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SHULKER_SHELL)
                .pattern("D D").pattern(" D ").pattern("D D").define('D', capsule)
                .unlockedBy("has_end_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "shulker_shell_from_end_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.CHORUS_FRUIT)
                .pattern("DDD").pattern("   ").pattern("   ").define('D', capsule)
                .unlockedBy("has_end_capsule", has(capsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "chorus_fruit_from_end_capsule"));

        // End Advanced Capsule
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.ENDER_PEARL, 2)
                .pattern("DDD").pattern("D D").pattern("DDD").define('D', advCapsule)
                .unlockedBy("has_end_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "ender_pearl_from_end_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SHULKER_SHELL, 2)
                .pattern("D D").pattern(" D ").pattern("D D").define('D', advCapsule)
                .unlockedBy("has_end_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "shulker_shell_from_end_adv_capsule"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.CHORUS_FRUIT, 2)
                .pattern("DDD").pattern("   ").pattern("   ").define('D', advCapsule)
                .unlockedBy("has_end_adv_capsule", has(advCapsule))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "chorus_fruit_from_end_adv_capsule"));
    }

    private void registerTieredRecipe(Consumer<FinishedRecipe> consumer, String group, Item[] capsules, Item[] ouputs, int[] outputCounts, String[] strings) {
        for (int i = 0; i < 3; i++) {
            String suffix = (i == 0) ? "_mini" : (i == 1) ? "_medium" : "_large";
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, group + "_from" + suffix + "_capsule");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ouputs[i], outputCounts[i])
                    .pattern(strings[0])
                    .pattern(strings[1])
                    .pattern(strings[2])
                    .group(group)
                    .define('C', capsules[i])
                    .unlockedBy("has_item",has(capsules[i]))
                    .save(consumer,recipeId);
        }
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
