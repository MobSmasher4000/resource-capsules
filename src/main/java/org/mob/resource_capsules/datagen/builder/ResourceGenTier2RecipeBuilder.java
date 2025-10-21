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
import org.mob.resource_capsules.recipe.ResourceGenTier1Recipe;
import org.mob.resource_capsules.recipe.ResourceGenTier2Recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ResourceGenTier2RecipeBuilder implements RecipeBuilder {
    private Ingredient ingredient;
    private ItemStack output;
    private int count = 1;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private ResourceGenTier2RecipeBuilder() {}

    public static ResourceGenTier2RecipeBuilder resourceGenTier2Recipe() {
        return new ResourceGenTier2RecipeBuilder();
    }

    public ResourceGenTier2RecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public ResourceGenTier2RecipeBuilder addOutput(ItemStack output) {
        this.output = output;
        this.count = output.getCount();
        return this;
    }

    public ResourceGenTier2RecipeBuilder count(int count) {
        this.count = count;
        return this;
    }

    @Override
    public ResourceGenTier2RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public ResourceGenTier2RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output == null || this.output.isEmpty() ? Items.AIR : this.output.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (ingredient == null) {
            throw new IllegalStateException("Missing ingredient for " + id);
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
                "resource_gen_tier_2/" + id.getPath()
        );

        // 👇 And same for advancements (to avoid conflicts)
        ResourceLocation advancementId = new ResourceLocation(
                id.getNamespace(),
                "recipes/resource_gen_tier_2/" + id.getPath()
        );


        consumer.accept(new Result(
                recipeId,
                this.ingredient,
                this.output,
                this.count,
                this.group == null ? "" : this.group,
                advancement,
                advancementId
        ));
    }

    // Inner record class for saving the actual JSON
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final ItemStack output;
        private final int count;
        private final String group;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Ingredient ingredient, ItemStack output, Integer count, String group,
                      Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.ingredient = ingredient;
            this.output = output;
            this.count = count;
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
            ingredientsArray.add(ingredient.toJson());
            json.add("ingredients", ingredientsArray);

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(output.getItem()).toString());
            resultObj.addProperty("count", count);
            json.add("result", resultObj);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {
            return ResourceGenTier2Recipe.Serializer.INSTANCE;
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
