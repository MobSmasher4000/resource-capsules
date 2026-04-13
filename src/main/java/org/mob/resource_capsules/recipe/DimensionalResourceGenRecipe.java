package org.mob.resource_capsules.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.mob.resource_capsules.ResourceCapsules;

import javax.annotation.Nullable;

public class DimensionalResourceGenRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;
    private final String allowedDimension;

    public DimensionalResourceGenRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id, String allowedDimension) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
        this.allowedDimension = allowedDimension;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) return false;

        // Dimension check
        if (allowedDimension != null && !level.dimension().location().toString().equals(allowedDimension)) {
            return false;
        }

        return inputItems.get(0).test(container.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
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
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public String getAllowedDimension() {
        return allowedDimension;
    }

    public static class Type implements RecipeType<DimensionalResourceGenRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "dimensional_resource_gen";
    }

    public static class Serializer implements RecipeSerializer<DimensionalResourceGenRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ResourceCapsules.MOD_ID, "dimensional_resource_gen");

        @Override
        public DimensionalResourceGenRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            String dimension = GsonHelper.getAsString(json, "dimension");

            return new DimensionalResourceGenRecipe(inputs, output, recipeId, dimension);
        }

        @Override
        public @Nullable DimensionalResourceGenRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            String dimension = buf.readUtf();

            return new DimensionalResourceGenRecipe(inputs, output, id, dimension);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DimensionalResourceGenRecipe recipe) {
            buf.writeInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output);
            buf.writeUtf(recipe.allowedDimension);
        }
    }
}
