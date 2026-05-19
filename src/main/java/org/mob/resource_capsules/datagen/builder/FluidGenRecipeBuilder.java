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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.FluidGenRecipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FluidGenRecipeBuilder implements RecipeBuilder {
    private Ingredient ingredient;
    private FluidStack outputFluid = FluidStack.EMPTY;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private FluidGenRecipeBuilder() {}

    public static FluidGenRecipeBuilder fluidGenRecipe() {
        return new FluidGenRecipeBuilder();
    }

    public FluidGenRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public FluidGenRecipeBuilder addOutputFluid(FluidStack fluid) {
        this.outputFluid = fluid;
        return this;
    }

    @Override
    public FluidGenRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public FluidGenRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        if (this.ingredient == null) {
            throw new IllegalStateException("Missing ingredient for " + pId);
        }
        if (this.outputFluid == null || this.outputFluid.isEmpty()) {
            throw new IllegalStateException("Missing output fluid for " + pId);
        }

        Advancement.Builder advancementBuilder = pRecipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "fluid_gen/" + pId.getPath()
        );

        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "recipes/fluid_gen/" + pId.getPath()
        );

        FluidGenRecipe recipe = new FluidGenRecipe(this.ingredient, this.outputFluid);

        pRecipeOutput.accept(recipeId, recipe, advancementBuilder.build(advancementId));
    }
}