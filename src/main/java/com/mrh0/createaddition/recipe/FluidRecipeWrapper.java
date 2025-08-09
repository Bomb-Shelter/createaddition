package com.mrh0.createaddition.recipe;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public class FluidRecipeWrapper implements Container, RecipeInput {

	public FluidStack fluid;
	
	public FluidRecipeWrapper(FluidStack fluid) {
		this.fluid = fluid;
	}
	
	@Override
	public void clearContent() {
		//fluid = new FluidStack(Fluids.EMPTY.getFluid(), 0);
	}

	@Override
	public int getContainerSize() {
		return 0;
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

	@Override
	public @NotNull ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int p_70304_1_) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public void setItem(int p_70299_1_, @NotNull ItemStack stack) {
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}

}
