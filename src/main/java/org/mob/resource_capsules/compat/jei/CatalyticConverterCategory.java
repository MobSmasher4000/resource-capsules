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
import net.minecraft.world.item.crafting.Ingredient;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.recipe.CatalyticConverterRecipe;

import java.util.ArrayList;
import java.util.List;

public class CatalyticConverterCategory implements IRecipeCategory<CatalyticConverterRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID, "catalytic_converter");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ResourceCapsules.MOD_ID,
            "textures/gui/jei/encapsulating_transmutator_gui.png");

    public static final RecipeType<CatalyticConverterRecipe> CATALYTIC_CONVERTER_RECIPE_TYPE =
            new RecipeType<>(UID, CatalyticConverterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CatalyticConverterCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 129, 64);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CATALYTIC_CONVERTER.get()));
    }

    @Override
    public RecipeType<CatalyticConverterRecipe> getRecipeType() {
        return CATALYTIC_CONVERTER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.resource_capsules.catalytic_converter");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CatalyticConverterRecipe recipe, IFocusGroup focuses) {
        int baseX = 9;
        int baseY = 8;
        int slotSize = 18;

        List<CatalyticConverterRecipe.SizedIngredient> inputs = recipe.getSizedIngredients();

        for (int i = 0; i < inputs.size(); i++) {
            int row = i / 3;
            int col = i % 3;
            int x = baseX + col * slotSize;
            int y = baseY + row * slotSize;

            CatalyticConverterRecipe.SizedIngredient sizedIng = inputs.get(i);
            Ingredient ing = sizedIng.ingredient();
            int count = sizedIng.count();

            List<ItemStack> displayStacks = new ArrayList<>();
            for (ItemStack stack : ing.getItems()) {
                ItemStack displayStack = stack.copy();
                displayStack.setCount(count);
                displayStacks.add(displayStack);
            }

            if (!displayStacks.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addItemStacks(displayStacks);
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addIngredients(ing);
            }
        }

        // Output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 25)
                .addItemStack(recipe.getResultItem(null));
    }
}