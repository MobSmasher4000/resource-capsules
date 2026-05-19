package org.mob.resource_capsules.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mob.resource_capsules.ResourceCapsules;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ResourceCapsules.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ResourceGenTier1Recipe>> RESOURCE_GEN_TIER_1_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_1", () -> ResourceGenTier1Recipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ResourceGenTier2Recipe>> RESOURCE_GEN_TIER_2_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_2", () -> ResourceGenTier2Recipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ResourceGenTier3Recipe>> RESOURCE_GEN_TIER_3_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_3", () -> ResourceGenTier3Recipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DimensionalResourceGenRecipe>> DIMENSIONAL_RESOURCE_GEN_SERIALIZER =
            SERIALIZERS.register("dimensional_resource_gen", () -> DimensionalResourceGenRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EncapsulatingTransmutatorRecipe>> ENCAPSULATING_TRANSMUTATOR_SERIALIZER =
            SERIALIZERS.register("encapsulating_transmutator", () -> EncapsulatingTransmutatorRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CatalyticConverterRecipe>> CATALYTIC_CONVERTER_SERIALIZER =
            SERIALIZERS.register("catalytic_converter", () -> CatalyticConverterRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BioResouceGenRecipe>> BIO_RESOURCE_GEN_SERIALIZER =
            SERIALIZERS.register("bio_resource_gen", () -> BioResouceGenRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidGenRecipe>> FLUID_GEN_SERIALIZER =
            SERIALIZERS.register("fluid_gen", () -> FluidGenRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
