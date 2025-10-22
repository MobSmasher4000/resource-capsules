package org.mob.resource_capsules.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.mob.resource_capsules.datagen.builder.DimensionalResourceGenRecipeBuilder;
import org.mob.resource_capsules.datagen.builder.ResourceGenTier1RecipeBuilder;
import org.mob.resource_capsules.datagen.builder.ResourceGenTier2RecipeBuilder;
import org.mob.resource_capsules.datagen.builder.ResourceGenTier3RecipeBuilder;
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

        dimensionalResourceGenRecipe(consumer, ModItems.OVERWORLD_CATALYST.get(), ModItems.OVERWORLD_CAPSULE.get());
        dimensionalResourceGenRecipe(consumer, ModItems.NETHER_CATALYST.get(), ModItems.NETHER_CAPSULE.get());
        dimensionalResourceGenRecipe(consumer, ModItems.NETHER_ADVANCED_CATALYST.get(), ModItems.NETHER_ADVANCED_CAPSULE.get());
        dimensionalResourceGenRecipe(consumer, ModItems.END_CATALYST.get(), ModItems.END_CAPSULE.get());
        dimensionalResourceGenRecipe(consumer, ModItems.END_ADVANCED_CATALYST.get(), ModItems.END_ADVANCED_CAPSULE.get());

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

    private void dimensionalResourceGenRecipe(Consumer<FinishedRecipe> consumer, Item input, Item output) {
        DimensionalResourceGenRecipeBuilder.dimensionalResourceGenRecipe()
                .addIngredient(Ingredient.of(input))
                .addOutput(new ItemStack(output))
                .unlockedBy("has_" + input.toString().replace("minecraft:", ""), has(input))
                .save(consumer);
    }
}
