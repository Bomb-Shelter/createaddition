package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.alternator.AlternatorBlockEntity;
import com.mrh0.createaddition.blocks.connector.LargeConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.SmallConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.SmallLightConnectorBlockEntity;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyBlockEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlockEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlockEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlockEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlockEntity;
import com.mrh0.createaddition.compat.computercraft.Peripherals;

public class CACapabilities {
    public static void register() {
        AlternatorBlockEntity.registerCapabilities();
        LargeConnectorBlockEntity.registerCapabilities();
        SmallConnectorBlockEntity.registerCapabilities();
        SmallLightConnectorBlockEntity.registerCapabilities();
        ElectricMotorBlockEntity.registerCapabilities();
        CreativeEnergyBlockEntity.registerCapabilities();
        ModularAccumulatorBlockEntity.registerCapabilities();
        PortableEnergyInterfaceBlockEntity.registerCapabilities();
        TeslaCoilBlockEntity.registerCapabilities();
        RollingMillBlockEntity.registerCapabilities();
        LiquidBlazeBurnerBlockEntity.registerCapability();

        if(CreateAddition.CC_ACTIVE) {
            Peripherals.registerPeripheralCapabilities();
        }
    }
}
