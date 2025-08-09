package com.mrh0.createaddition.recipe.rolling;

import com.mrh0.createaddition.index.CARecipes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RollingRecipe extends StandardProcessingRecipe<RecipeWrapper> {
    public static final IRecipeTypeInfo TYPE_INFO = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CARecipes.ROLLING.getId();
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) CARecipes.ROLLING.get();
        }

        @Override
        public <V extends RecipeInput, R extends Recipe<V>> RecipeType<R> getType() {
            return (RecipeType<R>) CARecipes.ROLLING_TYPE.get();
        }
    };

    public RollingRecipe(ProcessingRecipeParams params) {
        super(TYPE_INFO, params);
    }

    public Ingredient getIngredient() {
        return ingredients.get(0);
    }

    @Override
    public boolean matches(RecipeWrapper inv, @NotNull Level level) {
        if (inv.isEmpty()) return false;
        return ingredients.get(0).test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    public ItemStack getResultStack() {
        return getRollableResults().getFirst().getStack();
    }
}
