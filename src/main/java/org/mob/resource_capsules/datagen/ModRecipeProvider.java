package org.mob.resource_capsules.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.mob.resource_capsules.datagen.builder.ResourceGenTier1RecipeBuilder;
import org.mob.resource_capsules.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        resourceGenTier1Recipe(consumer, ModItems.TIER_1_MINI_CATALYST.get(), ModItems.TIER_1_MINI_CAPSULE.get());
    }

    private void resourceGenTier1Recipe(Consumer<FinishedRecipe> consumer, Item input, Item output) {
        ResourceGenTier1RecipeBuilder.resourceGenTier1Recipe()
                .addIngredient(Ingredient.of(input))
                .addOutput(new ItemStack(output))
                .unlockedBy("has_" + input.toString().replace("minecraft:", ""), has(input))
                .save(consumer);
    }
}
