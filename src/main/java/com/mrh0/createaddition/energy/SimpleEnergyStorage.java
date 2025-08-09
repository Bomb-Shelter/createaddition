package com.mrh0.createaddition.energy;


import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

/**
 * A base energy storage implementation with fixed capacity, and per-operation insertion and extraction limits.
 * Make sure to override {@link #onFinalCommit} to call {@code markDirty} and similar functions.
 */
@SuppressWarnings({"unused"})
public class SimpleEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    public long energy;
    public long capacity;
    public final long maxReceive, maxExtract;

    public SimpleEnergyStorage(long capacity) {
        this(capacity, capacity, capacity, capacity);
    }

    public SimpleEnergyStorage(long capacity, long maxInsert, long maxExtract, long energy) {
        StoragePreconditions.notNegative(capacity);
        StoragePreconditions.notNegative(maxInsert);
        StoragePreconditions.notNegative(maxExtract);

        this.capacity = capacity;
        this.maxReceive = maxInsert;
        this.maxExtract = maxExtract;
        this.energy = energy;
    }

    @Override
    protected Long createSnapshot() {
        return energy;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        energy = snapshot;
    }

    @Override
    public boolean supportsInsertion() {
        return maxReceive > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(maxReceive, Math.min(maxAmount, capacity - energy));

        if (inserted > 0) {
            updateSnapshots(transaction);
            energy += inserted;
            return inserted;
        }

        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long extracted = Math.min(maxExtract, Math.min(maxAmount, energy));

        if (extracted > 0) {
            updateSnapshots(transaction);
            energy -= extracted;
            return extracted;
        }

        return 0;
    }

    @Override
    public long getAmount() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
}
