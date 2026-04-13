package org.mob.resource_capsules.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.mob.resource_capsules.ResourceCapsules;

import java.util.ArrayList;
import java.util.List;

public class EncapsulatingTransmutatorRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> inputs;
    private final List<Integer> inputCounts;

    public EncapsulatingTransmutatorRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, List<Integer> inputCounts, ItemStack output) {
        this.id = id;
        this.inputs = inputs;
        this.inputCounts = inputCounts;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        for (int i = 0; i < inputs.size(); i++) {
            Ingredient ingredient = inputs.get(i);
            int requiredCount = inputCounts.get(i);

            if (i >= container.getContainerSize()) return false;

            ItemStack stack = container.getItem(i);

            if (stack.isEmpty() || !ingredient.test(stack) || stack.getCount() < requiredCount) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public NonNullList<Ingredient> getIngredientsList() {
        return inputs;
    }

    public List<Integer> getInputCounts() {
        return inputCounts;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<EncapsulatingTransmutatorRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "encapsulating_transmutator";
    }

    public static class Serializer implements RecipeSerializer<EncapsulatingTransmutatorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ResourceCapsules.MOD_ID, "encapsulating_transmutator");

        @Override
        public EncapsulatingTransmutatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.create();
            List<Integer> counts = new ArrayList<>();

            for (JsonElement element : ingredientsJson) {
                JsonObject entry = element.getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(entry.get("ingredient"));
                int count = GsonHelper.getAsInt(entry, "count", 1);
                inputs.add(ingredient);
                counts.add(count);
            }

            JsonObject resultObj = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = new ItemStack(
                    BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(GsonHelper.getAsString(resultObj, "item"))),
                    GsonHelper.getAsInt(resultObj, "count", 1)
            );

            return new EncapsulatingTransmutatorRecipe(recipeId, inputs, counts, result);
        }

        @Override
        public EncapsulatingTransmutatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);
            List<Integer> counts = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
                counts.add(buffer.readVarInt());
            }
            ItemStack output = buffer.readItem();
            return new EncapsulatingTransmutatorRecipe(recipeId, inputs, counts, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EncapsulatingTransmutatorRecipe recipe) {
            buffer.writeVarInt(recipe.getIngredientsList().size());
            for (int i = 0; i < recipe.getIngredientsList().size(); i++) {
                recipe.getIngredientsList().get(i).toNetwork(buffer);
                buffer.writeVarInt(recipe.getInputCounts().get(i));
            }
            buffer.writeItem(recipe.getResultItem(null));
        }
    }
}
