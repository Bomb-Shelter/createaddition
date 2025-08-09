package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.energy.CreativeEnergyStorage;

import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;

import java.util.EnumMap;
import java.util.EnumSet;

public class CreativeEnergyBlockEntity extends CrateBlockEntity {

	protected final CreativeEnergyStorage capability;

	private final EnumSet<Direction> invalidSides = EnumSet.allOf(Direction.class);
	private final EnumMap<Direction, BlockApiCache<EnergyStorage, Direction>> cache = new EnumMap<>(Direction.class);
	
	public CreativeEnergyBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		capability = new CreativeEnergyStorage();
	}

	public static void registerCapabilities() {
		EnergyStorage.SIDED.registerForBlockEntity(
			(be, context) -> be.capability,
			CABlockEntities.CREATIVE_ENERGY.get()
		);
	}
	
	private boolean firstTickState = true;
	
	@Override
	public void tick() {
		super.tick();
		if (level == null) return;
		if (level.isClientSide()) return;
		if (firstTickState) firstTick();
		firstTickState = false;
		
		for (Direction d : Direction.values()) {
			EnergyStorage ies = cache.get(d).find(d);
			if (ies == null) continue;
			try (Transaction transaction = TransferUtil.getTransaction()) {
				ies.insert(Integer.MAX_VALUE, transaction);
				transaction.commit();
			}
		}
	}
	
	public void firstTick() {
		updateCache();
	}
	
	public void updateCache() {
		if (level == null) return;
		if (level.isClientSide()) return;
		for (Direction side : Direction.values()) {
			cache.put(side, BlockApiCache.create(
				EnergyStorage.SIDED,
				(ServerLevel) level,
				getBlockPos().relative(side)
				//side.getOpposite(),
				//() -> !this.isRemoved(),
				//() -> invalidSides.add(side)
			));
		}
	}
}
