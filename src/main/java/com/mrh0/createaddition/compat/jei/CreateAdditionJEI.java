package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CreateAdditionJEI implements IModPlugin {

	private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "jei_plugin");

	@Override
	@NotNull
	public ResourceLocation getPluginUid() {
		return ID;
	}

	public IIngredientManager ingredientManager;
	static final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		ALL.clear();

		builder(ChargingRecipe.class)
				.addTypedRecipes(CARecipes.CHARGING_TYPE)
				.catalyst(CABlocks.TESLA_COIL::get)
				.itemIcon(CABlocks.TESLA_COIL.get())
				.emptyBackground(177, 53)
				.build("charging", ChargingCategory::new);

		builder(RollingRecipe.class)
				.addTypedRecipes(CARecipes.ROLLING_TYPE)
				.catalyst(CABlocks.ROLLING_MILL::get)
				.itemIcon(CABlocks.ROLLING_MILL.get())
				.emptyBackground(177, 53)
				.build("rolling", RollingMillCategory::new);

		builder(LiquidBurningRecipe.class)
				.addTypedRecipes(CARecipes.LIQUID_BURNING_TYPE)
				.catalyst(AllBlocks.BLAZE_BURNER::get)
				.itemIcon(AllBlocks.BLAZE_BURNER.get())
				.emptyBackground(177, 53)
				.build("liquid_burning", LiquidBurningCategory::new);

		ALL.forEach(registration::addRecipeCategories);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ingredientManager = registration.getIngredientManager();
		ALL.forEach(c -> c.registerRecipes(registration));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		ALL.forEach(c -> c.registerCatalysts(registration));

		registration.getJeiHelpers().getRecipeType(ResourceLocation.fromNamespaceAndPath("create", "sandpaper_polishing")).ifPresent(type -> {
			registration.addRecipeCatalyst(new ItemStack(CAItems.DIAMOND_GRIT_SANDPAPER.get()), type);
		});
		//registration.addRecipeCatalyst(new ItemStack(CAItems.DIAMOND_GRIT_SANDPAPER.get()), new ResourceLocation(Create.ID, "deploying"));
	}

	private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
		return new CategoryBuilder<>(recipeClass);
	}

	private static class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
		public CategoryBuilder(Class<? extends T> recipeClass) {
			super(recipeClass);
		}

		@Override
		public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
			CreateRecipeCategory<T> category = super.build(id, factory);
			ALL.add(category);
			return category;
		}

		@Override
		public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
			return build(CreateAddition.asResource(name), factory);
		}
	}
}