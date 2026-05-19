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

public class CatalyticConverterRecipe implements Recipe<CatalyticConverterRecipe.CatalyticRecipeInput> {
    private final NonNullList<SizedIngredient> inputs;
    private final ItemStack output;

    public CatalyticConverterRecipe(List<SizedIngredient> inputs, ItemStack output) {
        this.inputs = NonNullList.create();
        this.inputs.addAll(inputs);
        this.output = output;
    }

    @Override
    public boolean matches(CatalyticRecipeInput pInput, Level pLevel) {
        List<ItemStack> available = new java.util.ArrayList<>();
        for (int i = 0; i < pInput.size(); i++) {
            ItemStack stack = pInput.getItem(i);
            if (!stack.isEmpty()) {
                available.add(stack.copy());
            }
        }

        for (SizedIngredient req : inputs) {
            int remainingNeeded = req.count();

            for (ItemStack stack : available) {
                if (req.ingredient().test(stack)) {
                    int take = Math.min(remainingNeeded, stack.getCount());
                    stack.shrink(take);
                    remainingNeeded -= take;

                    if (remainingNeeded <= 0) break;
                }
            }

            if (remainingNeeded > 0) return false;
        }

        return true;
    }

    public record CatalyticRecipeInput(List<ItemStack> items) implements RecipeInput {
        @Override
        public ItemStack getItem(int pIndex) {
            return pIndex >= 0 && pIndex < items.size() ? items.get(pIndex) : ItemStack.EMPTY;
        }

        @Override
        public int size() { return items.size(); }
    }

    public record SizedIngredient(Ingredient ingredient, int count) {
        public static final Codec<SizedIngredient> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(SizedIngredient::count)
        ).apply(inst, SizedIngredient::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient,
                ByteBufCodecs.VAR_INT, SizedIngredient::count,
                SizedIngredient::new
        );

        public boolean test(ItemStack stack) {
            return ingredient.test(stack) && stack.getCount() >= count;
        }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (SizedIngredient si : inputs) list.add(si.ingredient());
        return list;
    }

    public NonNullList<SizedIngredient> getSizedIngredients() {
        return inputs;
    }

    @Override
    public ItemStack assemble(CatalyticRecipeInput container, HolderLookup.Provider registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) { return output.copy(); }

    @Override
    public RecipeSerializer<?> getSerializer() { return Serializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    public static class Type implements RecipeType<CatalyticConverterRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "catalytic_converter";
    }

    public static class Serializer implements RecipeSerializer<CatalyticConverterRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<CatalyticConverterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(CatalyticConverterRecipe::getSizedIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output)
        ).apply(inst, CatalyticConverterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CatalyticConverterRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), CatalyticConverterRecipe::getSizedIngredients,
                ItemStack.STREAM_CODEC, r -> r.output,
                CatalyticConverterRecipe::new
        );

        @Override public MapCodec<CatalyticConverterRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, CatalyticConverterRecipe> streamCodec() { return STREAM_CODEC; }
    }
}