package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PortableEnergyInterfaceMovement implements MovementBehaviour {

	static final String _workingPos_ = "WorkingPos";
	static final String _clientPrevPos_ = "ClientPrevPos";

	@Override
	public Vec3 getActiveAreaOffset(MovementContext context) {
		return Vec3.atLowerCornerOf(context.state.getValue(PortableEnergyInterfaceBlock.FACING).getNormal()).scale(1.850000023841858D);
	}

	//@Override
	//public boolean hasSpecialInstancedRendering() {
	//	return true;
	//}

	@Override
	@Environment(EnvType.CLIENT)
	public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
		if (!VisualizationManager.supportsVisualization(context.world))
			PortableEnergyInterfaceRenderer.renderInContraption(context, renderWorld, matrices, buffer);
	}

	@Override
	@Nullable
	public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
		return new PEIActorVisual(visualizationContext, simulationWorld, movementContext);
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		boolean onCarriage = context.contraption instanceof CarriageContraption;
		if (!onCarriage || !(context.motion.length() > 0.25D)) {
			if (!this.findInterface(context, pos)) context.data.remove(_workingPos_);
		}
	}

	@Override
	public void tick(MovementContext context) {
		if (context.world.isClientSide) {
			getAnimation(context).tickChaser();
		}

		boolean onCarriage = context.contraption instanceof CarriageContraption;
		if (!onCarriage || !(context.motion.length() > 0.25D)) {
			BlockPos pos;
			if (context.world.isClientSide) {
				pos = BlockPos.containing(context.position);
				if (!this.findInterface(context, pos)) this.reset(context);

			} else if (context.data.contains(_workingPos_)) {
				pos = NbtUtils.readBlockPos(context.data, _workingPos_).orElse(BlockPos.ZERO); // Should never orElse
				Vec3 target = VecHelper.getCenterOf(pos);
				if (!context.stall && !onCarriage && context.position.closerThan(target, target.distanceTo(context.position.add(context.motion)))) {
					context.stall = true;
				}

				Optional<Direction> currentFacingIfValid = this.getCurrentFacingIfValid(context);
				if (currentFacingIfValid.isPresent()) {
					PortableEnergyInterfaceBlockEntity stationaryInterface = this.getStationaryInterfaceAt(context.world, pos, context.state, currentFacingIfValid.get());
					if (stationaryInterface == null) {
						this.reset(context);
					} else {
						if (stationaryInterface.getConnectedEntity() == null) {
							stationaryInterface.startTransferringTo(context.contraption, stationaryInterface.getConnectionDistance());
						}
						boolean timerBelow = stationaryInterface.getTransferTimer() <= 4;
						stationaryInterface.keepAlive = 2;
						if (context.stall && timerBelow) context.stall = false;
					}
				}
			}
		}
	}

	protected boolean findInterface(MovementContext context, BlockPos pos) {
		Contraption var4 = context.contraption;
		if (var4 instanceof CarriageContraption cc) {
            if (!cc.notInPortal()) return false;
		}

		Optional<Direction> currentFacingIfValid = this.getCurrentFacingIfValid(context);
		if (currentFacingIfValid.isEmpty()) return false;
		else {
			Direction currentFacing = currentFacingIfValid.get();
			PortableEnergyInterfaceBlockEntity psi = this.findStationaryInterface(context.world, pos, context.state, currentFacing);
			if (psi == null) return false;
			else if (psi.isPowered()) return false;
			else {
				context.data.put(_workingPos_, NbtUtils.writeBlockPos(psi.getBlockPos()));
				if (!context.world.isClientSide) {
					Vec3 diff = VecHelper.getCenterOf(psi.getBlockPos()).subtract(context.position);
					diff = VecHelper.project(diff, Vec3.atLowerCornerOf(currentFacing.getNormal()));
					float distance = (float)(diff.length() + 1.850000023841858D - 1.0D);
					psi.startTransferringTo(context.contraption, distance);
				} else {
					context.data.put(_clientPrevPos_, NbtUtils.writeBlockPos(pos));
					if (context.contraption instanceof CarriageContraption || context.contraption.entity.isStalled() || context.motion.lengthSqr() == 0.0D) {
						getAnimation(context).chase(psi.getConnectionDistance() / 2.0F, 0.25D, LerpedFloat.Chaser.LINEAR);
					}
				}

				return true;
			}
		}
	}

    @Override
	public void cancelStall(MovementContext context) {
		this.reset(context);
	}

	public void reset(MovementContext context) {
		context.data.remove(_clientPrevPos_);
		context.data.remove(_workingPos_);
		context.stall = false;
		getAnimation(context).chase(0.0D, 0.25D, LerpedFloat.Chaser.LINEAR);
	}

	@Nullable
	private PortableEnergyInterfaceBlockEntity findStationaryInterface(Level world, BlockPos pos, BlockState state, Direction facing) {
		for(int i = 0; i < 2; ++i) {
			PortableEnergyInterfaceBlockEntity interfaceAt = this.getStationaryInterfaceAt(world, pos.relative(facing, i), state, facing);
			if (interfaceAt != null) return interfaceAt;
		}
		return null;
	}

	@Nullable
	private PortableEnergyInterfaceBlockEntity getStationaryInterfaceAt(Level world, BlockPos pos, BlockState state, Direction facing) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof PortableEnergyInterfaceBlockEntity psi) {
            BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() != state.getBlock()) return null;
			else if (blockState.getValue(PortableEnergyInterfaceBlock.FACING) != facing.getOpposite()) return null;
			else return psi.isPowered() ? null : psi;
		} else return null;
	}

	private Optional<Direction> getCurrentFacingIfValid(MovementContext context) {
		Vec3 directionVec = Vec3.atLowerCornerOf(context.state.getValue(PortableEnergyInterfaceBlock.FACING).getNormal());
		directionVec = context.rotation.apply(directionVec);
		Direction facingFromVector = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z);
		return directionVec.distanceTo(Vec3.atLowerCornerOf(facingFromVector.getNormal())) > 0.5D ? Optional.empty() : Optional.of(facingFromVector);
	}

	public static LerpedFloat getAnimation(MovementContext context) {
		Object var2 = context.temporaryData;
		if (var2 instanceof LerpedFloat lf) {
			return lf;
		} else {
			LerpedFloat nlf = LerpedFloat.linear();
			context.temporaryData = nlf;
			return nlf;
		}
	}

}
