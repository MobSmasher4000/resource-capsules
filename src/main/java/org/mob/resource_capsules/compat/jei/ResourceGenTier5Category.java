package org.mob.resource_capsules.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
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
import org.mob.resource_capsules.recipe.ResourceGenTier5Recipe;

public class ResourceGenTier5Category implements IRecipeCategory<ResourceGenTier5Recipe> {
    public static final ResourceLocation UID = new ResourceLocation(ResourceCapsules.MOD_ID, "resource_gen_tier_5");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ResourceCapsules.MOD_ID,
            "textures/gui/jei/resource_gen_tier_gui.png");

    public static final RecipeType<ResourceGenTier5Recipe> RESOURCE_GEN_TIER_5_RECIPE_TYPE =
            new RecipeType<>(UID, ResourceGenTier5Recipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ResourceGenTier5Category(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 174, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RESOURCE_GEN_TIER_5.get()));
    }

    @Override
    public RecipeType<ResourceGenTier5Recipe> getRecipeType() {
        return RESOURCE_GEN_TIER_5_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {return Component.translatable("block.resource_capsules.resource_gen_tier_5");}

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ResourceGenTier5Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 54, 34).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 33).addItemStack(recipe.getResultItem(null));
    }
}
