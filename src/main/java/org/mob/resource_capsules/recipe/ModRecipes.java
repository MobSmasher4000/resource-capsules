package org.mob.resource_capsules.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ResourceCapsules.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ResourceGenTier1Recipe>> RESOURCE_GEN_TIER_1_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_1", () -> ResourceGenTier1Recipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ResourceGenTier2Recipe>> RESOURCE_GEN_TIER_2_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_2", () -> ResourceGenTier2Recipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ResourceGenTier3Recipe>> RESOURCE_GEN_TIER_3_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_3", () -> ResourceGenTier3Recipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ResourceGenTier4Recipe>> RESOURCE_GEN_TIER_4_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_4", () -> ResourceGenTier4Recipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ResourceGenTier5Recipe>> RESOURCE_GEN_TIER_5_SERIALIZER =
            SERIALIZERS.register("resource_gen_tier_5", () -> ResourceGenTier5Recipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<DimensionalResourceGenRecipe>> DIMENSIONAL_RESOURCE_GEN_SERIALIZER =
            SERIALIZERS.register("dimensional_resource_gen", () -> DimensionalResourceGenRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<EncapsulatingTransmutatorRecipe>> ENCAPSULATING_TRANSMUTATOR_SERIALIZER =
            SERIALIZERS.register("encapsulating_transmutator", () -> EncapsulatingTransmutatorRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CatalyticConverterRecipe>> CATALYTIC_CONVERTER_SERIALIZER =
            SERIALIZERS.register("catalytic_converter", () -> CatalyticConverterRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<BioResouceGenRecipe>> BIO_RESOURCE_GEN_SERIALIZER =
            SERIALIZERS.register("bio_resource_gen", () -> BioResouceGenRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<FluidGenRecipe>> FLUID_GEN_SERIALIZER =
            SERIALIZERS.register("fluid_gen", () -> FluidGenRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
