package org.mob.resource_capsules.datagen;

import net.allthemods.alltheores.blocks.BlockList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
