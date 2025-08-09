package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import com.mrh0.createaddition.blocks.connector.base.ConnectorMode;
import com.mrh0.createaddition.blocks.connector.base.ConnectorVariant;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.BlockModelProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.MultiPartBlockStateBuilder;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallConnectorBlock extends AbstractConnectorBlock<SmallConnectorBlockEntity> {
    public static final VoxelShaper CONNECTOR_SHAPE = CAShapes.shape(6, 0, 6, 10, 5, 10).forDirectional();
    public SmallConnectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<SmallConnectorBlockEntity> getBlockEntityClass() {
        return SmallConnectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmallConnectorBlockEntity> getBlockEntityType() {
        return CABlockEntities.SMALL_CONNECTOR.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CABlockEntities.SMALL_CONNECTOR.create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CONNECTOR_SHAPE.get(state.getValue(FACING).getOpposite());
    }

    public static void makeBlockState(DataGenContext<Block, SmallConnectorBlock> ctx, RegistrateBlockstateProvider provider) {
        BlockModelProvider models = provider.models();
        String basePath = "block/connector/";
        String basePathSmall = basePath + "small/";
        MultiPartBlockStateBuilder builder = provider.getMultipartBuilder(ctx.get());

        ModelFile.ExistingModelFile noneModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "none"));
        ModelFile.ExistingModelFile pushModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "push"));
        ModelFile.ExistingModelFile pullModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "pull"));
        ModelFile.ExistingModelFile girderBaseModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePath + "girder_base"));

        for(Direction direction: Direction.values()) {
            int horizontalAngle = direction == Direction.UP ? 180 : direction == Direction.DOWN ? 0 : 90;
            int  verticalAngle = ((int) direction.toYRot() + (direction.getAxis()
                    .isVertical() ? 180 : 0))% 360;
            builder.part()
                    .modelFile(noneModel)
                    .rotationX(horizontalAngle)
                    .rotationY(verticalAngle)
                    .addModel()
                    .condition(FACING, direction)
                    .condition(MODE, ConnectorMode.None)
                    .end()
                    .part()
                    .modelFile(pushModel)
                    .rotationX(horizontalAngle)
                    .rotationY(verticalAngle)
                    .addModel()
                    .condition(FACING, direction)
                    .condition(MODE, ConnectorMode.Push)
                    .end()
                    .part()
                    .modelFile(pullModel)
                    .rotationX(horizontalAngle)
                    .rotationY(verticalAngle)
                    .addModel()
                    .condition(FACING, direction)
                    .condition(MODE, ConnectorMode.Pull)
                    .end();

            builder.part()
                    .modelFile(girderBaseModel)
                    .rotationX(horizontalAngle)
                    .rotationY(verticalAngle)
                    .addModel()
                    .condition(FACING, direction)
                    .condition(VARIANT, ConnectorVariant.Girder)
                    .end();

        }


    }
}
