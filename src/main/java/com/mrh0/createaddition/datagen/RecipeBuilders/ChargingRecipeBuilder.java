package com.mrh0.createaddition.datagen.RecipeBuilders;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeParams;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Objects;
import java.util.Optional;

public class ChargingRecipeBuilder extends CARecipeBuilder {
    protected Ingredient ingredient;
    protected int energy , maxChargeRate;

    public ChargingRecipeBuilder(ItemStack result) {
        super(result);
        this.ingredient = Ingredient.EMPTY;
        this.energy = 0;
        this.maxChargeRate = 0;
    }

    public static ChargingRecipeBuilder charging(ItemStack itemStack) {
        return new ChargingRecipeBuilder(itemStack);
    }

    public static ChargingRecipeBuilder charging(ItemLike result, int count) {
        return charging(new ItemStack(result,count));
    }

    public static ChargingRecipeBuilder charging(ItemLike result) {
        return charging(new ItemStack(result));
    }

    public static ChargingRecipeBuilder charging(Item result, int count) {
        return charging(new ItemStack(result,count));
    }


    public static ChargingRecipeBuilder charging(Item result) {
        return charging(new ItemStack(result));
    }

    public static ChargingRecipeBuilder charging(ItemStack item, ResourceKey<Enchantment> enchantmentKey, HolderLookup.Provider provider) {
        item.enchant(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey),1);
        return charging(item);
    }

    public static ChargingRecipeBuilder deoxidize(Block block) {
        Optional<Block> deoxidizedBlock = WeatheringCopper.getPrevious(block);
        if (deoxidizedBlock.isEmpty()) CreateAddition.LOGGER.error("Cannot de-oxidize {}", block);
        return charging(deoxidizedBlock.get()).require(Ingredient.of(block)).energy(4000).maxChargeRate(200);
    }

    public static ChargingRecipeBuilder deoxidize(Block block, Block deoxidizedBlock) {
        return charging(deoxidizedBlock).require(Ingredient.of(block)).energy(4000).maxChargeRate(200);
    }

    public ChargingRecipeBuilder require(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public ChargingRecipeBuilder require(ItemLike item) {
        this.require(Ingredient.of(item));
        return this;
    }

    public ChargingRecipeBuilder require(ItemStack item) {
        this.require(Ingredient.of(item));
        return this;
    }

    public ChargingRecipeBuilder require(ItemLike item, int count) {
        for(int i=0; i< count; ++i) {
            this.require(Ingredient.of(item));
        }
        return this;
    }

    public ChargingRecipeBuilder energy(int energy) {
        this.energy = energy;
        return this;
    }

    public ChargingRecipeBuilder maxChargeRate(int maxChargeRate) {
        this.maxChargeRate = maxChargeRate;
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        ChargingRecipe chargingRecipe = new ChargingRecipe(
                new ChargingRecipeParams()
              /*  Objects.requireNonNullElse(this.group, ""),
                this.ingredient,
                this.result,
                this.energy,
                this.maxChargeRate*/
        );
        recipeOutput.accept(resourceLocation.withPrefix("charging/"), chargingRecipe, null);
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        save(recipeOutput, BuiltInRegistries.ITEM.getKey(this.result.getItem()).getPath());
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, id));
    }
}
