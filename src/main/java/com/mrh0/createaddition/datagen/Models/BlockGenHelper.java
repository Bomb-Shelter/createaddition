package com.mrh0.createaddition.datagen.Models;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import io.github.fabricators_of_create.porting_lib.models.generators.BlockModelProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockGenHelper {
    public static NonNullBiConsumer<DataGenContext<Block, ElectricMotorBlock>, RegistrateBlockstateProvider> directionalBlockState(ResourceLocation resourceLocation) {
        return (ctx, prov) -> directionalModel(resourceLocation, ctx, prov);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockState() {
        return (ctx, prov) -> directionalModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov);
    }

    public static NonNullBiConsumer<DataGenContext<Block, ElectricMotorBlock>, RegistrateBlockstateProvider> horizontalBlockState(ResourceLocation resourceLocation) {
        return (ctx, prov) -> horizontalModel(resourceLocation, ctx, prov, 0);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalBlockState() {
        return (ctx, prov) -> horizontalModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov, 90);
    }

    public static <T extends Block> void directionalModel(ResourceLocation resourceLocation, DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockModelProvider models = prov.models();
        ModelFile.ExistingModelFile blockModel = models.getExistingFile(resourceLocation);
        prov.getVariantBuilder(ctx.get())
                .forAllStatesExcept(state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationX(dir == Direction.DOWN ? 270
                            : dir.getAxis()
                            .isHorizontal() ? 0 : 90)
                    .rotationY(dir.getAxis()
                            .isVertical() ? 90 : (((int) dir.toYRot()) + 360) % 360)
                    .build();
        }, BlockStateProperties.WATERLOGGED, BlockStateProperties.POWERED);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleBlock(ResourceLocation resourceLocation) {
        return (ctx, prov) -> simpleBlockModel(resourceLocation, ctx, prov);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleBlock() {
        return (ctx, prov) -> simpleBlockModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov);
    }

    public static <T extends Block>  void horizontalModel(ResourceLocation resourceLocation, DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, int angleOffset) {
        BlockModelProvider models = prov.models();
        ModelFile.ExistingModelFile blockModel = models.getExistingFile(resourceLocation);
        prov.getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(blockModel)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360).build()
                );
    }

    public static <T extends Block> void simpleBlockModel(ResourceLocation resourceLocation, DataGenContext<Block,T> ctx, RegistrateBlockstateProvider prov) {
        ModelFile.ExistingModelFile blockModel = prov.models().getExistingFile(resourceLocation);
        prov.simpleBlock(ctx.get(),blockModel);
    }

    public static String getBlockName(Block block) {
        return  BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
