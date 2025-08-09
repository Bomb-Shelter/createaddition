package com.mrh0.createaddition.datagen;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.RecipeProvider.*;
import com.mrh0.createaddition.datagen.TagProvider.CABlockTagProvider;
import com.mrh0.createaddition.datagen.TagProvider.CAFluidTagProvider;
import com.mrh0.createaddition.datagen.TagProvider.CAItemTagProvider;
import com.simibubi.create.api.registry.CreateRegistries;
import io.github.fabricators_of_create.porting_lib.data.DatapackBuiltinEntriesProvider;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class CreateAdditionsDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        System.out.println("gatherData HERE");
        FabricDataGenerator.Pack pack = generator.createPack();

        AtomicReference<FabricTagProvider.BlockTagProvider> blockTags = new AtomicReference<>();
        pack.addProvider((output, lookupProvider) -> {
            var tags = new CABlockTagProvider(output, lookupProvider);
            blockTags.set(tags);
            return tags;
        });
        pack.addProvider((output, lookupProvider) -> new CAFluidTagProvider(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CAItemTagProvider(output, lookupProvider, blockTags.get()));
        pack.addProvider((output, lookupProvider) -> new CACraftingRecipeProvider(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CACrushingRecipeGen(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CACompactingRecipeGen(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CAFillingRecipeGen(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CAMixingRecipeGen(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CAMechanicalCrafterRecipeGen(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CAPressingRecipeGen(output, lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CAChargingRecipeProvider(output,lookupProvider, CreateAddition.MODID));
        pack.addProvider((output, lookupProvider) -> new CARollingRecipeGen(output,lookupProvider));
        pack.addProvider((output, lookupProvider) -> new CALiquidBurningRecipeProvider(output,lookupProvider));

        pack.addProvider((output, lookupProvider) -> new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, CADamageTypesDatagen::bootstrap)
                .add(CreateRegistries.POTATO_PROJECTILE_TYPE, CAPotatoProjectileTypesDatagen::bootstrap),
                Set.of(CreateAddition.MODID)
        ));
        CreateAddition.REGISTRATE.onData(pack, ExistingFileHelper.withResourcesFromArg());
    }
}
