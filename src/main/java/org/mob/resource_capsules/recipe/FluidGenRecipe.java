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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.ResourceCapsules;

public class FluidGenRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final FluidStack outputFluid;

    public FluidGenRecipe(ResourceLocation id, Ingredient input, FluidStack outputFluid) {
        this.id = id;
        this.input = input;
        this.outputFluid = outputFluid;
    }

    public FluidStack getOutputFluid() {
        return this.outputFluid.copy();
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        return input.test(pContainer.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() { return Serializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    // TYPE
    public static class Type implements RecipeType<FluidGenRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_gen";
    }

    // SERIALIZER
    public static class Serializer implements RecipeSerializer<FluidGenRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ResourceCapsules.MOD_ID, "fluid_gen");

        @Override
        public FluidGenRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            // 1. Read the Ingredient (Takes the first item in the "ingredients" array)
            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            Ingredient input = Ingredient.fromJson(ingredients.get(0));

            // 2. Read the Result
            JsonObject result = GsonHelper.getAsJsonObject(pSerializedRecipe, "result");
            String fluidId = GsonHelper.getAsString(result, "fluid");
            int amount = GsonHelper.getAsInt(result, "amount");

            // Build the fluid stack
            FluidStack fluidStack = new FluidStack(
                    ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidId)),
                    amount
            );

            return new FluidGenRecipe(pRecipeId, input, fluidStack);
        }

        @Override
        public @Nullable FluidGenRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            FluidStack output = pBuffer.readFluidStack();
            return new FluidGenRecipe(pRecipeId, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FluidGenRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.outputFluid);
        }
    }
}