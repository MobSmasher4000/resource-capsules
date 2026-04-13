package org.mob.resource_capsules.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.FluidGenRecipe;
import org.mob.resource_capsules.recipe.ModRecipes; // Replace with where your FLUID_GEN_SERIALIZER is registered

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FluidGenRecipeBuilder implements RecipeBuilder {
    private Ingredient ingredient;
    private FluidStack outputFluid = FluidStack.EMPTY;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
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
    public FluidGenRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
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
        // Required by RecipeBuilder, but since we output fluid, we return AIR
        return Items.AIR;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (ingredient == null) {
            throw new IllegalStateException("Missing ingredient for " + id);
        }
        if (outputFluid == null || outputFluid.isEmpty()) {
            throw new IllegalStateException("Missing output fluid for " + id);
        }

        Advancement.Builder advancement = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);

        this.criteria.forEach(advancement::addCriterion);

        ResourceLocation recipeId = new ResourceLocation(
                id.getNamespace(),
                "fluid_gen/" + id.getPath()
        );

        ResourceLocation advancementId = new ResourceLocation(
                id.getNamespace(),
                "recipes/fluid_gen/" + id.getPath()
        );

        consumer.accept(new Result(
                recipeId,
                this.ingredient,
                this.outputFluid,
                this.group == null ? "" : this.group,
                advancement,
                advancementId
        ));
    }

    // Inner record class for saving the actual JSON
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final FluidStack outputFluid;
        private final String group;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Ingredient ingredient, FluidStack outputFluid, String group,
                      Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.ingredient = ingredient;
            this.outputFluid = outputFluid;
            this.group = group;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!group.isEmpty()) {
                json.addProperty("group", group);
            }

            // Write the Input Ingredient
            JsonArray ingredientsArray = new JsonArray();
            ingredientsArray.add(ingredient.toJson());
            json.add("ingredients", ingredientsArray);

            // Write the Fluid Output
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("amount", outputFluid.getAmount());
            resultObj.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(outputFluid.getFluid()).toString());
            json.add("result", resultObj);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return FluidGenRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }
}