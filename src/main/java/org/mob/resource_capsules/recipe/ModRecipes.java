package org.mob.resource_capsules.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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

    public static final RegistryObject<RecipeSerializer<DimensionalResourceGenRecipe>> DIMENSIONAL_RESOURCE_GEN_SERIALIZER =
            SERIALIZERS.register("dimensional_resource_gen", () -> DimensionalResourceGenRecipe.Serializer.INSTANCE);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
