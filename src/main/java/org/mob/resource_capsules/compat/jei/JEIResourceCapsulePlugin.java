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
import org.mob.resource_capsules.recipe.*;
import org.mob.resource_capsules.screen.screen.*;

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

        registration.addRecipeCategories(new ResourceGenTier4Category(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new ResourceGenTier5Category(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new DimensionalResourceGenCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new EncapsulatingTransmutatorCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new CatalyticConverterCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new BioResourceGenCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new FluidGenCategory(
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

        List<ResourceGenTier4Recipe> resourceGenTier4Recipes = recipeManager.getAllRecipesFor(ResourceGenTier4Recipe.Type.INSTANCE);
        registration.addRecipes(ResourceGenTier4Category.RESOURCE_GEN_TIER_4_RECIPE_TYPE, resourceGenTier4Recipes);

        List<ResourceGenTier5Recipe> resourceGenTier5Recipes = recipeManager.getAllRecipesFor(ResourceGenTier5Recipe.Type.INSTANCE);
        registration.addRecipes(ResourceGenTier5Category.RESOURCE_GEN_TIER_5_RECIPE_TYPE, resourceGenTier5Recipes);

        List<DimensionalResourceGenRecipe> dimensionalResourceGenRecipes = recipeManager.getAllRecipesFor(DimensionalResourceGenRecipe.Type.INSTANCE);
        registration.addRecipes(DimensionalResourceGenCategory.DIMENSIONAL_RESOURCE_GEN_RECIPE_TYPE, dimensionalResourceGenRecipes);

        List<EncapsulatingTransmutatorRecipe> encapsulatingTransmutatorRecipes = recipeManager.getAllRecipesFor(EncapsulatingTransmutatorRecipe.Type.INSTANCE);
        registration.addRecipes(EncapsulatingTransmutatorCategory.ENCAPSULATING_TRANSMUTATOR_RECIPE_TYPE, encapsulatingTransmutatorRecipes);

        List<CatalyticConverterRecipe> catalyticConverterRecipes = recipeManager.getAllRecipesFor(CatalyticConverterRecipe.Type.INSTANCE);
        registration.addRecipes(CatalyticConverterCategory.CATALYTIC_CONVERTER_RECIPE_TYPE, catalyticConverterRecipes);

        List<BioResouceGenRecipe> bioResouceGenRecipes = recipeManager.getAllRecipesFor(BioResouceGenRecipe.Type.INSTANCE);
        registration.addRecipes(BioResourceGenCategory.BIO_RESOUCE_GEN_RECIPE_TYPE, bioResouceGenRecipes);

        List<FluidGenRecipe> fluidGenRecipes = recipeManager.getAllRecipesFor(FluidGenRecipe.Type.INSTANCE);
        registration.addRecipes(FluidGenCategory.FLUID_GEN_RECIPE_TYPE, fluidGenRecipes);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ResourceGenTier1Screen.class, 74, 30, 22, 20,
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);

        registration.addRecipeClickArea(ResourceGenTier2Screen.class, 74, 30, 22, 20,
                ResourceGenTier2Category.RESOURCE_GEN_TIER_2_RECIPE_TYPE);

        registration.addRecipeClickArea(ResourceGenTier3Screen.class, 74, 30, 22, 20,
                ResourceGenTier3Category.RESOURCE_GEN_TIER_3_RECIPE_TYPE);

        registration.addRecipeClickArea(ResourceGenTier4Screen.class, 74, 30, 22, 20,
                ResourceGenTier4Category.RESOURCE_GEN_TIER_4_RECIPE_TYPE);

        registration.addRecipeClickArea(ResourceGenTier5Screen.class, 74, 30, 22, 20,
                ResourceGenTier5Category.RESOURCE_GEN_TIER_5_RECIPE_TYPE);

        registration.addRecipeClickArea(DimensionalResourceGenScreen.class, 74, 30, 22, 20,
                DimensionalResourceGenCategory.DIMENSIONAL_RESOURCE_GEN_RECIPE_TYPE);

        registration.addRecipeClickArea(EncapsulatingTransmutatorScreen.class, 90, 33, 23, 18,
                EncapsulatingTransmutatorCategory.ENCAPSULATING_TRANSMUTATOR_RECIPE_TYPE);

        registration.addRecipeClickArea(CatalyticConverterScreen.class, 90, 33, 23, 18,
                CatalyticConverterCategory.CATALYTIC_CONVERTER_RECIPE_TYPE);

        registration.addRecipeClickArea(BioResourceGenScreen.class, 105, 32, 7, 27,
                BioResourceGenCategory.BIO_RESOUCE_GEN_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_1.get().asItem()),
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_2.get().asItem()),
                ResourceGenTier2Category.RESOURCE_GEN_TIER_2_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_3.get().asItem()),
                ResourceGenTier3Category.RESOURCE_GEN_TIER_3_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_4.get().asItem()),
                ResourceGenTier4Category.RESOURCE_GEN_TIER_4_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_TIER_5.get().asItem()),
                ResourceGenTier5Category.RESOURCE_GEN_TIER_5_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get().asItem()),
                ResourceGenTier1Category.RESOURCE_GEN_TIER_1_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get().asItem()),
                ResourceGenTier2Category.RESOURCE_GEN_TIER_2_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get().asItem()),
                ResourceGenTier3Category.RESOURCE_GEN_TIER_3_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get().asItem()),
                ResourceGenTier4Category.RESOURCE_GEN_TIER_4_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get().asItem()),
                ResourceGenTier5Category.RESOURCE_GEN_TIER_5_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DIMENSIONAL_RESOURCE_GEN.get().asItem()),
                DimensionalResourceGenCategory.DIMENSIONAL_RESOURCE_GEN_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENCAPSULATING_TRANSMUTATOR.get().asItem()),
                EncapsulatingTransmutatorCategory.ENCAPSULATING_TRANSMUTATOR_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYTIC_CONVERTER.get().asItem()),
                CatalyticConverterCategory.CATALYTIC_CONVERTER_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BIO_RESOURCE_GEN.get().asItem()),
                BioResourceGenCategory.BIO_RESOUCE_GEN_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FLUID_GEN.get().asItem()),
                FluidGenCategory.FLUID_GEN_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FLUID_GEN_MULTIBLOCK.get().asItem()),
                FluidGenCategory.FLUID_GEN_RECIPE_TYPE);
    }
}