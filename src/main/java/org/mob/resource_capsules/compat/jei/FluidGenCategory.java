package org.mob.resource_capsules.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.recipe.FluidGenRecipe;

public class FluidGenCategory implements IRecipeCategory<FluidGenRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "fluid_gen");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "textures/gui/jei/fluid_gen_gui.png");

    public static final RecipeType<FluidGenRecipe> FLUID_GEN_RECIPE_TYPE =
            new RecipeType<>(UID, FluidGenRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public FluidGenCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 71, 66);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FLUID_GEN.get()));
    }

    @Override
    public RecipeType<FluidGenRecipe> getRecipeType() {
        return FLUID_GEN_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.resource_capsules.fluid_gen");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidGenRecipe recipe, IFocusGroup focuses) {
        // Draw the Input Slot
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 3)
                .addIngredients(recipe.getIngredients().get(0));

        // Draw the Output Fluid Tank
        long fluidAmount = recipe.getOutputFluid().getAmount();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 3)
                .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getOutputFluid())
                .setFluidRenderer(fluidAmount, false, 16, 62) // Matches your 16x62 fluid tank!
                .setSlotName("Output Tank");
    }
}