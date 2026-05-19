package org.mob.resource_capsules.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class BioResouceGenRecipe implements Recipe<BioResouceGenRecipe.BioRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final FluidStack inputFluid;
    private final ItemStack output;

    public BioResouceGenRecipe(List<Ingredient> inputItems, FluidStack inputFluid, ItemStack output) {
        this.inputItems = NonNullList.create();
        this.inputItems.addAll(inputItems);
        this.inputFluid = inputFluid;
        this.output = output;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    @Override
    public boolean matches(BioRecipeInput input, Level level) {
        if (level.isClientSide()) return false;

        boolean itemMatches = inputItems.get(0).test(input.item());

        boolean fluidMatches = true;
        if (!inputFluid.isEmpty()) {
            FluidStack tankFluid = input.fluidStack();
            fluidMatches = !tankFluid.isEmpty()
                    && tankFluid.is(inputFluid.getFluid())
                    && tankFluid.getAmount() >= inputFluid.getAmount();
        }

        return itemMatches && fluidMatches;
    }

    public record BioRecipeInput(ItemStack item, FluidStack fluidStack) implements RecipeInput {
        @Override
        public ItemStack getItem(int pIndex) {
            return pIndex == 0 ? item : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public ItemStack assemble(BioRecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BioResouceGenRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "bio_resource_gen";
    }

    public static class Serializer implements RecipeSerializer<BioResouceGenRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<BioResouceGenRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(BioResouceGenRecipe::getIngredients),
                FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(BioResouceGenRecipe::getInputFluid),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output)
        ).apply(inst, BioResouceGenRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BioResouceGenRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), BioResouceGenRecipe::getIngredients,
                FluidStack.OPTIONAL_STREAM_CODEC, BioResouceGenRecipe::getInputFluid,
                ItemStack.STREAM_CODEC, r -> r.output,
                BioResouceGenRecipe::new
        );

        @Override
        public MapCodec<BioResouceGenRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BioResouceGenRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}