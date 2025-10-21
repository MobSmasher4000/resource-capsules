package org.mob.resource_capsules.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
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
import org.mob.resource_capsules.recipe.ResourceGenTier1Recipe;
import org.mob.resource_capsules.recipe.ResourceGenTier2Recipe;
import org.mob.resource_capsules.recipe.ResourceGenTier3Recipe;
import org.mob.resource_capsules.screen.screen.ResourceGenTier1Screen;
import org.mob.resource_capsules.screen.screen.ResourceGenTier2Screen;
import org.mob.resource_capsules.screen.screen.ResourceGenTier3Screen;

import java.util.List;

@JeiPlugin
public class JEIResourceCapsulePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ResourceCapsules.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ResourceGenTier1Category(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new ResourceGenTier2Category(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new ResourceGenTier3Category(
                registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<ResourceGenTier1Recipe> resourceGenTier1Recipes = recipeManager.getAllRecipesFor(ResourceGenTier1Recipe.Type.INSTANCE);
        registration.addRecipes(ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE, resourceGenTier1Recipes);

        List<ResourceGenTier2Recipe> resourceGenTier2Recipes = recipeManager.getAllRecipesFor(ResourceGenTier2Recipe.Type.INSTANCE);
        registration.addRecipes(ResourceGenTier2Category.RESOURCE_GEN_TIER_2_RECIPE_TYPE, resourceGenTier2Recipes);

        List<ResourceGenTier3Recipe> resourceGenTier3Recipes = recipeManager.getAllRecipesFor(ResourceGenTier3Recipe.Type.INSTANCE);
        registration.addRecipes(ResourceGenTier3Category.RESOURCE_GEN_TIER_3_RECIPE_TYPE, resourceGenTier3Recipes);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ResourceGenTier1Screen.class, 74, 30, 22, 20,
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);

        registration.addRecipeClickArea(ResourceGenTier2Screen.class, 74, 30, 22, 20,
                ResourceGenTier2Category.RESOURCE_GEN_TIER_2_RECIPE_TYPE);

        registration.addRecipeClickArea(ResourceGenTier3Screen.class, 74, 30, 22, 20,
                ResourceGenTier3Category.RESOURCE_GEN_TIER_3_RECIPE_TYPE);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_1.get().asItem()),
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_2.get().asItem()),
                ResourceGenTier2Category.RESOURCE_GEN_TIER_2_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_3.get().asItem()),
                ResourceGenTier3Category.RESOURCE_GEN_TIER_3_RECIPE_TYPE);
    }
}