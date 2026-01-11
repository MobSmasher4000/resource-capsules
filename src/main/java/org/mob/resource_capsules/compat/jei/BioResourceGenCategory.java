package org.mob.resource_capsules.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.recipe.BioResouceGenRecipe;

public class BioResourceGenCategory implements IRecipeCategory<BioResouceGenRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ResourceCapsules.MOD_ID, "bio_resource_gen");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ResourceCapsules.MOD_ID,
            "textures/gui/jei/bio_resource_gen_gui.png");

    public static final RecipeType<BioResouceGenRecipe> BIO_RESOUCE_GEN_RECIPE_TYPE =
            new RecipeType<>(UID, BioResouceGenRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BioResourceGenCategory(IGuiHelper helper) {
        // Use your GUI texture here (pointing to the slots/progress bar area)
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,new ItemStack(ModBlocks.BIO_RESOURCE_GEN.get()));
    }

    @Override
    public RecipeType<BioResouceGenRecipe> getRecipeType() { return BIO_RESOUCE_GEN_RECIPE_TYPE; }

    @Override
    public Component getTitle() { return Component.translatable("block.resource_capsules.bio_resource_gen"); }

    @Override
    public IDrawable getBackground() { return background; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BioResouceGenRecipe recipe, IFocusGroup focuses) {
        // Input Item Slot
        builder.addSlot(RecipeIngredientRole.INPUT, 86, 15).addIngredients(recipe.getIngredients().get(0));

        // Output Item Slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 60).addItemStack(recipe.getResultItem(null));

        // Fluid Slot (Matching your tank)
        if (!recipe.getInputFluid().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 55, 15)
                    .addIngredient(ForgeTypes.FLUID_STACK, recipe.getInputFluid())
                    .setFluidRenderer(16000, false, 16, 62); // 16x48 px bar
        }
    }
}