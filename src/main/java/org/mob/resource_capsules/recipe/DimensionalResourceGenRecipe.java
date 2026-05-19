package org.mob.resource_capsules.recipe;

import com.mojang.serialization.Codec;
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

import java.util.List;

public class DimensionalResourceGenRecipe implements Recipe<SingleRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final String allowedDimension;

    public DimensionalResourceGenRecipe(List<Ingredient> inputItems, ItemStack output, String allowedDimension) {
        this.inputItems = NonNullList.create();
        this.inputItems.addAll(inputItems);
        this.output = output;
        this.allowedDimension = allowedDimension;
    }

    @Override
    public boolean matches(SingleRecipeInput container, Level level) {
        if (level.isClientSide()) return false;

        // Dimension check
        if (allowedDimension != null && !allowedDimension.isEmpty() && !level.dimension().location().toString().equals(allowedDimension)) {
            return false;
        }

        return inputItems.get(0).test(container.item());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() { return inputItems; }

    @Override
    public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider registryAccess) { return output.copy(); }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) { return output.copy(); }

    @Override
    public RecipeSerializer<?> getSerializer() { return Serializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    public String getAllowedDimension() { return allowedDimension; }

    public static class Type implements RecipeType<DimensionalResourceGenRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "dimensional_resource_gen";
    }

    public static class Serializer implements RecipeSerializer<DimensionalResourceGenRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<DimensionalResourceGenRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(DimensionalResourceGenRecipe::getIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output),
                Codec.STRING.optionalFieldOf("dimension", "").forGetter(DimensionalResourceGenRecipe::getAllowedDimension)
        ).apply(inst, DimensionalResourceGenRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DimensionalResourceGenRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), DimensionalResourceGenRecipe::getIngredients,
                ItemStack.STREAM_CODEC, r -> r.output,
                ByteBufCodecs.STRING_UTF8, DimensionalResourceGenRecipe::getAllowedDimension,
                DimensionalResourceGenRecipe::new
        );

        @Override public MapCodec<DimensionalResourceGenRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, DimensionalResourceGenRecipe> streamCodec() { return STREAM_CODEC; }
    }
}