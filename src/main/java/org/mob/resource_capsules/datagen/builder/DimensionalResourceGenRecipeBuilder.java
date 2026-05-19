package org.mob.resource_capsules.datagen.builder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.DimensionalResourceGenRecipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DimensionalResourceGenRecipeBuilder implements RecipeBuilder {
    private Ingredient ingredient;
    private ItemStack output;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private String dimension;

    private DimensionalResourceGenRecipeBuilder() {}

    public static DimensionalResourceGenRecipeBuilder dimensionalResourceGenRecipe() {
        return new DimensionalResourceGenRecipeBuilder();
    }

    public DimensionalResourceGenRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public DimensionalResourceGenRecipeBuilder addOutput(ItemStack output) {
        this.output = output;
        return this;
    }

    public DimensionalResourceGenRecipeBuilder count(int count) {
        if (this.output != null) {
            this.output.setCount(count);
        }
        return this;
    }

    public DimensionalResourceGenRecipeBuilder dimension(String dimensionId) {
        this.dimension = dimensionId;
        return this;
    }

    @Override
    public DimensionalResourceGenRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public DimensionalResourceGenRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output == null || this.output.isEmpty() ? Items.AIR : this.output.getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        if (this.ingredient == null) {
            throw new IllegalStateException("Missing ingredient for " + pId);
        }
        if (this.output == null || this.output.isEmpty()) {
            throw new IllegalStateException("Missing output for " + pId);
        }
        if (this.dimension == null || this.dimension.isEmpty()) {
            throw new IllegalStateException("Dimension must be specified for " + pId);
        }

        Advancement.Builder advancementBuilder = pRecipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "dimensional_resource_gen/" + pId.getPath()
        );

        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "recipes/dimensional_resource_gen/" + pId.getPath()
        );

        DimensionalResourceGenRecipe recipe = new DimensionalResourceGenRecipe(
                List.of(this.ingredient),
                this.output,
                this.dimension
        );

        pRecipeOutput.accept(recipeId, recipe, advancementBuilder.build(advancementId));
    }

}