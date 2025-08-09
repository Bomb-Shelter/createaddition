package com.mrh0.createaddition.index;

import com.mojang.serialization.MapCodec;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
//import com.mrh0.createaddition.recipe.charging.DeoxidizingRecipe;
import com.mrh0.createaddition.recipe.conditions.HasFluidTagCondition;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.resources.conditions.PortingLibConditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class CARecipes {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateAddition.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CreateAddition.MODID);
	public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(PortingLibConditions.CONDITION_SERIALIZERS, CreateAddition.MODID);

	private static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String id) {
		return RECIPE_TYPES.register(id, () -> new RecipeType<T>() {
			public String toString() { return id; }
		});
	}

	public static final Supplier<RecipeType<RollingRecipe>> ROLLING_TYPE = registerRecipeType("rolling");
	public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RollingRecipe>> ROLLING = SERIALIZERS.register("rolling", () -> new StandardProcessingRecipe.Serializer<>(RollingRecipe::new));

	public static final Supplier<RecipeType<ChargingRecipe>> CHARGING_TYPE = registerRecipeType("charging");
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChargingRecipe>> CHARGING = SERIALIZERS.register("charging", () -> new ChargingRecipe.Serializer<>(ChargingRecipe::new));
	//public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DeoxidizingRecipe>> DEOXIDIZING = SERIALIZERS.register("deoxidizing", DeoxidizingRecipe.Serializer::new);

	public static final Supplier<RecipeType<LiquidBurningRecipe>> LIQUID_BURNING_TYPE = registerRecipeType("liquid_burning");
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LiquidBurningRecipe>> LIQUID_BURNING = SERIALIZERS.register("liquid_burning", () -> new LiquidBurningRecipe.Serializer<>(LiquidBurningRecipe::new));

	public static final Supplier<MapCodec<HasFluidTagCondition>> HAS_FLUID_TAG_CONDITION =
			CONDITION_CODECS.register("has_fluid_tag", () -> HasFluidTagCondition.CODEC);

    public static void register() {
    	SERIALIZERS.register();
		RECIPE_TYPES.register();

		CONDITION_CODECS.register();
    }
}
