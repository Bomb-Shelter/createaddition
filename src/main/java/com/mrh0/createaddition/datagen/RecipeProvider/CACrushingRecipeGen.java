package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import io.github.fabricators_of_create.porting_lib.resources.conditions.NotCondition;
import io.github.fabricators_of_create.porting_lib.resources.conditions.TagEmptyCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CACrushingRecipeGen extends CrushingRecipeGen {
    public CACrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, CreateAddition.MODID);
    }

    GeneratedRecipe

    DIAMOND = create(CreateAddition.MODID,() -> Items.DIAMOND, b -> b.duration(300)
            .output(CAItems.DIAMOND_GRIT).withCondition(new NotCondition(new TagEmptyCondition(CATagRegister.Items.DIAMOND_DUSTS)))
    ),

    OCHRUM = mineralRecycling(AllPaletteStoneTypes.OCHRUM, b -> b.duration(250)
        .output(0.2f, AllItems.CRUSHED_GOLD)
        .output(0.2f, Items.GOLD_NUGGET)
        .output(0.2f, CAItems.ELECTRUM_NUGGET)
    ),
    TUFF = mineralRecycling(AllPaletteStoneTypes.TUFF, b -> b.duration(350)
        .output(0.25f, Items.FLINT)
        .output(0.1f, Items.GOLD_NUGGET)
        .output(0.1f, AllItems.COPPER_NUGGET)
        .output(0.1f, AllItems.ZINC_NUGGET)
        .output(0.1f, Items.IRON_NUGGET)
        .output(0.1f, CAItems.ELECTRUM_NUGGET)
    );
}
