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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.mob.resource_capsules.ResourceCapsules;

import javax.annotation.Nullable;

public class BioResouceGenRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final FluidStack inputFluid;
    private final ItemStack output;
    private final ResourceLocation id;

    public BioResouceGenRecipe(NonNullList<Ingredient> inputItems, FluidStack inputFluid, ItemStack output, ResourceLocation id) {
        this.inputItems = inputItems;
        this.inputFluid = inputFluid;
        this.output = output;
        this.id = id;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) return false;

        // Check item match
        boolean itemMatches = inputItems.get(0).test(container.getItem(1));

        // Check fluid match (if recipe requires fluid)
        boolean fluidMatches = true;
        if (!inputFluid.isEmpty() && container instanceof BioContainer bioContainer) {
            FluidStack tankFluid = bioContainer.getFluidStack();
            fluidMatches = !tankFluid.isEmpty()
                    && tankFluid.getFluid().isSame(inputFluid.getFluid())
                    && tankFluid.getAmount() >= inputFluid.getAmount();
        }

        return itemMatches && fluidMatches;
    }

    /**
     * Small inner class or separate file to carry fluid data to the recipe
     */
    public static class BioContainer extends SimpleContainer {
        private final FluidStack fluidStack;

        public BioContainer(int size, FluidStack fluidStack) {
            super(size);
            this.fluidStack = fluidStack;
        }

        public FluidStack getFluidStack() {
            return fluidStack;
        }
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

    // Type
    public static class Type implements RecipeType<BioResouceGenRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "bio_resource_gen";
    }

    // Serializer
    public static class Serializer implements RecipeSerializer<BioResouceGenRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ResourceCapsules.MOD_ID, "bio_resource_gen");

        @Override
        public BioResouceGenRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // Output item
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            // Ingredient(s)
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);
            inputs.set(0, Ingredient.fromJson(ingredients.get(0)));

            // Fluid input (optional)
            FluidStack inputFluid = FluidStack.EMPTY;
            if (json.has("fluid")) {
                JsonObject fluidJson = GsonHelper.getAsJsonObject(json, "fluid");
                ResourceLocation fluidId = new ResourceLocation(GsonHelper.getAsString(fluidJson, "fluid"));
                int amount = GsonHelper.getAsInt(fluidJson, "amount", 0);
                if (ForgeRegistries.FLUIDS.containsKey(fluidId)) {
                    inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidId), amount);
                }
            }

            return new BioResouceGenRecipe(inputs, inputFluid, output, recipeId);
        }

        @Nullable
        @Override
        public BioResouceGenRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int size = buffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            FluidStack fluid = FluidStack.readFromPacket(buffer);

            ItemStack output = buffer.readItem();
            return new BioResouceGenRecipe(inputs, fluid, output, recipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BioResouceGenRecipe recipe) {
            buffer.writeInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            recipe.inputFluid.writeToPacket(buffer);

            buffer.writeItem(recipe.getResultItem(null));
        }
    }
}
