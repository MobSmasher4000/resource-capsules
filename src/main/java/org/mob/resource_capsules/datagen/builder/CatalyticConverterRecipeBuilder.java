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
import org.mob.resource_capsules.recipe.CatalyticConverterRecipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalyticConverterRecipeBuilder implements RecipeBuilder {

    private final List<CatalyticConverterRecipe.SizedIngredient> ingredients = new ArrayList<>();
    private ItemStack output;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private CatalyticConverterRecipeBuilder() {}

    public static CatalyticConverterRecipeBuilder catalyticConverterRecipe() {
        return new CatalyticConverterRecipeBuilder();
    }

    public CatalyticConverterRecipeBuilder addIngredient(Ingredient ingredient, int count) {
        if (this.ingredients.size() >= 9) {
            throw new IllegalStateException("Cannot have more than 9 ingredients!");
        }
        this.ingredients.add(new CatalyticConverterRecipe.SizedIngredient(ingredient, count));
        return this;
    }

    public CatalyticConverterRecipeBuilder addOutput(ItemStack output) {
        this.output = output;
        return this;
    }

    public CatalyticConverterRecipeBuilder outputCount(int count) {
        if (this.output != null) {
            this.output.setCount(count);
        }
        return this;
    }

    @Override
    public CatalyticConverterRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public CatalyticConverterRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output == null || this.output.isEmpty() ? Items.AIR : this.output.getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        if (this.ingredients.isEmpty()) {
            throw new IllegalStateException("Missing ingredients for " + pId);
        }
        if (this.output == null || this.output.isEmpty()) {
            throw new IllegalStateException("Missing output for " + pId);
        }

        Advancement.Builder advancementBuilder = pRecipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "catalytic_converter/" + pId.getPath()
        );

        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "recipes/catalytic_converter/" + pId.getPath()
        );

        CatalyticConverterRecipe recipe = new CatalyticConverterRecipe(this.ingredients, this.output);

        pRecipeOutput.accept(recipeId, recipe, advancementBuilder.build(advancementId));
    }

}