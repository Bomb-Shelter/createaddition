package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.SimpleEnergyStorage;
import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionSuccessCallback;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.lookup.block.ServerWorldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;

public class PortableEnergyInterfaceBlockEntity extends PortableStorageInterfaceBlockEntity {

	protected EnergyStorage capability;
	//protected LazyOptional<PortableEnergyInterfacePeripheral> peripheral;

	public PortableEnergyInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		capability = this.createEmptyHandler();

		//if (CreateAddition.CC_ACTIVE)
		//	this.peripheral = LazyOptional.of(() -> Peripherals.createPortableEnergyInterfacePeripheral(this));
	}

	public static void registerCapabilities() {
		EnergyStorage.SIDED.registerForBlockEntity(
			(be, context) -> be.capability,
			CABlockEntities.PORTABLE_ENERGY_INTERFACE.get()
		);
	}

	public void startTransferringTo(Contraption contraption, float distance) {
		EnergyStorage oldcap = this.capability;
		invalidate();
		this.capability =  new InterfaceEnergyHandler(PortableEnergyManager.get(contraption));
		//oldcap.invalidate();
		super.startTransferringTo(contraption, distance);
	}

	@Override
	protected void invalidateCapability() {
		if (level == null) return;
		if (level instanceof ServerWorldCache worldCache)
			worldCache.fabric_invalidateCache(getBlockPos());
		// this.capability.invalidate();
	}

	@Override
	protected void stopTransferring() {
		EnergyStorage oldcap = this.capability;
		invalidate();
		this.capability = this.createEmptyHandler();
		//oldcap.invalidate();
		super.stopTransferring();
	}

	private EnergyStorage createEmptyHandler() {
		return new InterfaceEnergyHandler(new SimpleEnergyStorage(0));
	}

	// Implement protected methods.

	public boolean isConnected() {
		int timeUnit = this.getTransferTimeout();
		return this.transferTimer >= 4 && this.transferTimer <= timeUnit + 4;
	}

	protected float getExtensionDistance(float partialTicks) {
		return (float)(Math.pow(this.connectionAnimation.getValue(partialTicks), 2.0D) * (double)this.distance / 2.0D);
	}

	protected float getConnectionDistance() {
		return this.distance;
	}

	protected Entity getConnectedEntity() {
		return this.connectedEntity;
	}

	protected int getTransferTimer() {
		return this.transferTimer;
	}

	// CC

	public long getEnergy() {
		return this.capability.getAmount();
	}

	public long getCapacity() {
		return this.capability.getCapacity();
	}

	public class InterfaceEnergyHandler implements EnergyStorage {

		private final EnergyStorage wrapped;

		public InterfaceEnergyHandler(EnergyStorage wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public long insert(long maxReceive, TransactionContext transaction) {
			if (!PortableEnergyInterfaceBlockEntity.this.canTransfer()) return 0;
			maxReceive = Math.min(maxReceive, CommonConfig.PEI_MAX_INPUT.get());
			if (this.wrapped == null) return 0;
			long received = this.wrapped.insert(maxReceive, transaction);
			if (received != 0)
				TransactionSuccessCallback.onSuccess(transaction, this::keepAlive);
			return received;
		}

		@Override
		public long extract(long maxExtract, TransactionContext transaction) {
			if (!PortableEnergyInterfaceBlockEntity.this.canTransfer()) return 0;
			maxExtract = Math.min(maxExtract, CommonConfig.PEI_MAX_OUTPUT.get());
			if (this.wrapped == null) return 0;
			long extracted = this.wrapped.extract(maxExtract, transaction);
			if (extracted != 0)
				TransactionSuccessCallback.onSuccess(transaction, this::keepAlive);
			return extracted;
		}

		@Override
		public long getAmount() {
			if (this.wrapped == null) return 0;
			return this.wrapped.getAmount();
		}

		@Override
		public long getCapacity() {
			if (this.wrapped == null) return 0;
			return this.wrapped.getCapacity();
		}

		@Override
		public boolean supportsExtraction() {
			return true;
		}

		@Override
		public boolean supportsInsertion() {
			return true;
		}

		public void keepAlive() {
			PortableEnergyInterfaceBlockEntity.this.onContentTransferred();
		}
	}
}
