package com.mrh0.createaddition.blocks.rolling_mill;

import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.infrastructure.fabric.transfer.CreateTransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import io.github.fabricators_of_create.porting_lib.transfer.item.wrapper.CombinedInventoryStorage;
import net.createmod.catnip.math.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.lookup.block.ServerWorldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RollingMillBlockEntity extends KineticBlockEntity {
	public ItemStackHandler inputInv;
	public ItemStackHandler outputInv;
	public Storage<ItemVariant> capability;
	public int timer;
	private RollingRecipe lastRecipe;

	public RollingMillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inputInv = new ItemStackHandler(1);
		outputInv = new ItemStackHandler(9);
		capability = new MillstoneInventoryHandler();
	}

	public static void registerCapabilities() {
		ItemStorage.SIDED.registerForBlockEntity(
			(be, context) -> be.capability,
			CABlockEntities.ROLLING_MILL.get()
		);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
		super.addBehaviours(behaviours);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void tickAudio() {
		super.tickAudio();

		if (getSpeed() == 0)
			return;
		if (inputInv.getStackInSlot(0)
				.isEmpty())
			return;

		float pitch = Mth.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
		SoundScapes.play(SoundScapes.AmbienceGroup.MILLING, worldPosition, pitch);
	}

	@Override
	public void tick() {
		super.tick();

		if (getSpeed() == 0)
			return;
		for (int i = 0; i < outputInv.getSlotCount(); i++)
			if (outputInv.getStackInSlot(i)
					.getCount() == outputInv.getSlotLimit(i))
				return;

		if (timer > 0) {
			timer -= getProcessingSpeed();

			if (level.isClientSide) {
				spawnParticles();
				return;
			}
			if (timer <= 0)
				process();
			return;
		}

		if (inputInv.getStackInSlot(0)
				.isEmpty())
			return;

		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RecipeHolder<RollingRecipe>> recipe = find(inventoryIn, level);
			if (recipe.isEmpty()) {
				timer = 100;
				sendData();
			} else {
				lastRecipe = recipe.get().value();
				timer = CommonConfig.ROLLING_MILL_PROCESSING_DURATION.get();
				sendData();
			}
			return;
		}

		timer = CommonConfig.ROLLING_MILL_PROCESSING_DURATION.get();
		sendData();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (this.getLevel() instanceof ServerWorldCache worldCache)
			worldCache.fabric_invalidateCache(this.getBlockPos());
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inputInv);
		ItemHelper.dropContents(level, worldPosition, outputInv);
	}

	private void process() {
		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RecipeHolder<RollingRecipe>> recipe = find(inventoryIn, level);
			if (recipe.isEmpty())
				return;
			lastRecipe = recipe.get().value();
		}

		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		stackInSlot.shrink(1);
		inputInv.setStackInSlot(0, stackInSlot);
		CreateTransferUtil.insertItemStacked(outputInv, lastRecipe.getResultStack().copy(), false);

		sendData();
		setChanged();
	}

	public void spawnParticles() {
		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		if (stackInSlot.isEmpty())
			return;

		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.5f);
		offset = VecHelper.rotate(offset, angle, Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT(registries));
		compound.put("OutputInventory", outputInv.serializeNBT(registries));
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(registries, compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
		super.read(compound, registries, clientPacket);
	}

	public int getProcessingSpeed() {
		return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

	private boolean canProcess(ItemStack stack) {
		ItemStackHandler tester = new ItemStackHandler(1);
		tester.setStackInSlot(0, stack);
		RecipeWrapper inventoryIn = new RecipeWrapper(tester);

		if (lastRecipe != null && lastRecipe.matches(inventoryIn, level))
			return true;
		return find(inventoryIn, level).isPresent();
	}

	private class MillstoneInventoryHandler extends CombinedInventoryStorage {

		public MillstoneInventoryHandler() {
			super(inputInv, outputInv);
		}

		@Override
		public boolean isItemValid(int slot, ItemVariant resource, int count) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return canProcess(resource.toStack(count)) && super.isItemValid(slot, resource, count);
		}

		@Override
		public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return 0;
			if (!isItemValid(slot, resource, TransferUtil.truncateLong(maxAmount)))
				return 0;
			return super.insertSlot(slot, resource, maxAmount, transaction);
		}

		@Override
		public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return 0;
			return super.extractSlot(slot, resource, maxAmount, transaction);
		}

	}

	public Optional<RecipeHolder<RollingRecipe>> find(RecipeWrapper inv, Level level) {
		var recipe = level.getRecipeManager().getRecipeFor(CARecipes.ROLLING_TYPE.get(), inv, level);
		if(recipe.isPresent()) {
			return recipe;
		}
		return level.getRecipeManager().getRecipeFor(CARecipes.ROLLING_TYPE.get(), inv, level);
	}

}