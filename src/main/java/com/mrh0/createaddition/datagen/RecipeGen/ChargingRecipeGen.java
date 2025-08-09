package com.mrh0.createaddition.datagen.RecipeGen;

import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeParams;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mrh0.createaddition.recipe.charging.ChargingRecipe.TYPE_INFO;

public class ChargingRecipeGen extends ProcessingRecipeGen<ChargingRecipeParams, ChargingRecipe, ChargingRecipe.Builder<ChargingRecipe>> {
    protected final HolderLookup.Provider provider;

    public ChargingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
        try {
            this.provider = registries.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public GeneratedRecipe deoxidize(Block block, Block deoxidizedBlock) {
        @SuppressWarnings("deprecation")
        var id = block.builtInRegistryHolder().key().location();
        return create("deoxidize_" + id.getNamespace() + "_" + id.getPath(), (b) -> b.require(block)
                .output(deoxidizedBlock)
                .energy(4000)
                .maxChargeRate(200)
        );
    }

    public GeneratedRecipe deoxidize(Block block) {
        Optional<Block> deoxidizedBlock = WeatheringCopper.getPrevious(block);
        if (deoxidizedBlock.isEmpty()) throw new IllegalStateException("Cannot de-oxidize " + block);
        return deoxidize(block, deoxidizedBlock.get());
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return TYPE_INFO;
    }

    @Override
    protected ChargingRecipe.Builder<ChargingRecipe> getBuilder(ResourceLocation id) {
        return new ChargingRecipe.Builder<>(ChargingRecipe::new, id);
    }
}
