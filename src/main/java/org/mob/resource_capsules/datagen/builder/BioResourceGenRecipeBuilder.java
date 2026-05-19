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
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.BioResouceGenRecipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BioResourceGenRecipeBuilder implements RecipeBuilder {
    private Ingredient ingredient;
    private ItemStack output;
    private FluidStack inputFluid = FluidStack.EMPTY;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private BioResourceGenRecipeBuilder() {}

    public static BioResourceGenRecipeBuilder bioResourceGenRecipe() {
        return new BioResourceGenRecipeBuilder();
    }

    public BioResourceGenRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public BioResourceGenRecipeBuilder addOutput(ItemStack output) {
        this.output = output;
        return this;
    }

    public BioResourceGenRecipeBuilder addFluidInput(FluidStack fluid) {
        this.inputFluid = fluid;
        return this;
    }

    public BioResourceGenRecipeBuilder count(int count) {
        if (this.output != null) {
            this.output.setCount(count);
        }
        return this;
    }

    @Override
    public BioResourceGenRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public BioResourceGenRecipeBuilder group(@Nullable String group) {
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

        Advancement.Builder advancementBuilder = pRecipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "bio_resource_gen/" + pId.getPath()
        );

        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
                pId.getNamespace(),
                "recipes/bio_resource_gen/" + pId.getPath()
        );

        BioResouceGenRecipe recipe = new BioResouceGenRecipe(
                List.of(this.ingredient),
                this.inputFluid,
                this.output
        );

        pRecipeOutput.accept(recipeId, recipe, advancementBuilder.build(advancementId));
    }
}