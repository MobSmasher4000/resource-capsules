package org.mob.resource_capsules.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.recipe.ModRecipes;
import org.mob.resource_capsules.recipe.ResourceGenTier1Recipe;
import org.mob.resource_capsules.screen.screen.ResourceGenTierScreen;

import java.util.List;

public class JEIResourceCapsulePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ResourceCapsules.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ResourceGenTier1Category(
                registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<ResourceGenTier1Recipe> resourceGenTierRecipes = recipeManager.getAllRecipesFor(ResourceGenTier1Recipe.Type.INSTANCE);
        registration.addRecipes(ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE, resourceGenTierRecipes);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ResourceGenTierScreen.class, 60, 30, 20, 30,
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_1.get().asItem()),
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);
    }
}