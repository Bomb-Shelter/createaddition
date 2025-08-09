package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import com.mrh0.createaddition.blocks.connector.base.ConnectorMode;
import com.mrh0.createaddition.blocks.connector.base.ConnectorVariant;
import com.mrh0.createaddition.energy.NodeRotation;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.models.generators.BlockModelProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.MultiPartBlockStateBuilder;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallLightConnectorBlock extends AbstractConnectorBlock<SmallLightConnectorBlockEntity> implements LightEmissiveBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final VoxelShaper CONNECTOR_SHAPE = CAShapes.shape(6, 0, 6, 10, 5, 10).add(5, 4, 5, 11, 10, 11).forDirectional();
    public SmallLightConnectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return super.getStateForPlacement(c).setValue(POWERED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, NodeRotation.ROTATION, VARIANT, POWERED);
    }

    @Override
    public Class<SmallLightConnectorBlockEntity> getBlockEntityClass() {
        return SmallLightConnectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmallLightConnectorBlockEntity> getBlockEntityType() {
        return CABlockEntities.SMALL_LIGHT_CONNECTOR.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CABlockEntities.SMALL_LIGHT_CONNECTOR.create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CONNECTOR_SHAPE.get(state.getValue(FACING).getOpposite());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    public static void makeBlockState(DataGenContext<Block, SmallLightConnectorBlock> ctx, RegistrateBlockstateProvider provider) {
        BlockModelProvider models = provider.models();
        String basePath = "block/connector/";
        String basePathSmall = basePath + "small_light/";
        MultiPartBlockStateBuilder builder = provider.getMultipartBuilder(ctx.get());

        ModelFile.ExistingModelFile noneModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "none"));
        ModelFile.ExistingModelFile pushModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "push"));
        ModelFile.ExistingModelFile pullModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "pull"));
        ModelFile.ExistingModelFile noneOffModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "none_off"));
        ModelFile.ExistingModelFile pushOffModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "push_off"));
        ModelFile.ExistingModelFile pullOffModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePathSmall + "pull_off"));
        ModelFile.ExistingModelFile girderBaseModel = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePath + "girder_base"));

        for(Direction direction: Direction.values()) {
            int horizontalAngle = direction == Direction.UP ? 180 : direction == Direction.DOWN ? 0 : 90;
            int  verticalAngle = ((int) direction.toYRot() + (direction.getAxis()
                    .isVertical() ? 180 : 0))% 360;
            for (ConnectorMode connectorMode: ConnectorMode.values()) {
                builder.part()
                        .modelFile(noneModel)
                        .rotationX(horizontalAngle)
                        .rotationY(verticalAngle)
                        .addModel()
                        .condition(SmallLightConnectorBlock.FACING, direction)
                        .condition(SmallLightConnectorBlock.MODE, connectorMode)
                        .condition(SmallLightConnectorBlock.POWERED, Boolean.TRUE)
                        .end()
                        .part()
                        .modelFile(pushModel)
                        .rotationX(horizontalAngle)
                        .rotationY(verticalAngle)
                        .addModel()
                        .condition(SmallLightConnectorBlock.FACING, direction)
                        .condition(SmallLightConnectorBlock.MODE, connectorMode)
                        .condition(SmallLightConnectorBlock.POWERED, Boolean.TRUE)
                        .end()
                        .part()
                        .modelFile(pullModel)
                        .rotationX(horizontalAngle)
                        .rotationY(verticalAngle)
                        .addModel()
                        .condition(SmallLightConnectorBlock.FACING, direction)
                        .condition(SmallLightConnectorBlock.MODE, connectorMode)
                        .condition(SmallLightConnectorBlock.POWERED, Boolean.TRUE)
                        .end()
                        .part()
                        .modelFile(noneOffModel)
                        .rotationX(horizontalAngle)
                        .rotationY(verticalAngle)
                        .addModel()
                        .condition(SmallLightConnectorBlock.FACING, direction)
                        .condition(SmallLightConnectorBlock.MODE, connectorMode)
                        .condition(SmallLightConnectorBlock.POWERED, Boolean.FALSE)
                        .end()
                        .part()
                        .modelFile(pullOffModel)
                        .rotationX(horizontalAngle)
                        .rotationY(verticalAngle)
                        .addModel()
                        .condition(SmallLightConnectorBlock.FACING, direction)
                        .condition(SmallLightConnectorBlock.MODE, connectorMode)
                        .condition(SmallLightConnectorBlock.POWERED, Boolean.FALSE)
                        .end()
                        .part()
                        .modelFile(pushOffModel)
                        .rotationX(horizontalAngle)
                        .rotationY(verticalAngle)
                        .addModel()
                        .condition(SmallLightConnectorBlock.FACING, direction)
                        .condition(SmallLightConnectorBlock.MODE, connectorMode)
                        .condition(SmallLightConnectorBlock.POWERED, Boolean.FALSE)
                        .end();
            }

            builder.part()
                    .modelFile(girderBaseModel)
                    .rotationX(horizontalAngle)
                    .rotationY(verticalAngle)
                    .addModel()
                    .condition(SmallLightConnectorBlock.FACING, direction)
                    .condition(SmallLightConnectorBlock.VARIANT, ConnectorVariant.Girder)
                    .end();

        }
    }
}
