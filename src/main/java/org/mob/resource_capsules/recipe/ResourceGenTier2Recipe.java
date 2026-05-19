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

import java.util.List;

public class ResourceGenTier2Recipe implements Recipe<SingleRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;

    public ResourceGenTier2Recipe(List<Ingredient> inputItems, ItemStack output) {
        this.inputItems = NonNullList.create();
        this.inputItems.addAll(inputItems);
        this.output = output;
    }

    @Override
    public boolean matches(SingleRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        return inputItems.get(0).test(pInput.item());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput pInput, HolderLookup.Provider pProvider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pProvider) {
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

    public static class Type implements RecipeType<ResourceGenTier2Recipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "resource_gen_tier_2";
    }

    public static class Serializer implements RecipeSerializer<ResourceGenTier2Recipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<ResourceGenTier2Recipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(ResourceGenTier2Recipe::getIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output)
        ).apply(inst, ResourceGenTier2Recipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ResourceGenTier2Recipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ResourceGenTier2Recipe::getIngredients,
                ItemStack.STREAM_CODEC, r -> r.output,
                ResourceGenTier2Recipe::new
        );

        @Override public MapCodec<ResourceGenTier2Recipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ResourceGenTier2Recipe> streamCodec() { return STREAM_CODEC; }
    }
}