package com.mrh0.createaddition.energy;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

public class InternalEnergyStorage extends SimpleEnergyStorage {
	public InternalEnergyStorage(long capacity) {
        super(capacity, capacity, capacity, 0);
    }

    public InternalEnergyStorage(long capacity, long maxTransfer) {
        super(capacity, maxTransfer, maxTransfer, 0);
    }

    public InternalEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
    }

    public InternalEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }
    
    public CompoundTag write(CompoundTag nbt) {
    	nbt.putLong("energy", energy);
    	return nbt;
    }
    
    public void read(CompoundTag nbt) {
    	setEnergy(nbt.getInt("energy"));
    }
    
    public CompoundTag write(CompoundTag nbt, String name) {
    	nbt.putLong("energy_"+name, energy);
    	return nbt;
    }
    
    public void read(CompoundTag nbt, String name) {
    	setEnergy(nbt.getLong("energy_"+name));
    }
    
    public long getSpace() {
    	return Math.max(getCapacity() - getAmount(), 0);
    }

    @Override
    public boolean supportsExtraction() {
		return maxExtract > 0;
    }
    
    @Override
    public boolean supportsInsertion() {
    	return maxReceive > 0;
    }
    
    public long internalConsumeEnergy(long consume) {
    	long oenergy = energy;
        energy = Math.max(0, energy - consume);
        return oenergy - energy;
    }
    
    public long internalProduceEnergy(long produce) {
    	long oenergy = energy;
        energy = Math.min(capacity, energy + produce);
        return oenergy - energy;
    }
    
    public void setEnergy(long energy) {
    	this.energy = energy;
    }
    
    public void setCapacity(long capacity) {
    	this.capacity = capacity;
    }
    
    @Deprecated
    public void outputToSide(Level level, BlockPos pos, Direction side, long max) {
		EnergyStorage ies = EnergyStorage.SIDED.find(level, pos, side.getOpposite());
		if(ies == null) return;
        try (Transaction transaction = TransferUtil.getTransaction()) {
            long ext = this.extract(max, transaction);
            this.insert(ext - ies.insert(ext, transaction), transaction);
            transaction.commit();
        }
    }
    
    @Override
    public String toString() {
    	return getAmount() + "/" + getCapacity() + " <-" + maxExtract + " ->" + maxReceive;
    }
}