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

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
