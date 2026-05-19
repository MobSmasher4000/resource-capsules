package org.mob.resource_capsules.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidGenRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient input;
    private final FluidStack outputFluid;

    public FluidGenRecipe(Ingredient input, FluidStack outputFluid) {
        this.input = input;
        this.outputFluid = outputFluid;
    }

    public FluidStack getOutputFluid() {
        return this.outputFluid.copy();
    }

    @Override
    public boolean matches(SingleRecipeInput pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        return input.test(pContainer.item());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    public Ingredient getInput() { return input; }

    @Override
    public ItemStack assemble(SingleRecipeInput pContainer, HolderLookup.Provider pRegistryAccess) { return ItemStack.EMPTY; }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) { return ItemStack.EMPTY; }

    @Override
    public RecipeSerializer<?> getSerializer() { return Serializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    public static class Type implements RecipeType<FluidGenRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_gen";
    }

    public static class Serializer implements RecipeSerializer<FluidGenRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<FluidGenRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> List.of(r.getInput())),
                FluidStack.CODEC.fieldOf("result").forGetter(FluidGenRecipe::getOutputFluid)
        ).apply(inst, (ingredients, fluid) -> new FluidGenRecipe(ingredients.get(0), fluid)));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidGenRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, FluidGenRecipe::getInput,
                FluidStack.STREAM_CODEC, FluidGenRecipe::getOutputFluid,
                FluidGenRecipe::new
        );

        @Override public MapCodec<FluidGenRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, FluidGenRecipe> streamCodec() { return STREAM_CODEC; }
    }
}