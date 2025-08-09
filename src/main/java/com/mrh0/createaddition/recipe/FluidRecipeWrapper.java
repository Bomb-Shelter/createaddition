package com.mrh0.createaddition.recipe;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public class FluidRecipeWrapper implements RecipeInput {

	public FluidStack fluid;
	
	public FluidRecipeWrapper(FluidStack fluid) {
		this.fluid = fluid;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public @NotNull ItemStack getItem(int i) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public int size() {
		return 0;
	}

}
