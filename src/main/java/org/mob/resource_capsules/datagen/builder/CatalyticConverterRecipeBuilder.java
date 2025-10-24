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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.recipe.CatalyticConverterRecipe;
import org.mob.resource_capsules.recipe.EncapsulatingTransmutatorRecipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CatalyticConverterRecipeBuilder implements RecipeBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<Integer> inputCounts = new ArrayList<>();
    private ItemStack output;
    private int outputCount = 1;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private CatalyticConverterRecipeBuilder() {}

    public static CatalyticConverterRecipeBuilder catalyticConverterRecipe() {
        return new CatalyticConverterRecipeBuilder();
    }

    // Add ingredient with specific count
    public CatalyticConverterRecipeBuilder addIngredient(Ingredient ingredient, int count) {
        if (this.ingredients.size() >= 9) {
            throw new IllegalStateException("Cannot have more than 9 ingredients!");
        }
        this.ingredients.add(ingredient);
        this.inputCounts.add(count);
        return this;
    }

    // Add output item
    public CatalyticConverterRecipeBuilder addOutput(ItemStack output) {
        this.output = output;
        this.outputCount = output.getCount();
        return this;
    }

    public CatalyticConverterRecipeBuilder outputCount(int count) {
        this.outputCount = count;
        return this;
    }

    @Override
    public CatalyticConverterRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
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
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("Missing ingredients for " + id);
        }
        if (output == null || output.isEmpty()) {
            throw new IllegalStateException("Missing output for " + id);
        }

        Advancement.Builder advancement = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);

        this.criteria.forEach(advancement::addCriterion);

        ResourceLocation recipeId = new ResourceLocation(
                id.getNamespace(),
                "catalytic_converter/" + id.getPath()
        );

        ResourceLocation advancementId = new ResourceLocation(
                id.getNamespace(),
                "recipes/catalytic_converter/" + id.getPath()
        );

        consumer.accept(new Result(
                recipeId,
                this.ingredients,
                this.inputCounts,
                this.output,
                this.outputCount,
                this.group == null ? "" : this.group,
                advancement,
                advancementId
        ));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final List<Ingredient> ingredients;
        private final List<Integer> inputCounts;
        private final ItemStack output;
        private final int outputCount;
        private final String group;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id,
                      List<Ingredient> ingredients,
                      List<Integer> inputCounts,
                      ItemStack output,
                      int outputCount,
                      String group,
                      Advancement.Builder advancement,
                      ResourceLocation advancementId) {
            this.id = id;
            this.ingredients = ingredients;
            this.inputCounts = inputCounts;
            this.output = output;
            this.outputCount = outputCount;
            this.group = group;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!group.isEmpty()) {
                json.addProperty("group", group);
            }

            JsonArray ingredientsArray = new JsonArray();
            for (int i = 0; i < ingredients.size(); i++) {
                JsonObject entry = new JsonObject();
                entry.add("ingredient", ingredients.get(i).toJson());
                entry.addProperty("count", inputCounts.get(i));
                ingredientsArray.add(entry);
            }
            json.add("ingredients", ingredientsArray);

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(output.getItem()).toString());
            resultObj.addProperty("count", outputCount);
            json.add("result", resultObj);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {
            return CatalyticConverterRecipe.Serializer.INSTANCE;
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
