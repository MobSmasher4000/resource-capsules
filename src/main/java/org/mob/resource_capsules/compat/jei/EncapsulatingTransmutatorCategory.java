package org.mob.resource_capsules.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.recipe.EncapsulatingTransmutatorRecipe;

import java.util.List;

public class EncapsulatingTransmutatorCategory implements IRecipeCategory<EncapsulatingTransmutatorRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ResourceCapsules.MOD_ID, "encapsulating_transmutator");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ResourceCapsules.MOD_ID,
            "textures/gui/jei/encapsulating_transmutator_gui.png");

    public static final RecipeType<EncapsulatingTransmutatorRecipe> ENCAPSULATING_TRANSMUTATOR_RECIPE_TYPE =
            new RecipeType<>(UID, EncapsulatingTransmutatorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public EncapsulatingTransmutatorCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 129, 64);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ENCAPSULATING_TRANSMUTATOR.get()));
    }

    @Override
    public RecipeType<EncapsulatingTransmutatorRecipe> getRecipeType() {
        return ENCAPSULATING_TRANSMUTATOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.resource_capsules.encapsulating_transmutator");
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
    public void setRecipe(IRecipeLayoutBuilder builder, EncapsulatingTransmutatorRecipe recipe, IFocusGroup focuses) {
        int baseX = 9;
        int baseY = 8;
        int slotSize = 18;

        List<Ingredient> inputs = recipe.getIngredientsList();
        List<Integer> counts = recipe.getInputCounts();

        for (int i = 0; i < inputs.size(); i++) {
            int row = i / 3;
            int col = i % 3;
            int x = baseX + col * slotSize;
            int y = baseY + row * slotSize;

            Ingredient ing = inputs.get(i);
            int count = counts.get(i);

            ItemStack[] matching = ing.getItems();
            if (matching.length > 0) {
                ItemStack stack = matching[0].copy();
                stack.setCount(count);
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addItemStack(stack);
            } else {
                // fallback if Ingredient is empty
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addIngredients(ing);
            }
        }

        // Output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 25)
                .addItemStack(recipe.getResultItem(null));
    }
}
