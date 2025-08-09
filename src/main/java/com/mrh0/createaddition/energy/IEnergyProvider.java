package com.mrh0.createaddition.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public interface IEnergyProvider {
    EnergyStorage getEnergyStorage(@Nullable Direction direction);
}
